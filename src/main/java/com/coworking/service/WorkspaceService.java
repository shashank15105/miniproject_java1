package com.coworking.service;

import com.coworking.config.DatabaseManager;
import com.coworking.dao.WorkspaceDao;
import com.coworking.model.BookingDetails;
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
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {
    private final DatabaseManager databaseManager;
    private final BookingNotificationService bookingNotificationService;
    private final ReceiptService receiptService;

    public WorkspaceService(
        DatabaseManager databaseManager,
        BookingNotificationService bookingNotificationService,
        ReceiptService receiptService
    ) {
        this.databaseManager = databaseManager;
        this.bookingNotificationService = bookingNotificationService;
        this.receiptService = receiptService;
    }

    public List<Workspace> getAvailableWorkspaces() {
        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            return workspaceDao.fetchAvailableWorkspaces();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load workspaces.", ex);
        }
    }

    public BookingResponse bookWorkspace(BookingRequest request, UserRecord authenticatedUser) {
        validateBookingRequest(request, authenticatedUser);
        BookingDetails bookingDetails;

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                WorkspaceDao workspaceDao = new WorkspaceDao(connection);

                UserRecord user = authenticatedUser != null
                    ? authenticatedUser
                    : resolveOrCreateUser(workspaceDao, request);

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

                bookingDetails = workspaceDao.fetchBookingDetails(bookingId);
                connection.commit();
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

        boolean emailSent = bookingNotificationService.sendConfirmationEmail(bookingDetails);
        return new BookingResponse(
            true,
            emailSent ? "Workspace booked and confirmation email sent." : "Workspace booked successfully.",
            bookingDetails.getBookingId(),
            bookingDetails.getTotalPrice(),
            bookingDetails.getUserId(),
            bookingDetails.getUserName(),
            emailSent,
            "/bookings/" + bookingDetails.getBookingId() + "/receipt",
            "/bookings/" + bookingDetails.getBookingId() + "/email-preview"
        );
    }

    public List<BookingRecord> getBookingsByUser(String userId, UserRecord authenticatedUser) {
        String targetUserId = resolveTargetUserId(userId, authenticatedUser);

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            if (!workspaceDao.userExists(targetUserId)) {
                throw new IllegalArgumentException("Invalid user ID.");
            }
            return workspaceDao.fetchBookingsByUser(targetUserId);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load bookings.", ex);
        }
    }

    public int clearBookingsByUser(String userId, UserRecord authenticatedUser) {
        String targetUserId = resolveTargetUserId(userId, authenticatedUser);

        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                WorkspaceDao workspaceDao = new WorkspaceDao(connection);

                if (!workspaceDao.userExists(targetUserId)) {
                    throw new IllegalArgumentException("Invalid user ID.");
                }

                Map<String, Integer> bookingCounts = workspaceDao.fetchBookingCountsByUser(targetUserId);
                int deletedCount = workspaceDao.deleteBookingsByUser(targetUserId);

                for (Map.Entry<String, Integer> entry : bookingCounts.entrySet()) {
                    workspaceDao.restoreSeats(entry.getKey(), entry.getValue());
                }

                connection.commit();
                return deletedCount;
            } catch (IllegalArgumentException | SQLException ex) {
                connection.rollback();
                if (ex instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw ex;
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to clear booking history.", ex);
        }
    }

    public byte[] generateReceipt(String bookingId, UserRecord authenticatedUser) {
        BookingDetails bookingDetails = getAuthorizedBooking(bookingId, authenticatedUser);
        return receiptService.generateReceipt(bookingDetails);
    }

    public String generateEmailPreview(String bookingId, UserRecord authenticatedUser) {
        BookingDetails bookingDetails = getAuthorizedBooking(bookingId, authenticatedUser);
        return bookingNotificationService.buildEmailPreview(bookingDetails);
    }

    private void validateBookingRequest(BookingRequest request, UserRecord authenticatedUser) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required.");
        }
        if (authenticatedUser == null && (request.getName() == null || request.getName().trim().isEmpty())) {
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

    private BookingDetails getAuthorizedBooking(String bookingId, UserRecord authenticatedUser) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID is required.");
        }

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            BookingDetails bookingDetails = workspaceDao.fetchBookingDetails(bookingId.trim());
            if (bookingDetails == null) {
                throw new IllegalArgumentException("Booking not found.");
            }
            if (authenticatedUser != null && !bookingDetails.getUserId().equals(authenticatedUser.getUserId())) {
                throw new IllegalArgumentException("You can only access your own bookings.");
            }
            return bookingDetails;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load booking details.", ex);
        }
    }

    private String resolveTargetUserId(String requestedUserId, UserRecord authenticatedUser) {
        if (authenticatedUser != null) {
            return authenticatedUser.getUserId();
        }
        if (requestedUserId == null || requestedUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required.");
        }
        return requestedUserId.trim();
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
