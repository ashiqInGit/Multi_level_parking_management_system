package com.parking.exception;

/**
 * Exception thrown when a vehicle is already parked in the lot.
 */
public class VehicleAlreadyParkedException extends RuntimeException {
    private final String licensePlate;

    public VehicleAlreadyParkedException(String licensePlate) {
        super(String.format("Vehicle with license plate %s is already parked", licensePlate));
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}

