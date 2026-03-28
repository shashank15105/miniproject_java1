package com.coworking.controller;

import com.coworking.model.BookingRecord;
import com.coworking.model.BookingRequest;
import com.coworking.model.BookingResponse;
import com.coworking.model.Workspace;
import com.coworking.service.WorkspaceService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
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
    public ResponseEntity<?> bookWorkspace(@RequestBody BookingRequest request) {
        try {
            BookingResponse response = workspaceService.bookWorkspace(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(@RequestParam String userId) {
        try {
            List<BookingRecord> bookings = workspaceService.getBookingsByUser(userId);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }
}
