package com.parking.strategy;

import com.parking.model.ParkingTicket;

/**
 * Strategy interface for fee calculation.
 * Implements the Strategy Design Pattern for flexible pricing.
 *
 * Benefits:
 * - Different pricing strategies per vehicle type
 * - Easy to add new pricing models (weekend rates, membership discounts, etc.)
 * - Runtime strategy switching capability
 */
public interface FeeCalculationStrategy {

    /**
     * Calculate the parking fee for a ticket
     *
     * @param ticket The parking ticket with entry/exit times
     * @return Calculated fee amount
     */
    double calculateFee(ParkingTicket ticket);

    /**
     * Get the base rate for the first hour
     *
     * @return Base rate in currency units
     */
    double getBaseRate();

    /**
     * Get the hourly rate for additional hours
     *
     * @return Hourly rate in currency units
     */
    double getHourlyRate();

    /**
     * Get the display name for this strategy
     *
     * @return Strategy display name
     */
    String getStrategyName();
}

