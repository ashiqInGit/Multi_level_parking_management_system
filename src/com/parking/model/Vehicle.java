package com.parking.model;

import com.parking.enums.VehicleType;

/**
 * Abstract base class for all vehicles in the parking system.
 * Uses Template Method pattern - subclasses define their specific type.
 */
public abstract class Vehicle {
    private final String licensePlate;
    private final String ownerName;

    public Vehicle(String licensePlate, String ownerName) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }
        this.licensePlate = licensePlate.toUpperCase().trim();
        this.ownerName = ownerName != null ? ownerName.trim() : "Unknown";
    }

    public abstract VehicleType getType();

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - Owner: %s",
            getType().getDisplayName(), licensePlate, ownerName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Vehicle)) return false;
        Vehicle other = (Vehicle) obj;
        return licensePlate.equals(other.licensePlate);
    }

    @Override
    public int hashCode() {
        return licensePlate.hashCode();
    }
}

