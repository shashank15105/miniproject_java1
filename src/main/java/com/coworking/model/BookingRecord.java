package com.coworking.model;

import java.time.LocalDateTime;

public class BookingRecord {
    private final String bookingId;
    private final String userId;
    private final String workspaceId;
    private final String workspaceName;
    private final String location;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int totalPrice;

    public BookingRecord(
        String bookingId,
        String userId,
        String workspaceId,
        String workspaceName,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalPrice
    ) {
        this.bookingId = bookingId;
        this.userId = userId;
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
