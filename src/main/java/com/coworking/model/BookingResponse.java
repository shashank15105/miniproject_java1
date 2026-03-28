package com.coworking.model;

public class BookingResponse {
    private final boolean success;
    private final String message;
    private final String bookingId;
    private final int totalPrice;
    private final String userId;
    private final String name;

    public BookingResponse(boolean success, String message, String bookingId, int totalPrice, String userId, String name) {
        this.success = success;
        this.message = message;
        this.bookingId = bookingId;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
