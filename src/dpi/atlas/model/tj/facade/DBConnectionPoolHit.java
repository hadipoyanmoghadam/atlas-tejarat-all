package dpi.atlas.model.tj.facade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Connection;

import dpi.atlas.model.AtlasModel;

import javax.naming.Context;
import javax.naming.InitialContext;

import hit.db2.Db2ConnectionPoolDataSource;

/**
 * Created by SH.Behnaz.
 * Date: October 9, 2013
 * Time: 15:09:43 PM
 */
public class DBConnectionPoolHit {
    private static Log log = LogFactory.getLog(DBConnectionPoolHit.class);

    Db2ConnectionPoolDataSource dataSource = null;
    hit.db2.Db2PooledDataSource pooledDS = new hit.db2.Db2PooledDataSource();
    private int maxActiveConnections;

    DBConnectionPoolHit(int initialPoolSize, int maxPoolSize, String datasourceName, int optimalPoolSize) throws Exception {

        try {

            Context ctx = new InitialContext();

            dataSource = (Db2ConnectionPoolDataSource) AtlasModel.getInstance().getBean("connectionPoolDataSource");

            ctx.rebind(datasourceName, dataSource);

            pooledDS.setDataSourceName(datasourceName);
            pooledDS.setMaxConnections(maxPoolSize);
            pooledDS.setOptimalConnections(optimalPoolSize);
            pooledDS.setInitialPoolSize(initialPoolSize);

            log.fatal("<<<<< DataSource properties:" + "Server Name:(" + dataSource.getServerName() +
                    "), Port:(" + dataSource.getPortNumber() + "), Database:(" + dataSource.getDatabaseName()+ ") >>>>>");
            log.fatal("###########################################################################################");


        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }


    public void returnConnection(Connection connection) throws SQLException {
        try {
            if (connection != null)
                connection.close();

        } catch (Exception e) {
            log.error("error in DBConnectionPoolHit >> can not close Connection!!!!!!!!!!!!" + e.getMessage());
            throw new SQLException("can not close Connection.!!!");
        }

    }

    public Connection getConnection() throws SQLException {

        log.debug("Pooled Available connection: " + pooledDS.getAvailableConnectionCount() + " Pooled Active connection: " + pooledDS.getActiveConnectionCount());
        try {
            Connection connection = pooledDS.getConnection();
            connection.setAutoCommit(false);
            int activeCount = this.getActiveConnectionPoole();
            if(activeCount > maxActiveConnections)
                maxActiveConnections = activeCount;
            return connection;
        } catch (SQLException e) {
            log.error("Pooled Available connection: " + pooledDS.getAvailableConnectionCount() + " Pooled Active connection: " + pooledDS.getActiveConnectionCount()+" ERROR::" +e.getMessage());
            log.error("<<<<< DataSource properties:" + "Server Name:(" + dataSource.getServerName() +
                    "), Port:(" + dataSource.getPortNumber() + "), Database:(" + dataSource.getDatabaseName()+
                    "), User:("+ dataSource.getUser() + "), Password:(" + dataSource.getPassword()+ " >>>>>");
            log.error("###########################################################################################");
            throw new SQLException("Error in getConnection.!!!");

        }
    }

    public int getAvailableConnectionPoole() {
        return pooledDS.getAvailableConnectionCount();
    }

    public int getActiveConnectionPoole() {
        return pooledDS.getActiveConnectionCount();
    }

    public String getDatasourceName() {
        return pooledDS.getDataSourceName();
    }

    public int getMaxActiveConnections() {
        return maxActiveConnections;
    }
}