package com.parking.enums;

/**
 * Enum representing different types of vehicles supported by the parking system.
 * New vehicle types can be added here to extend the system.
 */
public enum VehicleType {
    MOTORBIKE("Motorbike", 1),
    CAR("Car", 2),
    TRUCK("Truck", 3);

    private final String displayName;
    private final int spotSize;

    VehicleType(String displayName, int spotSize) {
        this.displayName = displayName;
        this.spotSize = spotSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSpotSize() {
        return spotSize;
    }

    /**
     * Get VehicleType from user input (case-insensitive)
     * @param input User input string
     * @return VehicleType or null if not found
     */
    public static VehicleType fromString(String input) {
        if (input == null) return null;
        for (VehicleType type : VehicleType.values()) {
            if (type.name().equalsIgnoreCase(input) ||
                type.displayName.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}

