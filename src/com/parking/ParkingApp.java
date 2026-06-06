package com.parking;

import com.parking.enums.VehicleType;
import com.parking.exception.InvalidTicketException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.VehicleAlreadyParkedException;
import com.parking.model.ParkingTicket;
import com.parking.service.ParkingLot;
import com.parking.service.ParkingService;
import com.parking.strategy.FeeCalculationStrategy;
import com.parking.strategy.FeeStrategyFactory;

import java.util.Map;
import java.util.Scanner;

/**
 * Main console application for the Multi-Level Parking Management System.
 * Provides interactive menu for parking operations.
 */
public class ParkingApp {

    private static final String BANNER = """
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║     MULTI-LEVEL PARKING MANAGEMENT SYSTEM                     ║
            ╚═══════════════════════════════════════════════════════════════╝
            """;

    private static final String MENU = """
            
            ┌───────────────────────────────────────┐
            │            MAIN MENU                  │
            ├───────────────────────────────────────┤
            │  1. Park Vehicle                      │
            │  2. Exit Parking (by Ticket ID)       │
            │  3. Exit Parking (by License Plate)   │
            │  4. View Parking Status               │
            │  5. View Parked Vehicles              │
            │  6. View Fee Structure                │
            │  7. Search Ticket                     │
            │  0. Exit Application                  │
            └───────────────────────────────────────┘
            """;

    private final ParkingService parkingService;
    private final Scanner scanner;
    private boolean isRunning;

    public ParkingApp() {
        this.parkingService = new ParkingService();
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        ParkingApp app = new ParkingApp();
        app.run();
    }

    /**
     * Run the application
     */
    public void run() {
        System.out.println(BANNER);

        // Initialize parking lot
        initializeParkingLot();

        // Main loop
        while (isRunning) {
            showMenu();
            int choice = getIntInput("Enter your choice: ");
            processChoice(choice);
        }

        System.out.println("\nThank you for using the Parking Management System!");
        System.out.println("Goodbye!\n");
        scanner.close();
    }

    /**
     * Initialize parking lot with user input or defaults
     */
    private void initializeParkingLot() {
        System.out.println("\n═══ PARKING LOT SETUP ═══\n");

        System.out.print("Use default configuration? (y/n): ");
        String useDefault = scanner.nextLine().trim().toLowerCase();

        if (useDefault.equals("y") || useDefault.equals("yes")) {
            parkingService.initializeParkingLot("City Center Parking");
            System.out.println("\n[SUCCESS] Parking lot initialized with default configuration:");
            System.out.println("  - Name: City Center Parking");
            System.out.println("  - 3 floors");
            System.out.println("  - 10 motorbike spots per floor");
            System.out.println("  - 10 car spots per floor");
            System.out.println("  - 5 truck spots per floor");
        } else {
            String name = getStringInput("Enter parking lot name: ");
            int floors = getPositiveIntInput("Enter number of floors: ");
            int motorbikeSpots = getPositiveIntInput("Enter motorbike spots per floor: ");
            int carSpots = getPositiveIntInput("Enter car spots per floor: ");
            int truckSpots = getPositiveIntInput("Enter truck spots per floor: ");

            parkingService.initializeParkingLot(name, floors,
                motorbikeSpots, carSpots, truckSpots);

            System.out.println("\n[SUCCESS] Parking lot initialized!");
        }
    }

    /**
     * Display main menu
     */
    private void showMenu() {
        System.out.println(MENU);
    }

