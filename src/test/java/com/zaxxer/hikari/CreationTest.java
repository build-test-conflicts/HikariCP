package com.zaxxer.hikari;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
/** 
 * System property testProxy can be one of: "com.zaxxer.hikari.JavaProxyFactory" "com.zaxxer.hikari.CglibProxyFactory" "com.zaxxer.hikari.JavassistProxyFactory"
 * @author Brett Wooldridge
 */
public class CreationTest {
  @Test public void testCreate() throws SQLException {
    HikariConfig config=new HikariConfig();
    config.setMinimumPoolSize(1);
    config.setMaximumPoolSize(1);
    config.setAcquireIncrement(1);
    config.setConnectionTestQuery("VALUES 1");
    config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
    HikariDataSource ds=new HikariDataSource(config);
    Assert.assertSame("Totals connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",1,ds.pool.getIdleConnections());
    Connection connection=ds.getConnection();
    Assert.assertNotNull(connection);
    Assert.assertSame("Totals connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",0,ds.pool.getIdleConnections());
    PreparedStatement statement=connection.prepareStatement("SELECT * FROM device WHERE device_id=?");
    Assert.assertNotNull(statement);
    statement.setInt(1,0);
    ResultSet resultSet=statement.executeQuery();
    Assert.assertNotNull(resultSet);
    Assert.assertFalse(resultSet.next());
    resultSet.close();
    statement.close();
    connection.close();
    Assert.assertSame("Totals connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",1,ds.pool.getIdleConnections());
  }
  @Test public void testMaxLifetime() throws Exception {
    HikariConfig config=new HikariConfig();
    config.setMinimumPoolSize(1);
    config.setMaximumPoolSize(1);
    config.setAcquireIncrement(1);
    config.setConnectionTestQuery("VALUES 1");
    config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
    System.setProperty("com.zaxxer.hikari.housekeeping.period","100");
    HikariDataSource ds=new HikariDataSource(config);
    System.clearProperty("com.zaxxer.hikari.housekeeping.period");
    config.setMaxLifetime(700);
    Assert.assertSame("Total connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",1,ds.pool.getIdleConnections());
    Connection connection=ds.getConnection();
    Assert.assertNotNull(connection);
    Assert.assertSame("Second total connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Second idle connections not as expected",0,ds.pool.getIdleConnections());
    connection.close();
    Assert.assertSame("Idle connections not as expected",1,ds.pool.getIdleConnections());
    Connection connection2=ds.getConnection();
    Assert.assertSame("Expected the same connection",connection,connection2);
    connection2.close();
    Thread.sleep(2000);
    connection2=ds.getConnection();
    Assert.assertNotSame("Expected a different connection",connection,connection2);
    connection2.close();
    Assert.assertSame("Post total connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Post idle connections not as expected",1,ds.pool.getIdleConnections());
  }
  @Test public void testDoubleClose() throws Exception {
    HikariConfig config=new HikariConfig();
    config.setMinimumPoolSize(1);
    config.setMaximumPoolSize(1);
    config.setAcquireIncrement(1);
    config.setConnectionTestQuery("VALUES 1");
    config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
    HikariDataSource ds=new HikariDataSource(config);
    Connection connection=ds.getConnection();
    connection.close();
    connection.close();
  }
  @Test public void testBackfill() throws Exception {
    HikariConfig config=new HikariConfig();
    config.setMinimumPoolSize(1);
    config.setMaximumPoolSize(4);
    config.setAcquireIncrement(2);
    config.setConnectionTimeout(500);
    config.setConnectionTestQuery("VALUES 1");
    config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
    HikariDataSource ds=new HikariDataSource(config);
    Assert.assertSame("Totals connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",1,ds.pool.getIdleConnections());
    Connection connection=ds.getConnection();
    Assert.assertNotNull(connection);
    Assert.assertSame("Totals connections not as expected",1,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",0,ds.pool.getIdleConnections());
    PreparedStatement statement=connection.prepareStatement("SELECT some, thing FROM somewhere WHERE something=?");
    Assert.assertNotNull(statement);
    ResultSet resultSet=statement.executeQuery();
    Assert.assertNotNull(resultSet);
    try {
      statement.getMaxFieldSize();
      Assert.fail();
    }
 catch (    Exception e) {
      Assert.assertSame(SQLException.class,e.getClass());
    }
    connection.close();
    Assert.assertSame("Totals connections not as expected",0,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",0,ds.pool.getIdleConnections());
    Thread.sleep(600);
    Assert.assertSame("Totals connections not as expected",2,ds.pool.getTotalConnections());
    Assert.assertSame("Idle connections not as expected",2,ds.pool.getIdleConnections());
  }
  @Test public void testIsolation() throws Exception {
    HikariConfig config=new HikariConfig();
    config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
    int transactionIsolation=config.getTransactionIsolation();
    Assert.assertSame(Connection.TRANSACTION_REPEATABLE_READ,transactionIsolation);
  }
  public CreationTest(){
  }
}
