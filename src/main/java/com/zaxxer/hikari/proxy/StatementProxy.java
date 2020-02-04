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
  protected final ResultSet wrapResultSet(  ResultSet resultSet){
    if (resultSet != null) {
      return ProxyFactory.getProxyResultSet(this,resultSet);
    }
    return null;
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
      return wrapResultSet(delegate.executeQuery(sql));
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
      return wrapResultSet(delegate.getResultSet());
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
      return wrapResultSet(delegate.getGeneratedKeys());
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
    return (Connection)connection;
  }
  public StatementProxy(){
  }
}
