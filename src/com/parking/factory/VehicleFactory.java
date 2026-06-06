package com.parking.factory;

import com.parking.enums.VehicleType;
import com.parking.model.Car;
import com.parking.model.Motorbike;
import com.parking.model.Truck;
import com.parking.model.Vehicle;

/**
 * Factory class for creating Vehicle instances.
 * Implements the Factory Design Pattern for vehicle creation.
 */
public class VehicleFactory {

    /**
     * Create a vehicle based on type, license plate, and owner name
     *
     * @param type Vehicle type enum
     * @param licensePlate License plate number
     * @param ownerName Owner's name
     * @return Vehicle instance
     * @throws IllegalArgumentException if vehicle type is null or unsupported
     */
    public static Vehicle createVehicle(VehicleType type, String licensePlate, String ownerName) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }

        switch (type) {
            case MOTORBIKE:
                return new Motorbike(licensePlate, ownerName);
            case CAR:
                return new Car(licensePlate, ownerName);
            case TRUCK:
                return new Truck(licensePlate, ownerName);
            default:
                throw new IllegalArgumentException("Unsupported vehicle type: " + type);
        }
    }

    /**
     * Create a vehicle with default owner name
     *
     * @param type Vehicle type enum
     * @param licensePlate License plate number
     * @return Vehicle instance
     */
    public static Vehicle createVehicle(VehicleType type, String licensePlate) {
        return createVehicle(type, licensePlate, "Unknown");
    }

    /**
     * Create a vehicle from string type (user input)
     *
     * @param typeString Vehicle type as string (case-insensitive)
     * @param licensePlate License plate number
     * @param ownerName Owner's name
     * @return Vehicle instance
     * @throws IllegalArgumentException if type string is invalid
     */
    public static Vehicle createVehicle(String typeString, String licensePlate, String ownerName) {
        VehicleType type = VehicleType.fromString(typeString);
        if (type == null) {
            throw new IllegalArgumentException("Invalid vehicle type: " + typeString +
                ". Valid types are: MOTORBIKE, CAR, TRUCK");
        }
        return createVehicle(type, licensePlate, ownerName);
    }

    /**
     * Create a vehicle from string type with default owner
     *
     * @param typeString Vehicle type as string
     * @param licensePlate License plate number
     * @return Vehicle instance
     */
    public static Vehicle createVehicle(String typeString, String licensePlate) {
        return createVehicle(typeString, licensePlate, "Unknown");
    }
}

