package com.coworking.model;

public class AuthResponse {
    private final boolean authenticated;
    private final String message;
    private final String userId;
    private final String name;
    private final String email;
    private final String phone;

    public AuthResponse(
        boolean authenticated,
        String message,
        String userId,
        String name,
        String email,
        String phone
    ) {
        this.authenticated = authenticated;
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
