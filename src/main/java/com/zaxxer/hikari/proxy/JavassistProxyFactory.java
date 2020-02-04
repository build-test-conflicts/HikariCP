package com.zaxxer.hikari.proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.util.ClassLoaderUtils;
/** 
 * @author Brett Wooldridge
 */
public final class JavassistProxyFactory {
  private ClassPool classPool;
static {
    ClassLoader contextClassLoader=Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(JavassistProxyFactory.class.getClassLoader());
      JavassistProxyFactory proxyFactoryFactory=new JavassistProxyFactory();
      proxyFactoryFactory.modifyProxyFactory();
    }
 catch (    Exception e) {
      throw new RuntimeException(e);
    }
 finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }
  public static void initialize(){
  }
  public JavassistProxyFactory(){
    classPool=new ClassPool();
    classPool.importPackage("java.sql");
    classPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
    try {
      String methodBody="{ checkClosed(); try { return delegate.method($$); } catch (SQLException e) { checkException(e); throw e;} }";
      generateProxyClass(Connection.class,ConnectionProxy.class,methodBody);
      methodBody="{ try { return delegate.method($$); } catch (SQLException e) { checkException(e); throw e;} }";
      generateProxyClass(Statement.class,StatementProxy.class,methodBody);
      methodBody="{ try { return ((cast) delegate).method($$); } catch (SQLException e) { checkException(e); throw e;} }";
      generateProxyClass(PreparedStatement.class,PreparedStatementProxy.class,methodBody);
      generateProxyClass(CallableStatement.class,CallableStatementProxy.class,methodBody);
    }
 catch (    Exception e) {
      throw new RuntimeException(e);
    }
  }
  public void modifyProxyFactory() throws Exception {
    String packageName=JavassistProxyFactory.class.getPackage().getName();
    CtClass proxyCt=classPool.getCtClass("com.zaxxer.hikari.proxy.ProxyFactory");
    for (    CtMethod method : proxyCt.getMethods()) {
      StringBuilder call=new StringBuilder("{");
      if ("getProxyConnection".equals(method.getName())) {
        call.append("return new ").append(packageName).append(".ConnectionJavassistProxy($$);");
      }
 else       if ("getProxyStatement".equals(method.getName())) {
        call.append("return new ").append(packageName).append(".StatementJavassistProxy($$);");
      }
 else       if ("getProxyPreparedStatement".equals(method.getName())) {
        call.append("return new ").append(packageName).append(".PreparedStatementJavassistProxy($$);");
      }
 else       if ("getProxyCallableStatement".equals(method.getName())) {
        call.append("return new ").append(packageName).append(".CallableStatementJavassistProxy($$);");
      }
 else {
        continue;
      }
      call.append('}');
      method.setBody(call.toString());
    }
    proxyCt.toClass(classPool.getClassLoader(),null);
  }
  /** 
 * Generate Javassist Proxy Classes
 */
  @SuppressWarnings("unchecked") private <T>Class<T> generateProxyClass(  Class<T> primaryInterface,  Class<?> superClass,  String methodBody) throws Exception {
    String superClassName=superClass.getName();
    CtClass superClassCt=classPool.getCtClass(superClassName);
    CtClass targetCt=classPool.makeClass(superClassName.replace("Proxy","JavassistProxy"),superClassCt);
    targetCt.setModifiers(Modifier.FINAL);
    Set<String> superSigs=new HashSet<String>();
    for (    CtMethod method : superClassCt.getMethods()) {
      if ((method.getModifiers() & Modifier.ABSTRACT) != Modifier.ABSTRACT) {
        superSigs.add(method.getName() + method.getSignature());
      }
    }
    methodBody=methodBody.replace("cast",primaryInterface.getName());
    Set<String> methods=new HashSet<String>();
    Set<Class<?>> interfaces=ClassLoaderUtils.getAllInterfaces(primaryInterface);
    for (    Class<?> intf : interfaces) {
      CtClass intfCt=classPool.getCtClass(intf.getName());
      targetCt.addInterface(intfCt);
      for (      CtMethod intfMethod : intfCt.getDeclaredMethods()) {
        if (superSigs.contains(intfMethod.getName() + intfMethod.getSignature())) {
          continue;
        }
        if (methods.contains(intfMethod.getName() + intfMethod.getSignature())) {
          continue;
        }
        methods.add(intfMethod.getName() + intfMethod.getSignature());
        CtMethod method=CtNewMethod.copy(intfMethod,targetCt,null);
        String modifiedBody;
        if (isThrowsSqlException(intfMethod)) {
          modifiedBody=methodBody.replace("method",method.getName());
        }
 else {
          modifiedBody="return ((cast) delegate).method($$);".replace("method",method.getName()).replace("cast",primaryInterface.getName());
        }
        if (method.getReturnType() == CtClass.voidType) {
          modifiedBody=modifiedBody.replace("return","");
        }
        method.setBody(modifiedBody);
        targetCt.addMethod(method);
      }
    }
    if (LoggerFactory.getLogger(getClass()).isDebugEnabled()) {
      targetCt.debugWriteFile(System.getProperty("java.io.tmpdir"));
    }
    return targetCt.toClass(classPool.getClassLoader(),null);
  }
  private boolean isThrowsSqlException(  CtMethod method){
    try {
      for (      CtClass clazz : method.getExceptionTypes()) {
        if (clazz.getSimpleName().equals("SQLException")) {
          return true;
        }
      }
    }
 catch (    NotFoundException e) {
    }
    return false;
  }
}
