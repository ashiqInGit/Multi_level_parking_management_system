package com.parking.test.integration;

import com.parking.enums.VehicleType;
import com.parking.model.Car;
import com.parking.model.Motorbike;
import com.parking.model.ParkingTicket;
import com.parking.model.Truck;
import com.parking.service.ParkingLot;
import com.parking.service.ParkingService;
import com.parking.test.TestRunner;

import java.time.LocalDateTime;

/**
 * Integration tests for complete parking flow.
 */
public class ParkingSystemIntegrationTest {

    private final TestRunner runner;

    public ParkingSystemIntegrationTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("ParkingSystemIntegrationTest");

        // Complete flow tests
        runner.runTest("testCompleteParkingFlow", this::testCompleteParkingFlow);
        runner.runTest("testMultipleVehicleTypes", this::testMultipleVehicleTypes);
        runner.runTest("testParkExitParkAgain", this::testParkExitParkAgain);

        // Fee calculation scenarios
        runner.runTest("testMotorbike30MinFee", this::testMotorbike30MinFee);
        runner.runTest("testCar90MinFee", this::testCar90MinFee);
        runner.runTest("testTruck4HoursFee", this::testTruck4HoursFee);

        // Capacity management
        runner.runTest("testFillAndEmptyParking", this::testFillAndEmptyParking);
        runner.runTest("testDifferentVehicleTypesIndependent", this::testDifferentVehicleTypesIndependent);

