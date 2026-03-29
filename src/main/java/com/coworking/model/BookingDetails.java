package com.coworking.model;

import java.time.LocalDateTime;

public class BookingDetails {
    private final String bookingId;
    private final String userId;
    private final String userName;
    private final String userEmail;
    private final String userPhone;
    private final String workspaceId;
    private final String workspaceName;
    private final String location;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int totalPrice;

    public BookingDetails(
        String bookingId,
        String userId,
        String userName,
        String userEmail,
        String userPhone,
        String workspaceId,
        String workspaceName,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalPrice
    ) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
