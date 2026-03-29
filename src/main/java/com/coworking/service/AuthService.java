package com.coworking.service;

import com.coworking.config.DatabaseManager;
import com.coworking.dao.WorkspaceDao;
import com.coworking.model.AuthResponse;
import com.coworking.model.LoginRequest;
import com.coworking.model.SignupRequest;
import com.coworking.model.UserRecord;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String SESSION_USER_ID = "coworking_user_id";
    private final DatabaseManager databaseManager;

    public AuthService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public AuthResponse signup(SignupRequest request, HttpSession session) {
        if (request == null) {
            throw new IllegalArgumentException("Signup details are required.");
        }

        String name = normalizeRequired(request.getName(), "Name is required.");
        String email = normalizeRequired(request.getEmail(), "Email is required.");
        String phone = normalizeOptional(request.getPhone(), "NA");

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            UserRecord existingUser = workspaceDao.findUserByEmail(email);

            if (existingUser != null) {
                bindUserToSession(session, existingUser);
                return toAuthResponse(existingUser, "Welcome back. You are already registered.");
            }

            UserRecord createdUser = workspaceDao.createUser(generateUserId(), name, email, phone);
            bindUserToSession(session, createdUser);
            return toAuthResponse(createdUser, "Account created successfully.");
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to complete signup.", ex);
        }
    }

    public AuthResponse login(LoginRequest request, HttpSession session) {
        if (request == null) {
            throw new IllegalArgumentException("Login details are required.");
        }

        String email = normalizeRequired(request.getEmail(), "Email is required.");
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            UserRecord user = workspaceDao.findUserByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("No account found for that email.");
            }

            bindUserToSession(session, user);
            return toAuthResponse(user, "Logged in successfully.");
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to complete login.", ex);
        }
    }

    public AuthResponse getCurrentUser(HttpSession session) {
        UserRecord user = getCurrentUserRecord(session);
        if (user == null) {
            return new AuthResponse(false, "No active session.", null, null, null, null);
        }
        return toAuthResponse(user, "Authenticated.");
    }

    public UserRecord getCurrentUserRecord(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userIdValue = session.getAttribute(SESSION_USER_ID);
        if (!(userIdValue instanceof String userId) || userId.isBlank()) {
            return null;
        }

        try (Connection connection = databaseManager.getConnection()) {
            WorkspaceDao workspaceDao = new WorkspaceDao(connection);
            return workspaceDao.findUserById(userId);
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to read the current session.", ex);
        }
    }

    public AuthResponse logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return new AuthResponse(false, "Logged out successfully.", null, null, null, null);
    }

    private void bindUserToSession(HttpSession session, UserRecord user) {
        session.setAttribute(SESSION_USER_ID, user.getUserId());
    }

    private AuthResponse toAuthResponse(UserRecord user, String message) {
        return new AuthResponse(
            true,
            message,
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getPhone()
        );
    }

    private String normalizeRequired(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim();
    }

    private String normalizeOptional(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private String generateUserId() {
        return "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
