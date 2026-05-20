package com.coworking.model;

import java.util.List;

public class Workspace {
    private final String workspaceId;
    private final String name;
    private final String location;
    private final int capacity;
    private final int availableSeats;
    private final int pricePerHour;
    private final List<String> amenities;
    private final double averageRating;
    private final int reviewCount;

    public Workspace(
        String workspaceId,
        String name,
        String location,
        int capacity,
        int availableSeats,
        int pricePerHour,
        List<String> amenities,
        double averageRating,
        int reviewCount
    ) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availableSeats = availableSeats;
        this.pricePerHour = pricePerHour;
        this.amenities = amenities;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }
}
