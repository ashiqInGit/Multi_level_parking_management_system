package com.parking.service;

import com.parking.enums.VehicleType;
import com.parking.model.ParkingFloor;
import com.parking.model.ParkingSpot;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class representing the entire parking lot.
 * Implements the Singleton Design Pattern for centralized parking management.
 */
public class ParkingLot {

    // Singleton instance
    private static ParkingLot instance;

    private String name;
    private final List<ParkingFloor> floors;
    private boolean isInitialized;
    private static final int DEFAULT_FLOORS = 3;
    private static final int DEFAULT_MOTORBIKE_SPOTS = 10;
    private static final int DEFAULT_CAR_SPOTS = 10;
    private static final int DEFAULT_TRUCK_SPOTS = 5;

    /**
     * Private constructor for Singleton pattern
     */
    private ParkingLot() {
        this.floors = new ArrayList<>();
        this.isInitialized = false;
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    /**
     * Reset the singleton instance (useful for testing)
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    public void initialize(String name) {
        initialize(name, DEFAULT_FLOORS,
            DEFAULT_MOTORBIKE_SPOTS, DEFAULT_CAR_SPOTS, DEFAULT_TRUCK_SPOTS);
    }

    public void initialize(String name, int numFloors,
                          int motorbikeSpots, int carSpots, int truckSpots) {
        if (isInitialized) {
            throw new IllegalStateException("Parking lot is already initialized");
        }

        this.name = name;

        // Create floors
        for (int i = 1; i <= numFloors; i++) {
            ParkingFloor floor = new ParkingFloor(i, motorbikeSpots, carSpots, truckSpots);
            floors.add(floor);
        }

        this.isInitialized = true;
    }

    public ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        checkInitialized();

        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicleType);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public boolean hasAvailableSpot(VehicleType vehicleType) {
        return findAvailableSpot(vehicleType) != null;
    }

    public int getAvailableSpotCount(VehicleType vehicleType) {
        checkInitialized();

        int count = 0;
        for (ParkingFloor floor : floors) {
            count += floor.getAvailableSpotCount(vehicleType);
        }
        return count;
    }

    public int getTotalSpotCount(VehicleType vehicleType) {
        checkInitialized();

        int count = 0;
        for (ParkingFloor floor : floors) {
            count += floor.getTotalSpotCount(vehicleType);
        }
        return count;
    }

    public ParkingFloor getFloor(int floorNumber) {
        checkInitialized();

        if (floorNumber < 1 || floorNumber > floors.size()) {
            return null;
        }
        return floors.get(floorNumber - 1);
    }

    public List<ParkingFloor> getFloors() {
        return new ArrayList<>(floors);
    }

    public ParkingSpot findSpotById(String spotId) {
        checkInitialized();

        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findSpotById(spotId);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public String getStatusSummary() {
        checkInitialized();

        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  %s%s║\n", name, " ".repeat(52 - name.length())));
        sb.append("╠══════════════════════════════════════════════════════╣\n");

        // Overall summary
        sb.append("║  OVERALL AVAILABILITY:                               ║\n");
        for (VehicleType type : VehicleType.values()) {
            int available = getAvailableSpotCount(type);
            int total = getTotalSpotCount(type);
            String line = String.format("    %s: %d/%d spots available",
                type.getDisplayName(), available, total);
            sb.append(String.format("║  %-50s║\n", line));
        }
        sb.append("╠══════════════════════════════════════════════════════╣\n");

        // Per floor summary
        sb.append("║  FLOOR-WISE STATUS:                                  ║\n");
        for (ParkingFloor floor : floors) {
            sb.append(String.format("║  %-50s║\n", floor.getFloorName()));
            for (VehicleType type : VehicleType.values()) {
                int available = floor.getAvailableSpotCount(type);
                int total = floor.getTotalSpotCount(type);
                String line = String.format("    %s: %d/%d",
                    type.getDisplayName(), available, total);
                sb.append(String.format("║    %-48s║\n", line));
            }
        }
        sb.append("╚══════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    private void checkInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("Parking lot is not initialized. Call initialize() first.");
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public String getName() {
        return name;
    }


    public int getNumberOfFloors() {
        return floors.size();
    }

    @Override
    public String toString() {
        if (!isInitialized) {
            return "ParkingLot [Not Initialized]";
        }
        return String.format("ParkingLot[%s] - %d floors", name, floors.size());
    }
}

