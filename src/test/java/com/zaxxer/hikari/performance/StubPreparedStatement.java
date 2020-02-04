package com.zaxxer.hikari.performance;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
/** 
 * @author Brett Wooldridge
 */
public class StubPreparedStatement extends StubStatement implements PreparedStatement {
  /** 
 * {@inheritDoc} 
 */
  public ResultSet executeQuery(  String sql) throws SQLException {
    return new StubResultSet();
  }
  /** 
 * {@inheritDoc} 
 */
  public int executeUpdate(  String sql) throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getMaxFieldSize() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setMaxFieldSize(  int max) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public int getMaxRows() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setMaxRows(  int max) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setEscapeProcessing(  boolean enable) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public int getQueryTimeout() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setQueryTimeout(  int seconds) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void cancel() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }
  /** 
 * {@inheritDoc} 
 */
  public void clearWarnings() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setCursorName(  String name) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean execute(  String sql) throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet getResultSet() throws SQLException {
    return new StubResultSet();
  }
  /** 
 * {@inheritDoc} 
 */
  public int getUpdateCount() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean getMoreResults() throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setFetchDirection(  int direction) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public int getFetchDirection() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setFetchSize(  int rows) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public int getFetchSize() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getResultSetConcurrency() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getResultSetType() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void addBatch(  String sql) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void clearBatch() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public int[] executeBatch() throws SQLException {
    return null;
  }
  /** 
 * {@inheritDoc} 
 */
  public Connection getConnection() throws SQLException {
    return null;
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean getMoreResults(  int current) throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet getGeneratedKeys() throws SQLException {
    return new StubResultSet();
  }
  /** 
 * {@inheritDoc} 
 */
  public int executeUpdate(  String sql,  int autoGeneratedKeys) throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public int executeUpdate(  String sql,  int[] columnIndexes) throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public int executeUpdate(  String sql,  String[] columnNames) throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean execute(  String sql,  int autoGeneratedKeys) throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean execute(  String sql,  int[] columnIndexes) throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean execute(  String sql,  String[] columnNames) throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getResultSetHoldability() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setPoolable(  boolean poolable) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean isPoolable() throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public void closeOnCompletion() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean isCloseOnCompletion() throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSet executeQuery() throws SQLException {
    return new StubResultSet();
  }
  /** 
 * {@inheritDoc} 
 */
  public int executeUpdate() throws SQLException {
    return 0;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNull(  int parameterIndex,  int sqlType) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBoolean(  int parameterIndex,  boolean x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setByte(  int parameterIndex,  byte x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setShort(  int parameterIndex,  short x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setInt(  int parameterIndex,  int x) throws SQLException {
    count+=parameterIndex;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setLong(  int parameterIndex,  long x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setFloat(  int parameterIndex,  float x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setDouble(  int parameterIndex,  double x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBigDecimal(  int parameterIndex,  BigDecimal x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setString(  int parameterIndex,  String x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBytes(  int parameterIndex,  byte[] x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setDate(  int parameterIndex,  Date x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setTime(  int parameterIndex,  Time x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setTimestamp(  int parameterIndex,  Timestamp x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setAsciiStream(  int parameterIndex,  InputStream x,  int length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setUnicodeStream(  int parameterIndex,  InputStream x,  int length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBinaryStream(  int parameterIndex,  InputStream x,  int length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void clearParameters() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setObject(  int parameterIndex,  Object x,  int targetSqlType) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setObject(  int parameterIndex,  Object x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public boolean execute() throws SQLException {
    return false;
  }
  /** 
 * {@inheritDoc} 
 */
  public void addBatch() throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setCharacterStream(  int parameterIndex,  Reader reader,  int length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setRef(  int parameterIndex,  Ref x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBlob(  int parameterIndex,  Blob x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setClob(  int parameterIndex,  Clob x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setArray(  int parameterIndex,  Array x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public ResultSetMetaData getMetaData() throws SQLException {
    return null;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setDate(  int parameterIndex,  Date x,  Calendar cal) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setTime(  int parameterIndex,  Time x,  Calendar cal) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setTimestamp(  int parameterIndex,  Timestamp x,  Calendar cal) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNull(  int parameterIndex,  int sqlType,  String typeName) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setURL(  int parameterIndex,  URL x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public ParameterMetaData getParameterMetaData() throws SQLException {
    return null;
  }
  /** 
 * {@inheritDoc} 
 */
  public void setRowId(  int parameterIndex,  RowId x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNString(  int parameterIndex,  String value) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNCharacterStream(  int parameterIndex,  Reader value,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNClob(  int parameterIndex,  NClob value) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setClob(  int parameterIndex,  Reader reader,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBlob(  int parameterIndex,  InputStream inputStream,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNClob(  int parameterIndex,  Reader reader,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setSQLXML(  int parameterIndex,  SQLXML xmlObject) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setObject(  int parameterIndex,  Object x,  int targetSqlType,  int scaleOrLength) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setAsciiStream(  int parameterIndex,  InputStream x,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBinaryStream(  int parameterIndex,  InputStream x,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setCharacterStream(  int parameterIndex,  Reader reader,  long length) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setAsciiStream(  int parameterIndex,  InputStream x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBinaryStream(  int parameterIndex,  InputStream x) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setCharacterStream(  int parameterIndex,  Reader reader) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNCharacterStream(  int parameterIndex,  Reader value) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setClob(  int parameterIndex,  Reader reader) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setBlob(  int parameterIndex,  InputStream inputStream) throws SQLException {
  }
  /** 
 * {@inheritDoc} 
 */
  public void setNClob(  int parameterIndex,  Reader reader) throws SQLException {
  }
  public StubPreparedStatement(){
  }
}
