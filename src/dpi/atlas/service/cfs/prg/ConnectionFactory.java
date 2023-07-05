package dpi.atlas.service.cfs.prg;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

public class ConnectionFactory {
    private String url;
    private String user;
    private String pass;
    private String driver;

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public Statement createStatement() throws SQLException {
        return connect().createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connect().prepareStatement(sql);
    }

    public ConnectionFactory(String fileName) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(fileName));
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            pass = properties.getProperty("pass");
            driver = properties.getProperty("driver");
            Class.forName(driver);
        } catch (Exception e) {
            e.toString();
        }
    }
}


