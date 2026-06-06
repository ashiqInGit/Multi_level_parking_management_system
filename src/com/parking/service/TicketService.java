package com.parking.service;

import com.parking.enums.VehicleType;
import com.parking.exception.InvalidTicketException;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.model.Vehicle;
import com.parking.strategy.FeeCalculationStrategy;
import com.parking.strategy.FeeStrategyFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing parking tickets.
 * Handles ticket generation, storage, retrieval, and fee calculation.
 */
public class TicketService {

    // Active tickets mapped by ticket ID
    private final Map<String, ParkingTicket> activeTickets;

    // Tickets mapped by license plate for quick lookup
    private final Map<String, ParkingTicket> ticketsByLicensePlate;

    // Historical tickets (completed)
    private final Map<String, ParkingTicket> completedTickets;

    public TicketService() {
        this.activeTickets = new HashMap<>();
        this.ticketsByLicensePlate = new HashMap<>();
        this.completedTickets = new HashMap<>();
    }

    public ParkingTicket generateTicket(Vehicle vehicle, ParkingSpot spot) {
        ParkingTicket ticket = new ParkingTicket(vehicle, spot);
        activeTickets.put(ticket.getTicketId(), ticket);
        ticketsByLicensePlate.put(vehicle.getLicensePlate(), ticket);
        return ticket;
    }

    public ParkingTicket generateTicket(Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        ParkingTicket ticket = new ParkingTicket(vehicle, spot, entryTime);
        activeTickets.put(ticket.getTicketId(), ticket);
        ticketsByLicensePlate.put(vehicle.getLicensePlate(), ticket);
        return ticket;
    }

    public ParkingTicket getTicketById(String ticketId) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            ticket = completedTickets.get(ticketId);
        }
        return ticket;
    }

    public ParkingTicket getActiveTicketByLicensePlate(String licensePlate) {
        return ticketsByLicensePlate.get(licensePlate.toUpperCase().trim());
    }

    public boolean hasActiveTicket(String licensePlate) {
        return ticketsByLicensePlate.containsKey(licensePlate.toUpperCase().trim());
    }

    public double processExit(String ticketId) {
        return processExit(ticketId, LocalDateTime.now());
    }

    public double processExit(String ticketId, LocalDateTime exitTime) {
        ParkingTicket ticket = activeTickets.get(ticketId);

        if (ticket == null) {
            throw new InvalidTicketException(ticketId);
        }

        // Mark exit time
        ticket.markExit(exitTime);

        // Calculate fee using strategy pattern
        VehicleType vehicleType = ticket.getVehicleType();
        FeeCalculationStrategy strategy = FeeStrategyFactory.getStrategy(vehicleType);
        double fee = strategy.calculateFee(ticket);
        ticket.setFee(fee);

        return fee;
    }

    public ParkingTicket completeTicket(String ticketId) {
        ParkingTicket ticket = activeTickets.remove(ticketId);

        if (ticket == null) {
            throw new InvalidTicketException(ticketId);
        }

        ticket.markPaid();
        ticketsByLicensePlate.remove(ticket.getVehicle().getLicensePlate());
        completedTickets.put(ticketId, ticket);

        return ticket;
    }

    public String getFeeBreakdown(String ticketId) {
        ParkingTicket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException(ticketId);
        }

        VehicleType type = ticket.getVehicleType();
        FeeCalculationStrategy strategy = FeeStrategyFactory.getStrategy(type);

        int hours = ticket.getDurationInHours();
        if (hours <= 0) hours = 1;

        StringBuilder sb = new StringBuilder();
        sb.append("\n┌─────────────────────────────────────┐\n");
        sb.append("│         FEE BREAKDOWN               │\n");
        sb.append("├─────────────────────────────────────┤\n");
        sb.append(String.format("│ Vehicle Type : %-20s│\n", type.getDisplayName()));
        sb.append(String.format("│ Duration     : %-20s│\n", hours + " hour(s)"));
        sb.append(String.format("│ Base Rate    : ₹%-19.2f│\n", strategy.getBaseRate()));

        if (hours > 1) {
            int additionalHours = hours - 1;
            double additionalCharge = additionalHours * strategy.getHourlyRate();
            sb.append(String.format("│ Additional   : %d hrs × ₹%.0f = ₹%-6.2f│\n",
                additionalHours, strategy.getHourlyRate(), additionalCharge));
        }

        sb.append("├─────────────────────────────────────┤\n");
        sb.append(String.format("│ TOTAL FEE    : ₹%-19.2f│\n", ticket.getFee()));
        sb.append("└─────────────────────────────────────┘\n");

        return sb.toString();
    }

    public Map<String, ParkingTicket> getActiveTickets() {
        return new HashMap<>(activeTickets);
    }

    public int getActiveTicketCount() {
        return activeTickets.size();
    }

    public void clearAllTickets() {
        activeTickets.clear();
        ticketsByLicensePlate.clear();
        completedTickets.clear();
    }
}

