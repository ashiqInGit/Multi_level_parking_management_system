package com.parking.model;

import com.parking.enums.VehicleType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a parking ticket issued when a vehicle enters the parking lot.
 * Contains all information needed for fee calculation on exit.
 */
public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    private boolean isPaid;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParkingTicket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = generateTicketId();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.fee = 0.0;
        this.isPaid = false;
    }

    /**
     * Constructor with custom entry time (useful for testing)
     */
    public ParkingTicket(Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        this.ticketId = generateTicketId();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.fee = 0.0;
        this.isPaid = false;
    }

    /**
     * Generate a unique ticket ID
     */
    private String generateTicketId() {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TKT-" + uuid;
    }

    /**
     * Mark the ticket as exited and calculate duration
     */
    public void markExit() {
        this.exitTime = LocalDateTime.now();
    }

    /**
     * Mark exit with specific time (useful for testing)
     */
    public void markExit(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    /**
     * Get parking duration in minutes
     */
    public long getDurationInMinutes() {
        LocalDateTime end = exitTime != null ? exitTime : LocalDateTime.now();
        return java.time.Duration.between(entryTime, end).toMinutes();
    }

    /**
     * Get parking duration in hours (rounded up)
     */
    public int getDurationInHours() {
        long minutes = getDurationInMinutes();
        return (int) Math.ceil(minutes / 60.0);
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public VehicleType getVehicleType() {
        return vehicle.getType();
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void markPaid() {
        this.isPaid = true;
    }

    /**
     * Get formatted entry time
     */
    public String getFormattedEntryTime() {
        return entryTime.format(FORMATTER);
    }

    /**
     * Get formatted exit time
     */
    public String getFormattedExitTime() {
        return exitTime != null ? exitTime.format(FORMATTER) : "Not exited";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════╗\n");
        sb.append("║         PARKING TICKET                 ║\n");
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append(String.format("║ Ticket ID  : %-25s║\n", ticketId));
        sb.append(String.format("║ Vehicle    : %-25s║\n", vehicle.getType().getDisplayName()));
        sb.append(String.format("║ License    : %-25s║\n", vehicle.getLicensePlate()));
        sb.append(String.format("║ Spot       : %-25s║\n", spot.getSpotId()));
        sb.append(String.format("║ Entry Time : %-25s║\n", getFormattedEntryTime()));
        if (exitTime != null) {
            sb.append(String.format("║ Exit Time  : %-25s║\n", getFormattedExitTime()));
            sb.append(String.format("║ Duration   : %-25s║\n", getDurationInHours() + " hour(s)"));
            sb.append(String.format("║ Fee        : ₹%-24.2f║\n", fee));
            sb.append(String.format("║ Status     : %-25s║\n", isPaid ? "PAID" : "UNPAID"));
        }
        sb.append("╚════════════════════════════════════════╝\n");
        return sb.toString();
    }

    /**
     * Short summary for display in lists
     */
    public String toShortString() {
        return String.format("[%s] %s - %s @ %s",
            ticketId, vehicle.getLicensePlate(),
            vehicle.getType().getDisplayName(), spot.getSpotId());
    }
}

