package com.parking.model;

import com.parking.enums.VehicleType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single floor in the multi-level parking lot.
 * Each floor contains multiple parking spots for different vehicle types.
 */
public class ParkingFloor {
    private final int floorNumber;
    private final String floorName;
    private final Map<VehicleType, List<ParkingSpot>> spotsByType;
    private final List<ParkingSpot> allSpots;

    public ParkingFloor(int floorNumber, int motorbikeSpots, int carSpots, int truckSpots) {
        this.floorNumber = floorNumber;
        this.floorName = "Floor-" + floorNumber;
        this.spotsByType = new HashMap<>();
        this.allSpots = new ArrayList<>();

        // Initialize spots for each vehicle type
        initializeSpots(VehicleType.MOTORBIKE, motorbikeSpots);
        initializeSpots(VehicleType.CAR, carSpots);
        initializeSpots(VehicleType.TRUCK, truckSpots);
    }

    /**
     * Initialize parking spots for a specific vehicle type
     */
    private void initializeSpots(VehicleType type, int count) {
        List<ParkingSpot> spots = new ArrayList<>();
        String prefix = getSpotPrefix(type);

        for (int i = 1; i <= count; i++) {
            String spotId = String.format("%s%d-%s%02d", "F", floorNumber, prefix, i);
            ParkingSpot spot = new ParkingSpot(spotId, floorNumber, type);
            spots.add(spot);
            allSpots.add(spot);
        }
        spotsByType.put(type, spots);
    }

    /**
     * Get spot ID prefix based on vehicle type
     */
    private String getSpotPrefix(VehicleType type) {
        switch (type) {
            case MOTORBIKE: return "M";
            case CAR: return "C";
            case TRUCK: return "T";
            default: return "X";
        }
    }

    /**
     * Find an available spot for the given vehicle type
     * @param type Vehicle type
     * @return Available ParkingSpot or null if none available
     */
    public ParkingSpot findAvailableSpot(VehicleType type) {
        List<ParkingSpot> spots = spotsByType.get(type);
        if (spots == null) return null;

        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Get count of available spots for a vehicle type
     */
    public int getAvailableSpotCount(VehicleType type) {
        List<ParkingSpot> spots = spotsByType.get(type);
        if (spots == null) return 0;

        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) count++;
        }
        return count;
    }

    /**
     * Get total spots for a vehicle type
     */
    public int getTotalSpotCount(VehicleType type) {
        List<ParkingSpot> spots = spotsByType.get(type);
        return spots != null ? spots.size() : 0;
    }

    /**
     * Get all spots on this floor
     */
    public List<ParkingSpot> getAllSpots() {
        return new ArrayList<>(allSpots);
    }

    /**
     * Get spots by vehicle type
     */
    public List<ParkingSpot> getSpotsByType(VehicleType type) {
        List<ParkingSpot> spots = spotsByType.get(type);
        return spots != null ? new ArrayList<>(spots) : new ArrayList<>();
    }

    /**
     * Find a spot by its ID
     */
    public ParkingSpot findSpotById(String spotId) {
        for (ParkingSpot spot : allSpots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Check if floor has any available spots for the given vehicle type
     */
    public boolean hasAvailableSpot(VehicleType type) {
        return getAvailableSpotCount(type) > 0;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getFloorName() {
        return floorName;
    }

    /**
     * Get floor status summary
     */
    public String getStatusSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n=== %s ===\n", floorName));

        for (VehicleType type : VehicleType.values()) {
            int available = getAvailableSpotCount(type);
            int total = getTotalSpotCount(type);
            sb.append(String.format("  %s: %d/%d available\n",
                type.getDisplayName(), available, total));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("ParkingFloor[%s] - Total Spots: %d",
            floorName, allSpots.size());
    }
}

