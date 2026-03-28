package com.coworking.model;

public class Workspace {
    private final String workspaceId;
    private final String name;
    private final String location;
    private final int capacity;
    private final int availableSeats;
    private final int pricePerHour;

    public Workspace(
        String workspaceId,
        String name,
        String location,
        int capacity,
        int availableSeats,
        int pricePerHour
    ) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availableSeats = availableSeats;
        this.pricePerHour = pricePerHour;
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
}
