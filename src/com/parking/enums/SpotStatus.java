package com.parking.enums;

/**
 * Enum representing the status of a parking spot.
 */
public enum SpotStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    RESERVED("Reserved"), // For future extension
    MAINTENANCE("Maintenance"); // For future extension

    private final String displayName;

    SpotStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

