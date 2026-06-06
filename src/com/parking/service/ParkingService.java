package com.parking.service;

import com.parking.enums.VehicleType;
import com.parking.exception.InvalidTicketException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.VehicleAlreadyParkedException;
import com.parking.factory.VehicleFactory;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.model.Vehicle;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Main service for parking operations.
 * Orchestrates parking lot, tickets, and vehicle management.
 */
public class ParkingService {

    private final ParkingLot parkingLot;
    private final TicketService ticketService;

    public ParkingService() {
        this.parkingLot = ParkingLot.getInstance();
        this.ticketService = new TicketService();
    }

    public ParkingService(TicketService ticketService) {
        this.parkingLot = ParkingLot.getInstance();
        this.ticketService = ticketService;
    }

    /**
     * Initialize the parking lot with default configuration
     *
     * @param name Parking lot name
     */
    public void initializeParkingLot(String name) {
        parkingLot.initialize(name);
    }

    /**
     * Initialize parking lot with custom configuration
     *
     * @param name Parking lot name
     * @param numFloors Number of floors
     * @param motorbikeSpots Motorbike spots per floor
     * @param carSpots Car spots per floor
     * @param truckSpots Truck spots per floor
     */
    public void initializeParkingLot(String name, int numFloors,
                                     int motorbikeSpots, int carSpots, int truckSpots) {
        parkingLot.initialize(name, numFloors, motorbikeSpots, carSpots, truckSpots);
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        // Check if vehicle is already parked
        if (ticketService.hasActiveTicket(vehicle.getLicensePlate())) {
            throw new VehicleAlreadyParkedException(vehicle.getLicensePlate());
        }

        // Find available spot
        ParkingSpot spot = parkingLot.findAvailableSpot(vehicle.getType());
        if (spot == null) {
            throw new ParkingFullException(vehicle.getType());
        }

        // Park the vehicle
        spot.parkVehicle(vehicle);

        // Generate and return ticket
        return ticketService.generateTicket(vehicle, spot);
    }

    public ParkingTicket parkVehicle(VehicleType vehicleType, String licensePlate, String ownerName) {
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate, ownerName);
        return parkVehicle(vehicle);
    }

    public ParkingTicket parkVehicle(String vehicleType, String licensePlate, String ownerName) {
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate, ownerName);
        return parkVehicle(vehicle);
    }

    public ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        // Check if vehicle is already parked
        if (ticketService.hasActiveTicket(vehicle.getLicensePlate())) {
            throw new VehicleAlreadyParkedException(vehicle.getLicensePlate());
        }

        // Find available spot
        ParkingSpot spot = parkingLot.findAvailableSpot(vehicle.getType());
        if (spot == null) {
            throw new ParkingFullException(vehicle.getType());
        }

        // Park the vehicle
        spot.parkVehicle(vehicle);

        // Generate and return ticket with custom time
        return ticketService.generateTicket(vehicle, spot, entryTime);
    }

    public ExitResult exitParking(String ticketId) {
        return exitParking(ticketId, LocalDateTime.now());
    }

    public ExitResult exitParking(String ticketId, LocalDateTime exitTime) {
        // Get ticket
        ParkingTicket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException(ticketId);
        }

        // Calculate fee
        double fee = ticketService.processExit(ticketId, exitTime);

        // Free the parking spot
        ParkingSpot spot = ticket.getSpot();
        spot.removeVehicle();

        // Complete the ticket
        ticketService.completeTicket(ticketId);

        return new ExitResult(ticket, fee);
    }

    public ExitResult exitParkingByLicensePlate(String licensePlate) {
        ParkingTicket ticket = ticketService.getActiveTicketByLicensePlate(licensePlate);
        if (ticket == null) {
            throw new InvalidTicketException("No active ticket for license plate: " + licensePlate);
        }
        return exitParking(ticket.getTicketId());
    }

    public String getParkingStatus() {
        return parkingLot.getStatusSummary();
    }

    public int getAvailableSpots(VehicleType vehicleType) {
        return parkingLot.getAvailableSpotCount(vehicleType);
    }

    public boolean isParkingAvailable(VehicleType vehicleType) {
        return parkingLot.hasAvailableSpot(vehicleType);
    }

    public ParkingTicket getTicket(String ticketId) {
        return ticketService.getTicketById(ticketId);
    }


    public String getFeeBreakdown(String ticketId) {
        return ticketService.getFeeBreakdown(ticketId);
    }

    public Map<String, ParkingTicket> getActiveTickets() {
        return ticketService.getActiveTickets();
    }

    public int getActiveTicketCount() {
        return ticketService.getActiveTicketCount();
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public TicketService getTicketService() {
        return ticketService;
    }

    public static class ExitResult {
        private final ParkingTicket ticket;
        private final double fee;

        public ExitResult(ParkingTicket ticket, double fee) {
            this.ticket = ticket;
            this.fee = fee;
        }

        public ParkingTicket getTicket() {
            return ticket;
        }

        public double getFee() {
            return fee;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n╔═══════════════════════════════════════════╗\n");
            sb.append("║           EXIT RECEIPT                    ║\n");
            sb.append("╠═══════════════════════════════════════════╣\n");
            sb.append(String.format("║ Ticket ID    : %-26s║\n", ticket.getTicketId()));
            sb.append(String.format("║ Vehicle      : %-26s║\n", ticket.getVehicle().getType().getDisplayName()));
            sb.append(String.format("║ License Plate: %-26s║\n", ticket.getVehicle().getLicensePlate()));
            sb.append(String.format("║ Entry Time   : %-26s║\n", ticket.getFormattedEntryTime()));
            sb.append(String.format("║ Exit Time    : %-26s║\n", ticket.getFormattedExitTime()));
            sb.append(String.format("║ Duration     : %-26s║\n", ticket.getDurationInHours() + " hour(s)"));
            sb.append("╠═══════════════════════════════════════════╣\n");
            sb.append(String.format("║ TOTAL AMOUNT : ₹%-25.2f║\n", fee));
            sb.append("╠═══════════════════════════════════════════╣\n");
            sb.append("║           THANK YOU! VISIT AGAIN          ║\n");
            sb.append("╚═══════════════════════════════════════════╝\n");
            return sb.toString();
        }
    }
}

