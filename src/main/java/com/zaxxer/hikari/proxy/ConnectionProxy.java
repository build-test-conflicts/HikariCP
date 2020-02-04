package com.zaxxer.hikari.proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import com.zaxxer.hikari.HikariPool;
import com.zaxxer.hikari.util.FastStatementList;
/** 
 * This is the proxy class for java.sql.Connection.
 * @author Brett Wooldridge
 */
public abstract class ConnectionProxy implements IHikariConnectionProxy {
  private static Set<String> SQL_ERRORS;
  Connection delegate;
  private FastStatementList openStatements;
  private HikariPool parentPool;
  private int defaultIsolationLevel;
  private boolean isClosed;
  private boolean forceClose;
  private boolean isTransactionIsolationDirty;
  private long creationTime;
  private volatile long lastAccess;
  private StackTraceElement[] leakTrace;
  private TimerTask leakTask;
static {
    SQL_ERRORS=new HashSet<String>();
    SQL_ERRORS.add("57P01");
    SQL_ERRORS.add("57P02");
    SQL_ERRORS.add("57P03");
    SQL_ERRORS.add("57P02");
    SQL_ERRORS.add("01002");
  }
  protected ConnectionProxy(  HikariPool pool,  Connection connection,  int defaultIsolationLevel){
    this.parentPool=pool;
    this.delegate=connection;
    this.defaultIsolationLevel=defaultIsolationLevel;
    creationTime=lastAccess=System.currentTimeMillis();
    openStatements=new FastStatementList();
  }
  public final void untrackStatement(  Object statement){
    if (!isClosed) {
      openStatements.remove(statement);
    }
  }
  public final long getCreationTime(){
    return creationTime;
  }
  public final long getLastAccess(){
    return lastAccess;
  }
  public final void unclose(){
    isClosed=false;
  }
  public final void realClose() throws SQLException {
    delegate.close();
  }
  public final void captureStack(  long leakDetectionThreshold,  Timer scheduler){
    StackTraceElement[] trace=Thread.currentThread().getStackTrace();
    leakTrace=new StackTraceElement[trace.length - 4];
    System.arraycopy(trace,4,leakTrace,0,leakTrace.length);
    leakTask=new LeakTask(leakTrace,leakDetectionThreshold);
    scheduler.schedule(leakTask,leakDetectionThreshold);
  }
  public final boolean isTransactionIsolationDirty(){
    return isTransactionIsolationDirty;
  }
  public void resetTransactionIsolationDirty(){
    isTransactionIsolationDirty=false;
  }
  public final boolean isBrokenConnection(){
    return forceClose;
  }
  public final void checkException(  SQLException sqle){
    String sqlState=sqle.getSQLState();
    if (sqlState != null) {
      forceClose|=sqlState.startsWith("08") | SQL_ERRORS.contains(sqlState);
    }
  }
  protected final void checkClosed() throws SQLException {
    if (isClosed) {
      throw new SQLException("Connection is closed");
    }
  }
  private final <T extends Statement>T trackStatement(  T statement){
    openStatements.add(statement);
    return statement;
  }
  /** 
 * {@inheritDoc} 
 */
  public void close() throws SQLException {
    if (!isClosed) {
      isClosed=true;
      if (leakTask != null) {
        leakTask.cancel();
        leakTask=null;
      }
      try {
        final int size=openStatements.size();
        for (int i=0; i < size; i++) {
          try {
            openStatements.get(i).close();
          }
 catch (          SQLException e) {
            checkException(e);
          }
        }
        if (!delegate.getAutoCommit()) {
          delegate.rollback();
        }
      }
 catch (      SQLException e) {
        checkException(e);
        throw e;
      }
 finally {
        openStatements.clear();
        lastAccess=System.currentTimeMillis();
        parentPool.releaseConnection(this);
      }
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean isClosed() throws SQLException {
    return isClosed;
  }
  /** 
 * {@inheritDoc} 
 */
  public Statement createStatement() throws SQLException {
    checkClosed();
    try {
      Statement proxyStatement=ProxyFactory.getProxyStatement(this,delegate.createStatement());
      return trackStatement(proxyStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public Statement createStatement(  int resultSetType,  int resultSetConcurrency) throws SQLException {
    checkClosed();
    try {
      Statement proxyStatement=ProxyFactory.getProxyStatement(this,delegate.createStatement(resultSetType,resultSetConcurrency));
      return trackStatement(proxyStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public Statement createStatement(  int resultSetType,  int resultSetConcurrency,  int resultSetHoldability) throws SQLException {
    checkClosed();
    try {
      Statement proxyStatement=ProxyFactory.getProxyStatement(this,delegate.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability));
      return trackStatement(proxyStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public CallableStatement prepareCall(  String sql) throws SQLException {
    checkClosed();
    try {
      CallableStatement proxyCallableStatement=ProxyFactory.getProxyCallableStatement(this,delegate.prepareCall(sql));
      return trackStatement(proxyCallableStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public CallableStatement prepareCall(  String sql,  int resultSetType,  int resultSetConcurrency) throws SQLException {
    checkClosed();
    try {
      CallableStatement proxyCallableStatement=ProxyFactory.getProxyCallableStatement(this,delegate.prepareCall(sql,resultSetType,resultSetConcurrency));
      return trackStatement(proxyCallableStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public CallableStatement prepareCall(  String sql,  int resultSetType,  int resultSetConcurrency,  int resultSetHoldability) throws SQLException {
    checkClosed();
    try {
      CallableStatement proxyCallableStatement=ProxyFactory.getProxyCallableStatement(this,delegate.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability));
      return trackStatement(proxyCallableStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql,  int autoGeneratedKeys) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql,autoGeneratedKeys));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql,  int resultSetType,  int resultSetConcurrency) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql,resultSetType,resultSetConcurrency));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql,  int resultSetType,  int resultSetConcurrency,  int resultSetHoldability) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql,  int[] columnIndexes) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql,columnIndexes));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public PreparedStatement prepareStatement(  String sql,  String[] columnNames) throws SQLException {
    checkClosed();
    try {
      PreparedStatement proxyPreparedStatement=ProxyFactory.getProxyPreparedStatement(this,delegate.prepareStatement(sql,columnNames));
      return trackStatement(proxyPreparedStatement);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean isValid(  int timeout) throws SQLException {
    if (isClosed) {
      return false;
    }
    try {
      return delegate.isValid(timeout);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public void setTransactionIsolation(  int level) throws SQLException {
    checkClosed();
    try {
      delegate.setTransactionIsolation(level);
      isTransactionIsolationDirty=(level != defaultIsolationLevel);
    }
 catch (    SQLException e) {
      checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean isWrapperFor(  Class<?> iface) throws SQLException {
    return iface.isInstance(delegate);
  }
  /** 
 * {@inheritDoc} 
 */
  @SuppressWarnings("unchecked") public <T>T unwrap(  Class<T> iface) throws SQLException {
    if (iface.isInstance(delegate)) {
      return (T)delegate;
    }
    throw new SQLException("Wrapped connection is not an instance of " + iface);
  }
  public ConnectionProxy(){
  }
}
