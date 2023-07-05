package org.apache.commons.dbcp;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 16, 2007
 * Time: 1:08:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DPIBasicDBCPDatasource implements DataSource {

    private static Log log = LogFactory.getLog(DPIBasicDBCPDatasource.class);
    protected boolean defaultAutoCommit;
    protected Boolean defaultReadOnly;
    protected int defaultTransactionIsolation;
    protected String defaultCatalog;
    protected String driverClassName;
    protected int maxActive;
    protected int maxIdle;
    protected int minIdle;
    protected int initialSize;
    protected long maxWait;
    protected boolean poolPreparedStatements;
    protected int maxOpenPreparedStatements;
    protected boolean testOnBorrow;
    protected boolean testOnReturn;
    protected long timeBetweenEvictionRunsMillis;
    protected int numTestsPerEvictionRun;
    protected long minEvictableIdleTimeMillis;
    protected boolean testWhileIdle;
    protected String password;
    protected String url;
    protected String username;
    protected String validationQuery;
    private boolean accessToUnderlyingConnectionAllowed;
    private boolean restartNeeded;
    protected GenericObjectPool connectionPool;
    protected Properties connectionProperties;
    protected DataSource dataSource;
    protected PrintWriter logWriter;
    private AbandonedConfig abandonedConfig;

    public DPIBasicDBCPDatasource() {
        defaultAutoCommit = true;
        defaultReadOnly = null;
        defaultTransactionIsolation = -1;
        defaultCatalog = null;
        driverClassName = null;
        maxActive = 8;
        maxIdle = 8;
        minIdle = 0;
        initialSize = 0;
        maxWait = -1L;
        poolPreparedStatements = false;
        maxOpenPreparedStatements = -1;
        testOnBorrow = true;
        testOnReturn = false;
        timeBetweenEvictionRunsMillis = -1L;
        numTestsPerEvictionRun = 3;
        minEvictableIdleTimeMillis = 0x1b7740L;
        testWhileIdle = false;
        password = null;
        url = null;
        username = null;
        validationQuery = null;
        accessToUnderlyingConnectionAllowed = false;
        restartNeeded = false;
        connectionPool = null;
        connectionProperties = new Properties();
        dataSource = null;
        logWriter = null;//new PrintWriter(System.out);
    }

    public synchronized boolean getDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public synchronized void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        restartNeeded = true;
    }

    public synchronized boolean getDefaultReadOnly() {
        if (defaultReadOnly != null)
            return defaultReadOnly.booleanValue();
        else
            return false;
    }

    public synchronized void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly ? Boolean.TRUE : Boolean.FALSE;
        restartNeeded = true;
    }

    public synchronized int getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public synchronized void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        restartNeeded = true;
    }

    public synchronized String getDefaultCatalog() {
        return defaultCatalog;
    }

    public synchronized void setDefaultCatalog(String defaultCatalog) {
        if (defaultCatalog != null && defaultCatalog.trim().length() > 0)
            this.defaultCatalog = defaultCatalog;
        else
            this.defaultCatalog = null;
        restartNeeded = true;
    }

    public synchronized String getDriverClassName() {
        return driverClassName;
    }

    public synchronized void setDriverClassName(String driverClassName) {
        if (driverClassName != null && driverClassName.trim().length() > 0)
            this.driverClassName = driverClassName;
        else
            this.driverClassName = null;
        restartNeeded = true;
    }

    public synchronized int getMaxActive() {
        return maxActive;
    }

    public synchronized void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        if (connectionPool != null)
            connectionPool.setMaxActive(maxActive);
    }

    public synchronized int getMaxIdle() {
        return maxIdle;
    }

    public synchronized void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        if (connectionPool != null)
            connectionPool.setMaxIdle(maxIdle);
    }

    public synchronized int getMinIdle() {
        return minIdle;
    }

    public synchronized void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        if (connectionPool != null)
            connectionPool.setMinIdle(minIdle);
    }

    public synchronized int getInitialSize() {
        return initialSize;
    }

    public synchronized void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        restartNeeded = true;
    }

    public synchronized long getMaxWait() {
        return maxWait;
    }

    public synchronized void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
        if (connectionPool != null)
            connectionPool.setMaxWait(maxWait);
    }

    public synchronized boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public synchronized void setPoolPreparedStatements(boolean poolingStatements) {
        poolPreparedStatements = poolingStatements;
        restartNeeded = true;
    }

    public synchronized int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public synchronized void setMaxOpenPreparedStatements(int maxOpenStatements) {
        maxOpenPreparedStatements = maxOpenStatements;
        restartNeeded = true;
    }

    public synchronized boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public synchronized void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        if (connectionPool != null)
            connectionPool.setTestOnBorrow(testOnBorrow);
    }

    public synchronized boolean getTestOnReturn() {
        return testOnReturn;
    }

    public synchronized void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        if (connectionPool != null)
            connectionPool.setTestOnReturn(testOnReturn);
    }

    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public synchronized void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        if (connectionPool != null)
            connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public synchronized int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public synchronized void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        if (connectionPool != null)
            connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public synchronized long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public synchronized void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        if (connectionPool != null)
            connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public synchronized boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public synchronized void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        if (connectionPool != null)
            connectionPool.setTestWhileIdle(testWhileIdle);
    }

    public synchronized int getNumActive() {
        if (connectionPool != null)
            return connectionPool.getNumActive();
        else
            return 0;
    }

    public synchronized int getNumIdle() {
        if (connectionPool != null)
            return connectionPool.getNumIdle();
        else
            return 0;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
        restartNeeded = true;
    }

    public synchronized String getUrl() {
        return url;
    }

    public synchronized void setUrl(String url) {
        this.url = url;
        restartNeeded = true;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
        restartNeeded = true;
    }

    public synchronized String getValidationQuery() {
        return validationQuery;
    }

    public synchronized void setValidationQuery(String validationQuery) {
        if (validationQuery != null && validationQuery.trim().length() > 0)
            this.validationQuery = validationQuery;
        else
            this.validationQuery = null;
        restartNeeded = true;
    }

    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public synchronized void setAccessToUnderlyingConnectionAllowed(boolean allow) {
        accessToUnderlyingConnectionAllowed = allow;
        restartNeeded = true;
    }

    private synchronized boolean isRestartNeeded() {
        return restartNeeded;
    }

    public Connection getConnection()
            throws SQLException {
        return createDataSource().getConnection();
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        return createDataSource().getConnection(username, password);
    }

    public int getLoginTimeout()
            throws SQLException {
        return createDataSource().getLoginTimeout();
    }

    public PrintWriter getLogWriter()
            throws SQLException {
        return createDataSource().getLogWriter();
    }

    public void setLoginTimeout(int loginTimeout)
            throws SQLException {
        createDataSource().setLoginTimeout(loginTimeout);
    }

    public void setLogWriter(PrintWriter logWriter)
            throws SQLException {
        createDataSource().setLogWriter(logWriter);
        this.logWriter = logWriter;
    }

    /**
     * @deprecated Method getRemoveAbandoned is deprecated
     */

    public boolean getRemoveAbandoned() {
        if (abandonedConfig != null)
            return abandonedConfig.getRemoveAbandoned();
        else
            return false;
    }

    /**
     * @deprecated Method setRemoveAbandoned is deprecated
     */

    public void setRemoveAbandoned(boolean removeAbandoned) {
        if (abandonedConfig == null)
            abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandoned(removeAbandoned);
        restartNeeded = true;
    }

    /**
     * @deprecated Method getRemoveAbandonedTimeout is deprecated
     */

    public int getRemoveAbandonedTimeout() {
        if (abandonedConfig != null)
            return abandonedConfig.getRemoveAbandonedTimeout();
        else
            return 300;
    }

    /**
     * @deprecated Method setRemoveAbandonedTimeout is deprecated
     */

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        if (abandonedConfig == null)
            abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        restartNeeded = true;
    }

    /**
     * @deprecated Method getLogAbandoned is deprecated
     */

    public boolean getLogAbandoned() {
        if (abandonedConfig != null)
            return abandonedConfig.getLogAbandoned();
        else
            return false;
    }

    /**
     * @deprecated Method setLogAbandoned is deprecated
     */

    public void setLogAbandoned(boolean logAbandoned) {
        if (abandonedConfig == null)
            abandonedConfig = new AbandonedConfig();
        abandonedConfig.setLogAbandoned(logAbandoned);
        restartNeeded = true;
    }

    public void addConnectionProperty(String name, String value) {
        connectionProperties.put(name, value);
        restartNeeded = true;
    }

    public void removeConnectionProperty(String name) {
        connectionProperties.remove(name);
        restartNeeded = true;
    }

    public synchronized void close()
            throws SQLException {
        System.out.println("Closeing DataSource");
        GenericObjectPool oldpool = connectionPool;
        connectionPool = null;
        dataSource = null;
        try {
            if (oldpool != null)
                oldpool.close();
        }
        catch (SQLException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLNestedException("Cannot close connection pool", e);
        }
    }

    protected synchronized DataSource createDataSource()
            throws SQLException {
        if (dataSource != null)
            return dataSource;
        if (driverClassName != null)
            try {
                Class.forName(driverClassName);
            }
            catch (Throwable t) {
                String message = "Cannot load JDBC driver class '" + driverClassName + "'";
                logWriter.println(message);
//                t.printStackTrace(logWriter);
                throw new SQLNestedException(message, t);
            }
        java.sql.Driver driver = null;
        try {
            driver = DriverManager.getDriver(url);
        }
        catch (Throwable t) {
            String message = "Cannot create JDBC driver of class '" + (driverClassName == null ? "" : driverClassName) + "' for connect URL '" + url + "'";
            logWriter.println(message);
//            t.printStackTrace(logWriter);
            throw new SQLNestedException(message, t);
        }
        if (validationQuery == null) {
            setTestOnBorrow(false);
            setTestOnReturn(false);
            setTestWhileIdle(false);
        }
        if (abandonedConfig != null && abandonedConfig.getRemoveAbandoned())
            connectionPool = new AbandonedObjectPool(null, abandonedConfig);
        else
            connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(maxActive);
        connectionPool.setMaxIdle(maxIdle);
        connectionPool.setMinIdle(minIdle);
        connectionPool.setMaxWait(maxWait);
        connectionPool.setTestOnBorrow(testOnBorrow);
        connectionPool.setTestOnReturn(testOnReturn);
        connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        connectionPool.setTestWhileIdle(testWhileIdle);
        GenericKeyedObjectPoolFactory statementPoolFactory = null;
        if (isPoolPreparedStatements())
            statementPoolFactory = new GenericKeyedObjectPoolFactory(null, -1, (byte) 0, 0L, 1, maxOpenPreparedStatements);
        if (username != null)
            connectionProperties.put("user", username);
        else
            log("DBCP DataSource configured without a 'username'");
        if (password != null)
            connectionProperties.put("password", password);
        else
            log("DBCP DataSource configured without a 'password'");
        DriverConnectionFactory driverConnectionFactory = new DriverConnectionFactory(driver, url, connectionProperties);
        PoolableConnectionFactory connectionFactory = null;

        log.debug("DefaultAutoCommit = " + this.defaultAutoCommit);

        try {
            connectionFactory = new PoolableConnectionFactory(driverConnectionFactory, connectionPool, statementPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation, defaultCatalog, abandonedConfig);
            if (connectionFactory == null)
                throw new SQLException("Cannot create PoolableConnectionFactory");
            validateConnectionFactory(connectionFactory);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLNestedException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
        }
        dataSource = new PoolingDataSource(connectionPool);
        ((PoolingDataSource) dataSource).setAccessToUnderlyingConnectionAllowed(isAccessToUnderlyingConnectionAllowed());
        dataSource.setLogWriter(logWriter);
        try {
            for (int i = 0; i < initialSize; i++)
                connectionPool.addObject();

        }
        catch (Exception e) {
            throw new SQLNestedException("Error preloading the connection pool", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new HookThread()));

        return dataSource;
    }

    private static void validateConnectionFactory(PoolableConnectionFactory connectionFactory)
            throws Exception {
        Connection conn = null;
        try {
            conn = (Connection) connectionFactory.makeObject();
            connectionFactory.activateObject(conn);
            connectionFactory.validateConnection(conn);
            connectionFactory.passivateObject(conn);
        }
        finally {
            connectionFactory.destroyObject(conn);
        }
    }

    private void restart() {
        try {
            close();
        }
        catch (SQLException e) {
            log("Could not restart DataSource, cause: " + e.getMessage());
        }
    }

    private void log(String message) {
        if (logWriter != null)
            logWriter.println(message);
    }


    class HookThread implements Runnable {

        public void run() {
            System.out.println("Starting To Shutdown...");
            System.out.println("End of Shutdown...");
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


