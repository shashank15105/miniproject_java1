package com.coworking.service;

import com.coworking.config.DatabaseManager;
import com.coworking.dao.WorkspaceDao;
import com.coworking.model.BookingRecord;
import com.coworking.model.BookingRequest;
import com.coworking.model.BookingResponse;
import com.coworking.model.UserRecord;
import com.coworking.model.Workspace;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {
    private final DatabaseManager databaseManager;

    public WorkspaceService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Workspace> getAvailableWorkspaces() {
        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            return workspaceDao.fetchAvailableWorkspaces();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load workspaces.", ex);
        }
    }

    public BookingResponse bookWorkspace(BookingRequest request) {
        validateBookingRequest(request);

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                WorkspaceDao workspaceDao = new WorkspaceDao(connection);

                UserRecord user = resolveOrCreateUser(workspaceDao, request);

                Workspace workspace = workspaceDao.findWorkspace(request.getWorkspaceId().trim());
                if (workspace == null) {
                    throw new IllegalArgumentException("Invalid workspace ID.");
                }

                if (workspace.getAvailableSeats() <= 0) {
                    throw new IllegalStateException("No seats available for this workspace.");
                }

                int totalPrice = calculateTotalPrice(
                    request.getStartTime(),
                    request.getEndTime(),
                    workspace.getPricePerHour()
                );

                boolean updated = workspaceDao.decrementAvailableSeat(request.getWorkspaceId().trim());
                if (!updated) {
                    throw new IllegalStateException("No seats available for this workspace.");
                }

                String bookingId = generateBookingId();
                workspaceDao.insertBooking(
                    bookingId,
                    user.getUserId(),
                    request.getWorkspaceId().trim(),
                    request.getStartTime(),
                    request.getEndTime(),
                    totalPrice
                );

                connection.commit();
                return new BookingResponse(
                    true,
                    "Workspace booked successfully.",
                    bookingId,
                    totalPrice,
                    user.getUserId(),
                    user.getName()
                );
            } catch (IllegalArgumentException | IllegalStateException | SQLException ex) {
                connection.rollback();
                if (ex instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                if (ex instanceof IllegalStateException illegalStateException) {
                    throw illegalStateException;
                }
                throw ex;
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to complete booking.", ex);
        }
    }

    public List<BookingRecord> getBookingsByUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            if (!workspaceDao.userExists(userId.trim())) {
                throw new IllegalArgumentException("Invalid user ID.");
            }
            return workspaceDao.fetchBookingsByUser(userId.trim());
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load bookings.", ex);
        }
    }

    private void validateBookingRequest(BookingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && !isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (request.getWorkspaceId() == null || request.getWorkspaceId().trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace ID is required.");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
    }

    private UserRecord resolveOrCreateUser(WorkspaceDao workspaceDao, BookingRequest request) throws SQLException {
        String normalizedName = request.getName().trim();
        String normalizedEmail = request.getEmail() == null ? "" : request.getEmail().trim();

        UserRecord existingUser = workspaceDao.findUserByNameOrEmail(normalizedName, normalizedEmail);
        if (existingUser != null) {
            if (!normalizedEmail.isBlank()
                && !"NA".equalsIgnoreCase(existingUser.getEmail())
                && !existingUser.getEmail().equalsIgnoreCase(normalizedEmail)) {
                throw new IllegalArgumentException("This email is already linked to another user.");
            }
            return existingUser;
        }

        return workspaceDao.createUser(generateUserId(), normalizedName, normalizedEmail);
    }

    private int calculateTotalPrice(LocalDateTime startTime, LocalDateTime endTime, int pricePerHour) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        long chargedHours = (long) Math.ceil(minutes / 60.0);
        return Math.toIntExact(chargedHours * pricePerHour);
    }

    private String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String generateUserId() {
        return "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
