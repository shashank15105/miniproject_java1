package com.coworking.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseManager {
    private final DatabaseProperties databaseProperties;

    public DatabaseManager(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC driver not found in the application classpath.", ex);
        }

        return DriverManager.getConnection(
            databaseProperties.getUrl(),
            databaseProperties.getUsername(),
            databaseProperties.getPassword()
        );
    }
}
