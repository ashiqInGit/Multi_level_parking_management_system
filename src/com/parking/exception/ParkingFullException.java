package com.parking.exception;

import com.parking.enums.VehicleType;

/**
 * Exception thrown when parking lot has no available spots for a vehicle type.
 */
public class ParkingFullException extends RuntimeException {
    private final VehicleType vehicleType;

    public ParkingFullException(VehicleType vehicleType) {
        super(String.format("No available parking spots for %s",
            vehicleType.getDisplayName()));
        this.vehicleType = vehicleType;
    }

    public ParkingFullException(String message) {
        super(message);
        this.vehicleType = null;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

