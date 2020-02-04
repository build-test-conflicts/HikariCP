package com.zaxxer.hikari.proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.pool.LeakTask;
import com.zaxxer.hikari.pool.PoolBagEntry;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.FastList;
/** 
 * This is the proxy class for java.sql.Connection.
 * @author Brett Wooldridge
 */
public abstract class ConnectionProxy implements IHikariConnectionProxy {
  private static Logger LOGGER;
  private static Set<String> SQL_ERRORS;
  private static ClockSource clockSource;
  Connection delegate;
  private LeakTask leakTask;
  private PoolBagEntry poolEntry;
  private FastList<Statement> openStatements;
  private long lastAccess;
  private boolean isCommitStateDirty;
  private boolean isConnectionStateDirty;
static {
    LOGGER=LoggerFactory.getLogger(ConnectionProxy.class);
    clockSource=ClockSource.INSTANCE;
    SQL_ERRORS=new HashSet<>();
    SQL_ERRORS.add("57P01");
    SQL_ERRORS.add("57P02");
    SQL_ERRORS.add("57P03");
    SQL_ERRORS.add("01002");
    SQL_ERRORS.add("JZ0C0");
    SQL_ERRORS.add("JZ0C1");
  }
  public ConnectionProxy(  final PoolBagEntry bagEntry,  final LeakTask leakTask,  final long now){
    this.poolEntry=bagEntry;
    this.leakTask=leakTask;
    this.lastAccess=now;
    this.delegate=bagEntry.connection;
    this.openStatements=bagEntry.openStatements;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public final String toString(){
    return new StringBuilder(64).append(this.getClass().getSimpleName()).append('@').append(System.identityHashCode(this)).append(" wrapping ").append(delegate).toString();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public final PoolBagEntry getPoolBagEntry(){
    return poolEntry;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public SQLException checkException(  final SQLException sqle){
    String sqlState=sqle.getSQLState();
    if (sqlState != null) {
      boolean isForceClose=sqlState.startsWith("08") || SQL_ERRORS.contains(sqlState);
      if (isForceClose) {
        poolEntry.evict=true;
        LOGGER.warn("{} - Connection {} marked as broken because of SQLSTATE({}), ErrorCode({})",poolEntry.parentPool,poolEntry,sqlState,sqle.getErrorCode(),sqle);
      }
 else {
        SQLException nse=sqle.getNextException();
        if (nse != null && nse != sqle) {
          checkException(nse);
        }
      }
    }
    return sqle;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void untrackStatement(  final Statement statement){
    openStatements.remove(statement);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void markCommitStateDirty(){
    isCommitStateDirty=true;
  }
  public <T extends Statement>T trackStatement(  final T statement){
    openStatements.add(statement);
    return statement;
  }
  private final void closeOpenStatements(){
    final int size=openStatements.size();
    if (size > 0) {
      for (int i=0; i < size; i++) {
        try {
          final Statement statement=openStatements.get(i);
          if (statement != null) {
            statement.close();
          }
        }
 catch (        SQLException e) {
          checkException(e);
        }
      }
      openStatements.clear();
    }
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void close() throws SQLException {
    if (delegate != ClosedConnection.CLOSED_CONNECTION) {
      leakTask.cancel();
      try {
        closeOpenStatements();
        if (isCommitStateDirty) {
          lastAccess=clockSource.currentTime();
          if (!poolEntry.isAutoCommit) {
            delegate.rollback();
            LOGGER.debug("{} - Executed rollback on connection {} due to dirty commit state on close().",poolEntry.parentPool,delegate);
          }
        }
        if (isConnectionStateDirty) {
          poolEntry.resetConnectionState();
          lastAccess=clockSource.currentTime();
        }
        delegate.clearWarnings();
      }
 catch (      SQLException e) {
        if (!poolEntry.evict) {
          throw checkException(e);
        }
      }
 finally {
        delegate=ClosedConnection.CLOSED_CONNECTION;
        poolEntry.releaseConnection(lastAccess);
      }
    }
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public boolean isClosed() throws SQLException {
    return (delegate == ClosedConnection.CLOSED_CONNECTION);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public Statement createStatement() throws SQLException {
    return ProxyFactory.getProxyStatement(this,trackStatement(delegate.createStatement()));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public Statement createStatement(  int resultSetType,  int concurrency) throws SQLException {
    return ProxyFactory.getProxyStatement(this,trackStatement(delegate.createStatement(resultSetType,concurrency)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public Statement createStatement(  int resultSetType,  int concurrency,  int holdability) throws SQLException {
    return ProxyFactory.getProxyStatement(this,trackStatement(delegate.createStatement(resultSetType,concurrency,holdability)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public CallableStatement prepareCall(  String sql) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this,trackStatement(delegate.prepareCall(sql)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public CallableStatement prepareCall(  String sql,  int resultSetType,  int concurrency) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this,trackStatement(delegate.prepareCall(sql,resultSetType,concurrency)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public CallableStatement prepareCall(  String sql,  int resultSetType,  int concurrency,  int holdability) throws SQLException {
    return ProxyFactory.getProxyCallableStatement(this,trackStatement(delegate.prepareCall(sql,resultSetType,concurrency,holdability)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql,  int autoGeneratedKeys) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql,autoGeneratedKeys)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql,  int resultSetType,  int concurrency) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql,resultSetType,concurrency)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql,  int resultSetType,  int concurrency,  int holdability) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql,resultSetType,concurrency,holdability)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql,  int[] columnIndexes) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql,columnIndexes)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public PreparedStatement prepareStatement(  String sql,  String[] columnNames) throws SQLException {
    return ProxyFactory.getProxyPreparedStatement(this,trackStatement(delegate.prepareStatement(sql,columnNames)));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void commit() throws SQLException {
    delegate.commit();
    isCommitStateDirty=false;
    lastAccess=clockSource.currentTime();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void rollback() throws SQLException {
    delegate.rollback();
    isCommitStateDirty=false;
    lastAccess=clockSource.currentTime();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void rollback(  Savepoint savepoint) throws SQLException {
    delegate.rollback(savepoint);
    isCommitStateDirty=false;
    lastAccess=clockSource.currentTime();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setAutoCommit(  boolean autoCommit) throws SQLException {
    delegate.setAutoCommit(autoCommit);
    poolEntry.setAutoCommit(autoCommit);
    isConnectionStateDirty=true;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setReadOnly(  boolean readOnly) throws SQLException {
    delegate.setReadOnly(readOnly);
    poolEntry.setReadOnly(readOnly);
    isConnectionStateDirty=true;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setTransactionIsolation(  int level) throws SQLException {
    delegate.setTransactionIsolation(level);
    poolEntry.setTransactionIsolation(level);
    isConnectionStateDirty=true;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setCatalog(  String catalog) throws SQLException {
    delegate.setCatalog(catalog);
    poolEntry.setCatalog(catalog);
    isConnectionStateDirty=true;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public void setNetworkTimeout(  Executor executor,  int milliseconds) throws SQLException {
    delegate.setNetworkTimeout(executor,milliseconds);
    poolEntry.setNetworkTimeout(milliseconds);
    isConnectionStateDirty=true;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public final boolean isWrapperFor(  Class<?> iface) throws SQLException {
    return iface.isInstance(delegate) || (delegate instanceof Wrapper && delegate.isWrapperFor(iface));
  }
  /** 
 * {@inheritDoc} 
 */
  @Override @SuppressWarnings("unchecked") public final <T>T unwrap(  Class<T> iface) throws SQLException {
    if (iface.isInstance(delegate)) {
      return (T)delegate;
    }
 else     if (delegate instanceof Wrapper) {
      return (T)delegate.unwrap(iface);
    }
    throw new SQLException("Wrapped connection is not an instance of " + iface);
  }
  public ConnectionProxy(){
  }
}