    /**
     * Process user's menu choice
     */
    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                parkVehicle();
                break;
            case 2:
                exitByTicketId();
                break;
            case 3:
                exitByLicensePlate();
                break;
            case 4:
                viewParkingStatus();
                break;
            case 5:
                viewParkedVehicles();
                break;
            case 6:
                viewFeeStructure();
                break;
            case 7:
                searchTicket();
                break;
            case 0:
                isRunning = false;
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice. Please try again.");
        }
    }

    /**
     * Park a vehicle
     */
    private void parkVehicle() {
        System.out.println("\n=== PARK VEHICLE ===\n");

        // Show vehicle types
        System.out.println("Vehicle Types:");
        for (VehicleType type : VehicleType.values()) {
            int available = parkingService.getAvailableSpots(type);
            System.out.printf("  %d. %s (%d spots available)%n",
                type.ordinal() + 1, type.getDisplayName(), available);
        }

        // Get vehicle type
        int typeChoice = getIntInput("\nSelect vehicle type (1-3): ");
        VehicleType vehicleType = getVehicleTypeFromChoice(typeChoice);

        if (vehicleType == null) {
            System.out.println("\n[ERROR] Invalid vehicle type.");
            return;
        }

        // Check availability first
        if (!parkingService.isParkingAvailable(vehicleType)) {
            System.out.printf("\n[ERROR] Sorry, no parking spots available for %s.%n",
                vehicleType.getDisplayName());
            return;
        }

        // Get vehicle details
        String licensePlate = getStringInput("Enter license plate: ");
        if (licensePlate.isEmpty()) {
            System.out.println("\n[ERROR] License plate cannot be empty.");
            return;
        }

        String ownerName = getStringInput("Enter owner name (or press Enter to skip): ");
        if (ownerName.isEmpty()) {
            ownerName = "Unknown";
        }

        try {
            ParkingTicket ticket = parkingService.parkVehicle(vehicleType, licensePlate, ownerName);

            System.out.println("\n[SUCCESS] Vehicle parked successfully!");
            System.out.println(ticket.toString());
            System.out.println("\n[IMPORTANT] Please keep your ticket ID safe: " + ticket.getTicketId());

        } catch (VehicleAlreadyParkedException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (ParkingFullException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] Invalid input: " + e.getMessage());
        }
    }

    /**
     * Exit parking by ticket ID
     */
    private void exitByTicketId() {
        System.out.println("\n=== EXIT PARKING ===\n");

        String ticketId = getStringInput("Enter ticket ID: ").toUpperCase();
        if (ticketId.isEmpty()) {
            System.out.println("\n[ERROR] Ticket ID cannot be empty.");
            return;
        }

        processExit(ticketId);
    }

    /**
     * Exit parking by license plate
     */
    private void exitByLicensePlate() {
        System.out.println("\n=== EXIT PARKING ===\n");

        String licensePlate = getStringInput("Enter license plate: ").toUpperCase();
        if (licensePlate.isEmpty()) {
            System.out.println("\n[ERROR] License plate cannot be empty.");
            return;
        }

        try {
            ParkingTicket ticket = parkingService.getTicketService()
                .getActiveTicketByLicensePlate(licensePlate);

            if (ticket == null) {
                System.out.println("\n[ERROR] No active parking found for license plate: " + licensePlate);
                return;
            }

            processExit(ticket.getTicketId());

        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    /**
     * Process vehicle exit
     */
    private void processExit(String ticketId) {
        try {
            // Get ticket first to show fee breakdown
            ParkingTicket ticket = parkingService.getTicket(ticketId);
            if (ticket == null) {
                System.out.println("\n[ERROR] Ticket not found: " + ticketId);
                return;
            }

            // Process exit
            ParkingService.ExitResult result = parkingService.exitParking(ticketId);

            // Show receipt
            System.out.println(result.toString());

            // Show fee breakdown
            System.out.println(parkingService.getFeeBreakdown(ticketId));

            System.out.println("[SUCCESS] Payment processed successfully!");

        } catch (InvalidTicketException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[ERROR] Error processing exit: " + e.getMessage());
        }
    }

    /**
     * View parking status
     */
    private void viewParkingStatus() {
        System.out.println("\n=== PARKING STATUS ===");
        System.out.println(parkingService.getParkingStatus());
        System.out.printf("Currently parked vehicles: %d%n", parkingService.getActiveTicketCount());
    }

    /**
     * View all parked vehicles
     */
    private void viewParkedVehicles() {
        System.out.println("\n=== PARKED VEHICLES ===\n");

        Map<String, ParkingTicket> activeTickets = parkingService.getActiveTickets();

        if (activeTickets.isEmpty()) {
            System.out.println("No vehicles currently parked.");
            return;
        }

        System.out.printf("Total vehicles parked: %d%n%n", activeTickets.size());
        System.out.println("┌─────────────────┬──────────────┬────────────┬────────────────────────┐");
        System.out.println("│ Ticket ID       │ License Plate│ Type       │ Entry Time             │");
        System.out.println("├─────────────────┼──────────────┼────────────┼────────────────────────┤");

        for (ParkingTicket ticket : activeTickets.values()) {
            System.out.printf("│ %-15s │ %-12s │ %-10s │ %-22s │%n",
                ticket.getTicketId(),
                ticket.getVehicle().getLicensePlate(),
                ticket.getVehicleType().getDisplayName(),
                ticket.getFormattedEntryTime());
        }
        System.out.println("└─────────────────┴──────────────┴────────────┴────────────────────────┘");
    }

    /**
     * View fee structure
     */
    private void viewFeeStructure() {
        System.out.println("\n═══ FEE STRUCTURE ═══\n");

        System.out.println("┌────────────┬─────────────────────┬────────────────────────┐");
        System.out.println("│ Vehicle    │ Base Rate (1st hr)  │ Additional (per hour)  │");
        System.out.println("├────────────┼─────────────────────┼────────────────────────┤");

        for (VehicleType type : VehicleType.values()) {
            FeeCalculationStrategy strategy = FeeStrategyFactory.getStrategy(type);
            System.out.printf("│ %-10s │ ₹%-18.2f │ ₹%-21.2f │%n",
                type.getDisplayName(),
                strategy.getBaseRate(),
                strategy.getHourlyRate());
        }

        System.out.println("└────────────┴─────────────────────┴────────────────────────┘");

        System.out.println("\nNotes:");
        System.out.println("  • Minimum charge is the base rate (for duration ≤ 1 hour)");
        System.out.println("  • Partial hours are rounded up to the next full hour");
        System.out.println("  • Formula: Base Rate + (Additional Hours × Hourly Rate)");

        System.out.println("\nExamples:");
        System.out.println("  • Car parked for 30 mins = ₹20.00 (base rate only)");
        System.out.println("  • Car parked for 2 hours = ₹20.00 + ₹10.00 = ₹30.00");
        System.out.println("  • Car parked for 3.5 hours = ₹20.00 + (3 × ₹10.00) = ₹50.00");
    }

    /**
     * Search for a ticket
     */
    private void searchTicket() {
        System.out.println("\n═══ SEARCH TICKET ═══\n");

        System.out.println("Search by:");
        System.out.println("  1. Ticket ID");
        System.out.println("  2. License Plate");

        int choice = getIntInput("Enter choice (1-2): ");

        ParkingTicket ticket = null;

        if (choice == 1) {
            String ticketId = getStringInput("Enter ticket ID: ").toUpperCase();
            ticket = parkingService.getTicket(ticketId);
        } else if (choice == 2) {
            String licensePlate = getStringInput("Enter license plate: ").toUpperCase();
            ticket = parkingService.getTicketService().getActiveTicketByLicensePlate(licensePlate);
        } else {
            System.out.println("\n[ERROR] Invalid choice.");
            return;
        }

        if (ticket == null) {
            System.out.println("\n[ERROR] Ticket not found.");
        } else {
            System.out.println("\n[SUCCESS] Ticket found:");
            System.out.println(ticket.toString());
        }
    }

    // ===================================================================
    // INPUT HELPER METHODS
    // ===================================================================

    /**
     * Get string input from user
     */
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Get integer input from user
     */
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Get positive integer input from user
     */
    private int getPositiveIntInput(String prompt) {
        while (true) {
            int value = getIntInput(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Please enter a positive number.");
        }
    }

    /**
     * Convert user's choice to VehicleType
     */
    private VehicleType getVehicleTypeFromChoice(int choice) {
        VehicleType[] types = VehicleType.values();
        if (choice >= 1 && choice <= types.length) {
            return types[choice - 1];
        }
        return null;
    }
}

