package com.parking.model;

import com.parking.enums.VehicleType;

/**
 * Represents a motorbike/two-wheeler vehicle.
 * Motorbikes require the smallest parking spots.
 */
public class Motorbike extends Vehicle {

    public Motorbike(String licensePlate, String ownerName) {
        super(licensePlate, ownerName);
    }

    public Motorbike(String licensePlate) {
        super(licensePlate, "Unknown");
    }

    @Override
    public VehicleType getType() {
        return VehicleType.MOTORBIKE;
    }
}

