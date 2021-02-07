package fr.isen.java2.db.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    public static Connection getConnection(String db_url, String db_username, String db_password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    db_url,
                    db_username,
                    db_password
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnectionFromProps() {
        Connection connection = null;
        try {
            Properties props = new Properties();
            FileInputStream fileInputStream = new FileInputStream("db.properties");
            props.load(fileInputStream);
            Class.forName(props.getProperty("DB_DRIVER_CLASS"));
            connection = getConnection(
                    props.getProperty("DB_URL"),
                    props.getProperty("DB_USERNAME"),
                    props.getProperty("DB_PASSWORD")
            );
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
