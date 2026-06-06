package com.parking.model;

import com.parking.enums.SpotStatus;
import com.parking.enums.VehicleType;

/**
 * Represents a single parking spot in the parking lot.
 * Each spot is designated for a specific vehicle type.
 */
public class ParkingSpot {
    private final String spotId;
    private final int floorNumber;
    private final VehicleType vehicleType; // Type of vehicle this spot can accommodate
    private SpotStatus status;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, int floorNumber, VehicleType vehicleType) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.vehicleType = vehicleType;
        this.status = SpotStatus.AVAILABLE;
        this.parkedVehicle = null;
    }

    /**
     * Check if this spot can accommodate the given vehicle type
     * @param type Vehicle type to check
     * @return true if compatible
     */
    public boolean canFitVehicle(VehicleType type) {
        return this.vehicleType == type;
    }

    /**
     * Park a vehicle in this spot
     * @param vehicle Vehicle to park
     * @return true if parked successfully
     */
    public boolean parkVehicle(Vehicle vehicle) {
        if (!isAvailable()) {
            return false;
        }
        if (!canFitVehicle(vehicle.getType())) {
            return false;
        }
        this.parkedVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
        return true;
    }

    /**
     * Remove vehicle from this spot
     * @return The vehicle that was parked, or null if empty
     */
    public Vehicle removeVehicle() {
        Vehicle vehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        this.status = SpotStatus.AVAILABLE;
        return vehicle;
    }

    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    @Override
    public String toString() {
        String vehicleInfo = parkedVehicle != null ?
            parkedVehicle.getLicensePlate() : "Empty";
        return String.format("Spot[%s] Floor:%d Type:%s Status:%s Vehicle:%s",
            spotId, floorNumber, vehicleType.getDisplayName(),
            status.getDisplayName(), vehicleInfo);
    }
}

