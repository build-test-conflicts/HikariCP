package com.zaxxer.hikari.pool;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag.IConcurrentBagEntry;
import com.zaxxer.hikari.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/** 
 * Entry used in the ConcurrentBag to track Connection instances.
 * @author Brett Wooldridge
 */
public final class PoolBagEntry implements IConcurrentBagEntry {
  private static Logger LOGGER;
  private static SimpleDateFormat DATE_FORMAT;
  public FastList<Statement> openStatements;
  public HikariPool parentPool;
  public long creationTime;
  public Connection connection;
  public long lastAccess;
  public volatile long lastOpenTime;
  public volatile boolean evict;
  public boolean isAutoCommit;
  int networkTimeout;
  int transactionIsolation;
  String catalog;
  boolean isReadOnly;
  private PoolElf poolElf;
  private AtomicInteger state;
  private volatile ScheduledFuture<?> endOfLife;
static {
    LOGGER=LoggerFactory.getLogger(PoolBagEntry.class);
    DATE_FORMAT=new SimpleDateFormat("MMM dd, HH:mm:ss.SSS");
  }
  public PoolBagEntry(  final Connection connection,  final HikariPool pool){
    this.connection=connection;
    this.parentPool=pool;
    this.creationTime=System.currentTimeMillis();
    this.poolElf=pool.poolElf;
    this.state=new AtomicInteger(STATE_NOT_IN_USE);
    this.lastAccess=ClockSource.INSTANCE.currentTime();
    this.openStatements=new FastList<>(Statement.class,16);
    poolElf.resetPoolEntry(this);
    final long maxLifetime=pool.config.getMaxLifetime();
    final long variance=maxLifetime > 60_000 ? ThreadLocalRandom.current().nextLong(10_000) : 0;
    final long lifetime=maxLifetime - variance;
    if (lifetime > 0) {
      endOfLife=pool.houseKeepingExecutorService.schedule(new Runnable(){
        @Override public void run(){
          if (pool.connectionBag.reserve(PoolBagEntry.this)) {
            pool.closeConnection(PoolBagEntry.this,"(connection reached maxLifetime)");
          }
 else {
            PoolBagEntry.this.evict=true;
          }
        }
      }
,lifetime,TimeUnit.MILLISECONDS);
    }
  }
  /** 
 * Release this entry back to the pool.
 * @param lastAccess last access time-stamp
 */
  public void releaseConnection(  final long lastAccess){
    this.lastAccess=lastAccess;
    parentPool.releaseConnection(this);
  }
  /** 
 * Reset the connection to its original state.
 * @throws SQLException thrown if there is an error resetting the connection state
 */
  public void resetConnectionState() throws SQLException {
    poolElf.resetConnectionState(this);
  }
  /** 
 * @param networkTimeout the networkTimeout to set
 */
  public void setNetworkTimeout(  int networkTimeout){
    this.networkTimeout=networkTimeout;
  }
  /** 
 * @param transactionIsolation the transactionIsolation to set
 */
  public void setTransactionIsolation(  int transactionIsolation){
    this.transactionIsolation=transactionIsolation;
  }
  /** 
 * @param catalog the catalog to set
 */
  public void setCatalog(  String catalog){
    this.catalog=catalog;
  }
  /** 
 * @param isAutoCommit the isAutoCommit to set
 */
  public void setAutoCommit(  boolean isAutoCommit){
    this.isAutoCommit=isAutoCommit;
  }
  /** 
 * @param isReadOnly the isReadOnly to set
 */
  public void setReadOnly(  boolean isReadOnly){
    this.isReadOnly=isReadOnly;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int getState(){
    return state.get();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public boolean compareAndSet(  int expect,  int update){
    return state.compareAndSet(expect,update);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public String toString(){
    return connection + ", created " + formatDateTime(creationTime)+ ", last release "+ ClockSource.INSTANCE.elapsedMillis(lastAccess)+ "ms ago, "+ stateToString();
  }
  void close(){
    if (endOfLife != null && !endOfLife.isDone() && !endOfLife.cancel(false)) {
      LOGGER.warn("{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}",parentPool.config.getPoolName(),connection);
    }
    endOfLife=null;
    connection=null;
    parentPool.houseKeepingExecutorService.purge();
  }
  private static synchronized String formatDateTime(  final long timestamp){
    return DATE_FORMAT.format(new Date(timestamp));
  }
  private String stateToString(){
switch (state.get()) {
case STATE_IN_USE:
      return "IN_USE";
case STATE_NOT_IN_USE:
    return "NOT_IN_USE";
case STATE_REMOVED:
  return "REMOVED";
case STATE_RESERVED:
return "RESERVED";
default :
return "Invalid";
}
}
public PoolBagEntry(){
}
}
