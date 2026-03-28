package com.coworking.dao;

import com.coworking.model.BookingRecord;
import com.coworking.model.UserRecord;
import com.coworking.model.Workspace;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceDao {
    private final Connection connection;

    public WorkspaceDao(Connection connection) {
        this.connection = connection;
    }

    public List<Workspace> fetchAvailableWorkspaces() throws SQLException {
        String sql =
            "SELECT workspace_id, name, location, capacity, available_seats, price_per_hour " +
            "FROM workspaces " +
            "WHERE available_seats > 0 " +
            "ORDER BY location, name";

        List<Workspace> workspaces = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                workspaces.add(mapWorkspace(resultSet));
            }
        }

        return workspaces;
    }

    public boolean userExists(String userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public UserRecord findUserByNameOrEmail(String name, String email) throws SQLException {
        if (email != null && !email.isBlank()) {
            String sql = "SELECT user_id, name, email FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email.trim());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUser(resultSet);
                    }
                }
            }
        }

        String sql = "SELECT user_id, name, email FROM users WHERE LOWER(name) = LOWER(?) ORDER BY user_id LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public UserRecord createUser(String userId, String name, String email) throws SQLException {
        String sql = "INSERT INTO users (user_id, name, email, phone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            statement.setString(2, name.trim());
            statement.setString(3, normalizedEmail(email));
            statement.setString(4, "NA");
            statement.executeUpdate();
        }

        return new UserRecord(userId, name.trim(), normalizedEmail(email));
    }

    public Workspace findWorkspace(String workspaceId) throws SQLException {
        String sql =
            "SELECT workspace_id, name, location, capacity, available_seats, price_per_hour " +
            "FROM workspaces WHERE workspace_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, workspaceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapWorkspace(resultSet);
                }
            }
        }

        return null;
    }

    public boolean decrementAvailableSeat(String workspaceId) throws SQLException {
        String sql =
            "UPDATE workspaces " +
            "SET available_seats = available_seats - 1 " +
            "WHERE workspace_id = ? AND available_seats > 0";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, workspaceId);
            return statement.executeUpdate() == 1;
        }
    }

    public void insertBooking(
        String bookingId,
        String userId,
        String workspaceId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalPrice
    ) throws SQLException {
        String sql =
            "INSERT INTO bookings (booking_id, user_id, workspace_id, start_time, end_time, total_price) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bookingId);
            statement.setString(2, userId);
            statement.setString(3, workspaceId);
            statement.setTimestamp(4, Timestamp.valueOf(startTime));
            statement.setTimestamp(5, Timestamp.valueOf(endTime));
            statement.setInt(6, totalPrice);
            statement.executeUpdate();
        }
    }

    public List<BookingRecord> fetchBookingsByUser(String userId) throws SQLException {
        String sql =
            "SELECT b.booking_id, b.user_id, b.workspace_id, w.name AS workspace_name, w.location, " +
            "b.start_time, b.end_time, b.total_price " +
            "FROM bookings b " +
            "JOIN workspaces w ON b.workspace_id = w.workspace_id " +
            "WHERE b.user_id = ? " +
            "ORDER BY b.start_time DESC";

        List<BookingRecord> bookings = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(
                        new BookingRecord(
                            resultSet.getString("booking_id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("workspace_id"),
                            resultSet.getString("workspace_name"),
                            resultSet.getString("location"),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getTimestamp("end_time").toLocalDateTime(),
                            resultSet.getInt("total_price")
                        )
                    );
                }
            }
        }

        return bookings;
    }

    private Workspace mapWorkspace(ResultSet resultSet) throws SQLException {
        return new Workspace(
            resultSet.getString("workspace_id"),
            resultSet.getString("name"),
            resultSet.getString("location"),
            resultSet.getInt("capacity"),
            resultSet.getInt("available_seats"),
            resultSet.getInt("price_per_hour")
        );
    }

    private UserRecord mapUser(ResultSet resultSet) throws SQLException {
        return new UserRecord(
            resultSet.getString("user_id"),
            resultSet.getString("name"),
            resultSet.getString("email")
        );
    }

    private String normalizedEmail(String email) {
        return email == null || email.isBlank() ? "NA" : email.trim();
    }
}
