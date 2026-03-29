package com.coworking.controller;

import com.coworking.model.AuthResponse;
import com.coworking.model.LoginRequest;
import com.coworking.model.SignupRequest;
import com.coworking.service.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        return ResponseEntity.ok(authService.getCurrentUser(session));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request, HttpSession session) {
        try {
            return ResponseEntity.ok(authService.signup(request, session));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            return ResponseEntity.ok(authService.login(request, session));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        return ResponseEntity.ok(authService.logout(session));
    }
}
