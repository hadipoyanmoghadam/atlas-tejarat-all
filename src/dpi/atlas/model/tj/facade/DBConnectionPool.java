package dpi.atlas.model.tj.facade;

import org.apache.commons.dbcp.DPIBasicDBCPDatasource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;





import dpi.atlas.model.AtlasModel;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 25, 2009
 * Time: 7:09:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBConnectionPool {
    private static Log log = LogFactory.getLog(DBConnectionPool.class);

    List idleDBConnections = Collections.synchronizedList(new LinkedList());
    List busyDBConnections = Collections.synchronizedList(new LinkedList());
    DPIBasicDBCPDatasource dataSource = null;
    int initialPoolSize;
    int maxPoolSize;

    DBConnectionPool(int initialPoolSize, int maxPoolSize) throws SQLException, Exception {
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;

        dataSource = (DPIBasicDBCPDatasource) AtlasModel.getInstance().getBean("dataSource");
        try {
            Class.forName(dataSource.getDriverClassName()).newInstance();
            for (int i = 0; i < initialPoolSize; i++) {
                Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
                connection.setAutoCommit(false);
                idleDBConnections.add(connection);
            }
        } catch (SQLException se) {
            log.error(se);
            throw se;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }

    }


    public void returnConnection(Connection connection) {
        if (connection == null || !busyDBConnections.contains(connection))
            return;
        busyDBConnections.remove(connection);
        idleDBConnections.add(connection);
    }

    public Connection getConnection() throws SQLException {
        Connection connreturn;
        log.debug("Idle DB: " + idleDBConnections.size() + " Busy: " + busyDBConnections.size());
        if (idleDBConnections.size() > 0) {
            connreturn = (Connection) idleDBConnections.remove(0);
            busyDBConnections.add(connreturn);
            return connreturn;
        } else if (idleDBConnections.size() + busyDBConnections.size() < maxPoolSize)
            try {
                log.info("DBConnectionPool size = 0, creating new db connection ...");
                Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
                connection.setAutoCommit(false);
                busyDBConnections.add(connection);
                return connection;
            } catch (SQLException e) {
                log.error(e);
                e.printStackTrace();
                throw e;
            }
        else
            throw new SQLException("DBConnectionPool has no more free connection, max limit reached, busy DB Connection count = " + busyDBConnections.size());
    }
}

