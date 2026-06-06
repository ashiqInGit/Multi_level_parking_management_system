package com.parking.model;

import com.parking.enums.VehicleType;

/**
 * Represents a truck/heavy vehicle.
 * Trucks require the largest parking spots.
 */
public class Truck extends Vehicle {

    public Truck(String licensePlate, String ownerName) {
        super(licensePlate, ownerName);
    }

    public Truck(String licensePlate) {
        super(licensePlate, "Unknown");
    }

    @Override
    public VehicleType getType() {
        return VehicleType.TRUCK;
    }
}

