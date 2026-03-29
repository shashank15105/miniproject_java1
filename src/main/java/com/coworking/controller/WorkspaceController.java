package com.coworking.controller;

import com.coworking.model.BookingRecord;
import com.coworking.model.BookingRequest;
import com.coworking.model.BookingResponse;
import com.coworking.model.UserRecord;
import com.coworking.service.AuthService;
import com.coworking.model.Workspace;
import com.coworking.service.WorkspaceService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final AuthService authService;

    public WorkspaceController(WorkspaceService workspaceService, AuthService authService) {
        this.workspaceService = workspaceService;
        this.authService = authService;
    }

    @GetMapping("/workspaces")
    public ResponseEntity<?> getWorkspaces() {
        try {
            List<Workspace> workspaces = workspaceService.getAvailableWorkspaces();
            return ResponseEntity.ok(workspaces);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookWorkspace(@RequestBody BookingRequest request, HttpSession session) {
        try {
            UserRecord authenticatedUser = authService.getCurrentUserRecord(session);
            BookingResponse response = workspaceService.bookWorkspace(request, authenticatedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(@RequestParam(required = false) String userId, HttpSession session) {
        try {
            UserRecord authenticatedUser = authService.getCurrentUserRecord(session);
            List<BookingRecord> bookings = workspaceService.getBookingsByUser(userId, authenticatedUser);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/bookings")
    public ResponseEntity<?> clearBookings(@RequestParam(required = false) String userId, HttpSession session) {
        try {
            UserRecord authenticatedUser = authService.getCurrentUserRecord(session);
            int deletedCount = workspaceService.clearBookingsByUser(userId, authenticatedUser);
            String message = deletedCount > 0
                ? "Booking history cleared successfully."
                : "No booking history found for this user.";
            return ResponseEntity.ok(Map.of("message", message, "deletedCount", deletedCount));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping(value = "/bookings/{bookingId}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadReceipt(@PathVariable String bookingId, HttpSession session) {
        try {
            UserRecord authenticatedUser = authService.getCurrentUserRecord(session);
            byte[] pdf = workspaceService.generateReceipt(bookingId, authenticatedUser);
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + bookingId + "-receipt.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping(value = "/bookings/{bookingId}/email-preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> previewEmail(@PathVariable String bookingId, HttpSession session) {
        try {
            UserRecord authenticatedUser = authService.getCurrentUserRecord(session);
            String html = workspaceService.generateEmailPreview(bookingId, authenticatedUser);
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }
}
