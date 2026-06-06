package com.parking.strategy;

import com.parking.enums.VehicleType;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory/Registry for fee calculation strategies.
 * Maps vehicle types to their corresponding fee strategies.
 *
 * This allows easy addition of new vehicle types and their pricing strategies.
 */
public class FeeStrategyFactory {

    private static final Map<VehicleType, FeeCalculationStrategy> strategies = new HashMap<>();

    // Initialize default strategies
    static {
        strategies.put(VehicleType.MOTORBIKE, new MotorbikeFeeStrategy());
        strategies.put(VehicleType.CAR, new CarFeeStrategy());
        strategies.put(VehicleType.TRUCK, new TruckFeeStrategy());
    }

    /**
     * Get the fee calculation strategy for a vehicle type
     *
     * @param vehicleType The vehicle type
     * @return Fee calculation strategy
     * @throws IllegalArgumentException if no strategy exists for the type
     */
    public static FeeCalculationStrategy getStrategy(VehicleType vehicleType) {
        FeeCalculationStrategy strategy = strategies.get(vehicleType);
        if (strategy == null) {
            throw new IllegalArgumentException("No fee strategy defined for vehicle type: " + vehicleType);
        }
        return strategy;
    }

    /**
     * Register a custom strategy for a vehicle type
     * Useful for extending the system with new vehicle types or custom pricing
     *
     * @param vehicleType The vehicle type
     * @param strategy The fee calculation strategy
     */
    public static void registerStrategy(VehicleType vehicleType, FeeCalculationStrategy strategy) {
        if (vehicleType == null || strategy == null) {
            throw new IllegalArgumentException("Vehicle type and strategy cannot be null");
        }
        strategies.put(vehicleType, strategy);
    }

    /**
     * Check if a strategy exists for a vehicle type
     *
     * @param vehicleType The vehicle type
     * @return true if strategy exists
     */
    public static boolean hasStrategy(VehicleType vehicleType) {
        return strategies.containsKey(vehicleType);
    }

    /**
     * Get all registered strategies
     *
     * @return Map of vehicle types to strategies
     */
    public static Map<VehicleType, FeeCalculationStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }
}

