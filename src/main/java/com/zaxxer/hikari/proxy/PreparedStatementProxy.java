package com.zaxxer.hikari.proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/** 
 * This is the proxy class for java.sql.PreparedStatement.
 * @author Brett Wooldridge
 */
public abstract class PreparedStatementProxy extends StatementProxy implements PreparedStatement {
  protected PreparedStatementProxy(  ConnectionProxy connection,  PreparedStatement statement){
    super(connection,statement);
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet executeQuery() throws SQLException {
    try {
      return ((PreparedStatement)delegate).executeQuery();
    }
 catch (    SQLException e) {
      connection.checkException(e);
      throw e;
    }
  }
  public PreparedStatementProxy(){
  }
}
