package com.coworking.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements ApplicationRunner {
    private final DatabaseProperties databaseProperties;

    public DatabaseInitializer(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ensureDatabaseExists();
        ensureTablesExist();
        ensureSampleData();
    }

    private void ensureDatabaseExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                databaseProperties.getServerUrl(),
                databaseProperties.getUsername(),
                databaseProperties.getPassword()
            );
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS coworking_space");
        }
    }

    private void ensureTablesExist() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                databaseProperties.getUrl(),
                databaseProperties.getUsername(),
                databaseProperties.getPassword()
            );
             Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id VARCHAR(50) PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(20) NOT NULL)"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS workspaces (" +
                "workspace_id VARCHAR(50) PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "location VARCHAR(100) NOT NULL, " +
                "capacity INT NOT NULL, " +
                "available_seats INT NOT NULL, " +
                "price_per_hour INT NOT NULL, " +
                "CHECK (capacity >= 0), " +
                "CHECK (available_seats >= 0), " +
                "CHECK (available_seats <= capacity), " +
                "CHECK (price_per_hour >= 0))"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id VARCHAR(50) PRIMARY KEY, " +
                "user_id VARCHAR(50) NOT NULL, " +
                "workspace_id VARCHAR(50) NOT NULL, " +
                "start_time DATETIME NOT NULL, " +
                "end_time DATETIME NOT NULL, " +
                "total_price INT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                "FOREIGN KEY (workspace_id) REFERENCES workspaces(workspace_id), " +
                "CHECK (end_time > start_time), " +
                "CHECK (total_price >= 0))"
            );
        }
    }

    private void ensureSampleData() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                databaseProperties.getUrl(),
                databaseProperties.getUsername(),
                databaseProperties.getPassword()
            )) {
            insertUserIfMissing(connection, "U1", "Aarav Sharma", "aarav@example.com", "9876543210");
            insertUserIfMissing(connection, "U2", "Diya Patel", "diya@example.com", "9876543211");
            insertWorkspaceIfMissing(connection, "W1", "Open Desk Alpha", "Mumbai", 20, 20, 200);
            insertWorkspaceIfMissing(connection, "W2", "Meeting Room Beta", "Bengaluru", 8, 8, 500);
            insertWorkspaceIfMissing(connection, "W3", "Sky Desk Studio", "Pune", 16, 16, 180);
            insertWorkspaceIfMissing(connection, "W4", "Focus Hub Gamma", "Hyderabad", 24, 24, 220);
            insertWorkspaceIfMissing(connection, "W5", "Collab Room Delta", "Chennai", 10, 10, 450);
            insertWorkspaceIfMissing(connection, "W6", "Startup Bay", "Delhi", 18, 18, 210);
            insertWorkspaceIfMissing(connection, "W7", "Executive Suite One", "Gurugram", 6, 6, 650);
            insertWorkspaceIfMissing(connection, "W8", "Creative Corner", "Kolkata", 14, 14, 170);
            insertWorkspaceIfMissing(connection, "W9", "Boardroom Nexus", "Noida", 12, 12, 550);
            insertWorkspaceIfMissing(connection, "W10", "Ocean View Desk", "Goa", 9, 9, 300);
        }
    }

    private void insertUserIfMissing(
        Connection connection,
        String userId,
        String name,
        String email,
        String phone
    ) throws SQLException {
        String sql =
            "INSERT INTO users (user_id, name, email, phone) " +
            "SELECT ?, ?, ?, ? FROM DUAL " +
            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, userId);
            statement.executeUpdate();
        }
    }

    private void insertWorkspaceIfMissing(
        Connection connection,
        String workspaceId,
        String name,
        String location,
        int capacity,
        int availableSeats,
        int pricePerHour
    ) throws SQLException {
        String sql =
            "INSERT INTO workspaces (workspace_id, name, location, capacity, available_seats, price_per_hour) " +
            "SELECT ?, ?, ?, ?, ?, ? FROM DUAL " +
            "WHERE NOT EXISTS (SELECT 1 FROM workspaces WHERE workspace_id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, workspaceId);
            statement.setString(2, name);
            statement.setString(3, location);
            statement.setInt(4, capacity);
            statement.setInt(5, availableSeats);
            statement.setInt(6, pricePerHour);
            statement.setString(7, workspaceId);
            statement.executeUpdate();
        }
    }
}