        // Edge cases
        runner.runTest("testMultipleFloorsAllocation", this::testMultipleFloorsAllocation);
        runner.runTest("testTicketLookupByLicensePlate", this::testTicketLookupByLicensePlate);
    }

    private ParkingService setUp() {
        ParkingLot.resetInstance();
        ParkingService service = new ParkingService();
        service.initializeParkingLot("Integration Test Lot", 2, 3, 5, 2);
        return service;
    }

    // ===================================================================
    // COMPLETE FLOW TESTS
    // ===================================================================

    private void testCompleteParkingFlow() {
        ParkingService service = setUp();

        // Setup creates: 2 floors, 3 motorbike, 5 car, 2 truck per floor
        // Total car spots = 10
        int initialCarSpots = service.getAvailableSpots(VehicleType.CAR);

        // 1. Park vehicle
        Car car = new Car("KA01AB1234", "John Doe");
        ParkingTicket ticket = service.parkVehicle(car);

        runner.assertNotNull(ticket, "Step 1: Should get ticket");
        runner.assertEquals(initialCarSpots - 1, service.getAvailableSpots(VehicleType.CAR), "Step 1: Should reduce available spots by 1");

        // 2. Verify active ticket
        ParkingTicket foundTicket = service.getTicket(ticket.getTicketId());
        runner.assertNotNull(foundTicket, "Step 2: Should find ticket");
        runner.assertEquals("KA01AB1234", foundTicket.getVehicle().getLicensePlate(), "Step 2: License plate should match");

        // 3. Exit parking
        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());
        runner.assertNotNull(result, "Step 3: Should get exit result");
        runner.assertTrue(result.getFee() > 0, "Step 3: Fee should be positive");

        // 4. Verify spot freed
        runner.assertEquals(initialCarSpots, service.getAvailableSpots(VehicleType.CAR), "Step 4: Should restore available spots");
    }

    private void testMultipleVehicleTypes() {
        ParkingService service = setUp();

        // Park different vehicle types
        ParkingTicket bikeTicket = service.parkVehicle(new Motorbike("MH01MB0001"));
        ParkingTicket carTicket = service.parkVehicle(new Car("MH01CR0001"));
        ParkingTicket truckTicket = service.parkVehicle(new Truck("MH01TR0001"));

        runner.assertNotNull(bikeTicket, "Should park motorbike");
        runner.assertNotNull(carTicket, "Should park car");
        runner.assertNotNull(truckTicket, "Should park truck");

        // Verify counts
        runner.assertEquals(5, service.getAvailableSpots(VehicleType.MOTORBIKE), "Should have 5 bike spots left");
        runner.assertEquals(9, service.getAvailableSpots(VehicleType.CAR), "Should have 9 car spots left");
        runner.assertEquals(3, service.getAvailableSpots(VehicleType.TRUCK), "Should have 3 truck spots left");
    }

    private void testParkExitParkAgain() {
        ParkingService service = setUp();
        String licensePlate = "MH01XX1234";

        // First park
        ParkingTicket ticket1 = service.parkVehicle(new Car(licensePlate));
        runner.assertNotNull(ticket1, "First park should succeed");

        // Exit
        service.exitParking(ticket1.getTicketId());

        // Second park with same license plate
        ParkingTicket ticket2 = service.parkVehicle(new Car(licensePlate));
        runner.assertNotNull(ticket2, "Second park should succeed after exit");
        runner.assertFalse(ticket1.getTicketId().equals(ticket2.getTicketId()), "Should have different ticket IDs");
    }

    // ===================================================================
    // FEE CALCULATION SCENARIO TESTS
    // ===================================================================

    private void testMotorbike30MinFee() {
        ParkingService service = setUp();
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(30);

        ParkingTicket ticket = service.parkVehicle(new Motorbike("MB001"), entryTime);
        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());

        // 30 mins rounds up to 1 hour, base rate = 10
        runner.assertEquals(10.0, result.getFee(), 0.01, "30 min motorbike fee should be 10");
    }

    private void testCar90MinFee() {
        ParkingService service = setUp();
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(90);

        ParkingTicket ticket = service.parkVehicle(new Car("CAR001"), entryTime);
        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());

        // 90 mins rounds up to 2 hours = 20 + 10 = 30
        runner.assertEquals(30.0, result.getFee(), 0.01, "90 min car fee should be 30");
    }

    private void testTruck4HoursFee() {
        ParkingService service = setUp();
        LocalDateTime entryTime = LocalDateTime.now().minusHours(4);

        ParkingTicket ticket = service.parkVehicle(new Truck("TRK001"), entryTime);
        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());

        // 4 hours = 40 + (3 * 20) = 100
        runner.assertEquals(100.0, result.getFee(), 0.01, "4 hour truck fee should be 100");
    }

    // ===================================================================
    // CAPACITY MANAGEMENT TESTS
    // ===================================================================

    private void testFillAndEmptyParking() {
        ParkingLot.resetInstance();
        ParkingService service = new ParkingService();
        service.initializeParkingLot("Small Lot", 1, 0, 2, 0); // Only 2 car spots

        // Fill all spots
        ParkingTicket t1 = service.parkVehicle(new Car("CAR001"));
        ParkingTicket t2 = service.parkVehicle(new Car("CAR002"));

        runner.assertEquals(0, service.getAvailableSpots(VehicleType.CAR), "Should be full");
        runner.assertFalse(service.isParkingAvailable(VehicleType.CAR), "Should not have availability");

        // Exit one
        service.exitParking(t1.getTicketId());

        runner.assertEquals(1, service.getAvailableSpots(VehicleType.CAR), "Should have 1 spot");
        runner.assertTrue(service.isParkingAvailable(VehicleType.CAR), "Should have availability");

        // Exit second
        service.exitParking(t2.getTicketId());

        runner.assertEquals(2, service.getAvailableSpots(VehicleType.CAR), "Should have 2 spots");
    }

    private void testDifferentVehicleTypesIndependent() {
        ParkingLot.resetInstance();
        ParkingService service = new ParkingService();
        service.initializeParkingLot("Mixed Lot", 1, 2, 2, 1);

        // Fill all car spots
        service.parkVehicle(new Car("CAR001"));
        service.parkVehicle(new Car("CAR002"));

        // Cars full, but bikes/trucks should still be available
        runner.assertFalse(service.isParkingAvailable(VehicleType.CAR), "Cars should be full");
        runner.assertTrue(service.isParkingAvailable(VehicleType.MOTORBIKE), "Bikes should be available");
        runner.assertTrue(service.isParkingAvailable(VehicleType.TRUCK), "Trucks should be available");
    }

    // ===================================================================
    // EDGE CASE TESTS
    // ===================================================================

    private void testMultipleFloorsAllocation() {
        ParkingLot.resetInstance();
        ParkingService service = new ParkingService();
        service.initializeParkingLot("Multi Floor", 3, 1, 1, 1); // 1 spot per type per floor

        // Park 3 cars (should go to floor 1, 2, 3)
        ParkingTicket t1 = service.parkVehicle(new Car("CAR001"));
        ParkingTicket t2 = service.parkVehicle(new Car("CAR002"));
        ParkingTicket t3 = service.parkVehicle(new Car("CAR003"));

        runner.assertEquals(0, service.getAvailableSpots(VehicleType.CAR), "All car spots should be full");

        // Verify they're on different floors
        runner.assertTrue(
            t1.getSpot().getFloorNumber() != t2.getSpot().getFloorNumber() ||
            t2.getSpot().getFloorNumber() != t3.getSpot().getFloorNumber(),
            "Cars should be on different floors"
        );
    }

    private void testTicketLookupByLicensePlate() {
        ParkingService service = setUp();

        service.parkVehicle(new Car("CAR001"));
        service.parkVehicle(new Car("CAR002"));
        ParkingTicket target = service.parkVehicle(new Car("TARGET123"));

        ParkingTicket found = service.getTicketService().getActiveTicketByLicensePlate("TARGET123");

        runner.assertNotNull(found, "Should find ticket by license plate");
        runner.assertEquals(target.getTicketId(), found.getTicketId(), "Ticket IDs should match");
    }
}


