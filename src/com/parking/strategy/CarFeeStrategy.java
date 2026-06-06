package com.parking.strategy;

import com.parking.model.ParkingTicket;

/**
 * Fee calculation strategy for Cars.
 * Base rate: ₹20 for first hour
 * Additional: ₹10 per hour
 */
public class CarFeeStrategy implements FeeCalculationStrategy {

    private static final double BASE_RATE = 20.0;
    private static final double HOURLY_RATE = 10.0;

    @Override
    public double calculateFee(ParkingTicket ticket) {
        int hours = ticket.getDurationInHours();
        
        // Minimum 1 hour charge
        if (hours <= 0) {
            hours = 1;
        }
        
        // Base rate for first hour + additional hours * hourly rate
        if (hours <= 1) {
            return BASE_RATE;
        }
        
        return BASE_RATE + ((hours - 1) * HOURLY_RATE);
    }

    @Override
    public double getBaseRate() {
        return BASE_RATE;
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }

    @Override
    public String getStrategyName() {
        return "Car Fee Strategy";
    }

    @Override
    public String toString() {
        return String.format("%s [Base: ₹%.2f, Hourly: ₹%.2f]", 
            getStrategyName(), BASE_RATE, HOURLY_RATE);
    }
}

