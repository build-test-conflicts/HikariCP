package com.zaxxer.hikari.proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/** 
 * This is the proxy class for java.sql.Statement.
 * @author Brett Wooldridge
 */
public abstract class StatementProxy implements Statement {
  IHikariConnectionProxy connection;
  Statement delegate;
  private boolean isClosed;
  protected StatementProxy(  IHikariConnectionProxy connection,  Statement statement){
    this.connection=connection;
    this.delegate=statement;
  }
  protected final void checkException(  SQLException e){
    connection.checkException(e);
  }
  /** 
 * {@inheritDoc} 
 */
  public void close() throws SQLException {
    if (isClosed) {
      return;
    }
    isClosed=true;
    connection.untrackStatement(this);
    try {
      delegate.close();
    }
 catch (    SQLException e) {
      connection.checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet executeQuery(  String sql) throws SQLException {
    try {
      return delegate.executeQuery(sql);
    }
 catch (    SQLException e) {
      connection.checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet getResultSet() throws SQLException {
    try {
      return delegate.getResultSet();
    }
 catch (    SQLException e) {
      connection.checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet getGeneratedKeys() throws SQLException {
    try {
      return delegate.getGeneratedKeys();
    }
 catch (    SQLException e) {
      connection.checkException(e);
      throw e;
    }
  }
  /** 
 * {@inheritDoc} 
 */
  public Connection getConnection() throws SQLException {
    return connection;
  }
  @Override @SuppressWarnings("unchecked") public <T>T unwrap(  Class<T> iface) throws SQLException {
    if (iface.isInstance(delegate)) {
      return (T)delegate;
    }
    throw new SQLException("Wrapped connection is not an instance of " + iface);
  }
  public StatementProxy(){
  }
}
