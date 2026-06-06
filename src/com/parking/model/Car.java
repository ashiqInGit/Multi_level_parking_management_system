package com.parking.model;

import com.parking.enums.VehicleType;

/**
 * Represents a car/four-wheeler vehicle.
 * Cars require medium-sized parking spots.
 */
public class Car extends Vehicle {

    public Car(String licensePlate, String ownerName) {
        super(licensePlate, ownerName);
    }

    public Car(String licensePlate) {
        super(licensePlate, "Unknown");
    }

    @Override
    public VehicleType getType() {
        return VehicleType.CAR;
    }
}

