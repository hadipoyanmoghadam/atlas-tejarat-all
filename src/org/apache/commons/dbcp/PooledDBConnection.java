package org.apache.commons.dbcp;

import hit.db2.Db2Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 15, 2007
 * Time: 9:03:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class PooledDBConnection {
    Connection conn = null;
    public PooledDBConnection(String diverClassName, String url, String user, String pass) {

        Db2Driver driver = null;
        try {
            driver = (Db2Driver) Class.forName(diverClassName).newInstance();
//            db2ConnectionPoolDataSource.
        } catch (java.lang.Exception ex) {
            System.out.println("Error : Loading Driver" + ex.getMessage());
        }

        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.toString();  //To change body of catch statement use File | Settings | File Templates.
        }

        return;

    }


    public Connection getConnection() {
        return conn;
    }
}
