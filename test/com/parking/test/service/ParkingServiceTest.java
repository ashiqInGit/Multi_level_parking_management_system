package com.parking.test.service;

import com.parking.enums.VehicleType;
import com.parking.exception.InvalidTicketException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.VehicleAlreadyParkedException;
import com.parking.model.Car;
import com.parking.model.Motorbike;
import com.parking.model.ParkingTicket;
import com.parking.model.Truck;
import com.parking.service.ParkingLot;
import com.parking.service.ParkingService;
import com.parking.test.TestRunner;

import java.time.LocalDateTime;

/**
 * Tests for ParkingService.
 */
public class ParkingServiceTest {

    private final TestRunner runner;

    public ParkingServiceTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("ParkingServiceTest");

        // Park vehicle tests
        runner.runTest("testParkMotorbike", this::testParkMotorbike);
        runner.runTest("testParkCar", this::testParkCar);
        runner.runTest("testParkTruck", this::testParkTruck);
        runner.runTest("testParkVehicleReturnsTicket", this::testParkVehicleReturnsTicket);

        // Exception tests
        runner.runTest("testParkSameVehicleTwiceThrows", this::testParkSameVehicleTwiceThrows);
        runner.runTest("testParkWhenFullThrows", this::testParkWhenFullThrows);

        // Exit parking tests
        runner.runTest("testExitParkingByTicketId", this::testExitParkingByTicketId);
        runner.runTest("testExitParkingCalculatesFee", this::testExitParkingCalculatesFee);
        runner.runTest("testExitParkingFreesSpot", this::testExitParkingFreesSpot);
        runner.runTest("testExitInvalidTicketThrows", this::testExitInvalidTicketThrows);

        // Availability tests
        runner.runTest("testIsParkingAvailable", this::testIsParkingAvailable);
        runner.runTest("testGetAvailableSpots", this::testGetAvailableSpots);
    }

    private ParkingService setUp() {
        ParkingLot.resetInstance();
        ParkingService service = new ParkingService();
        service.initializeParkingLot("Test Lot", 1, 3, 3, 2);
        return service;
    }

    // ===================================================================
    // PARK VEHICLE TESTS
    // ===================================================================

    private void testParkMotorbike() {
        ParkingService service = setUp();
        Motorbike bike = new Motorbike("MH01AB1234");

        ParkingTicket ticket = service.parkVehicle(bike);

        runner.assertNotNull(ticket, "Should return ticket");
        runner.assertEquals(VehicleType.MOTORBIKE, ticket.getVehicleType(), "Ticket vehicle type should be MOTORBIKE");
    }

    private void testParkCar() {
        ParkingService service = setUp();
        Car car = new Car("MH01AB1234");

        ParkingTicket ticket = service.parkVehicle(car);

        runner.assertNotNull(ticket, "Should return ticket");
        runner.assertEquals(VehicleType.CAR, ticket.getVehicleType(), "Ticket vehicle type should be CAR");
    }

    private void testParkTruck() {
        ParkingService service = setUp();
        Truck truck = new Truck("MH01AB1234");

        ParkingTicket ticket = service.parkVehicle(truck);

        runner.assertNotNull(ticket, "Should return ticket");
        runner.assertEquals(VehicleType.TRUCK, ticket.getVehicleType(), "Ticket vehicle type should be TRUCK");
    }

    private void testParkVehicleReturnsTicket() {
        ParkingService service = setUp();
        Car car = new Car("MH01AB1234", "John");

        ParkingTicket ticket = service.parkVehicle(car);

        runner.assertNotNull(ticket.getTicketId(), "Ticket should have ID");
        runner.assertNotNull(ticket.getSpot(), "Ticket should have spot");
        runner.assertNotNull(ticket.getEntryTime(), "Ticket should have entry time");
        runner.assertEquals("MH01AB1234", ticket.getVehicle().getLicensePlate(), "Vehicle license plate should match");
    }

    // ===================================================================
    // EXCEPTION TESTS
    // ===================================================================

    private void testParkSameVehicleTwiceThrows() {
        ParkingService service = setUp();
        Car car = new Car("MH01AB1234");

        service.parkVehicle(car);

        runner.assertThrows(VehicleAlreadyParkedException.class, () -> {
            service.parkVehicle(new Car("MH01AB1234")); // Same license plate
        }, "Should throw when parking same vehicle twice");
    }

    private void testParkWhenFullThrows() {
        ParkingService service = setUp();
        // Lot has only 2 truck spots

        service.parkVehicle(new Truck("TRK001"));
        service.parkVehicle(new Truck("TRK002"));

        runner.assertThrows(ParkingFullException.class, () -> {
            service.parkVehicle(new Truck("TRK003"));
        }, "Should throw when parking lot is full");
    }

    // ===================================================================
    // EXIT PARKING TESTS
    // ===================================================================

    private void testExitParkingByTicketId() {
        ParkingService service = setUp();
        Car car = new Car("MH01AB1234");
        ParkingTicket ticket = service.parkVehicle(car);

        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());

        runner.assertNotNull(result, "Should return exit result");
        runner.assertNotNull(result.getTicket(), "Result should have ticket");
    }

    private void testExitParkingCalculatesFee() {
        ParkingService service = setUp();
        Car car = new Car("MH01AB1234");

        // Park with custom entry time (2 hours ago)
        LocalDateTime entryTime = LocalDateTime.now().minusHours(2);
        ParkingTicket ticket = service.parkVehicle(car, entryTime);

        ParkingService.ExitResult result = service.exitParking(ticket.getTicketId());

        // Car: 2 hours = 20 + 10 = 30
        runner.assertEquals(30.0, result.getFee(), 0.01, "Fee should be 30 for 2 hours");
    }

    private void testExitParkingFreesSpot() {
        ParkingService service = setUp();

        // Fill all car spots (3)
        service.parkVehicle(new Car("CAR001"));
        service.parkVehicle(new Car("CAR002"));
        ParkingTicket ticket = service.parkVehicle(new Car("CAR003"));

        // No more car spots available
        runner.assertEquals(0, service.getAvailableSpots(VehicleType.CAR), "Should have 0 spots before exit");

        // Exit one car
        service.exitParking(ticket.getTicketId());

        // Now one spot should be available
        runner.assertEquals(1, service.getAvailableSpots(VehicleType.CAR), "Should have 1 spot after exit");
    }

    private void testExitInvalidTicketThrows() {
        ParkingService service = setUp();

        runner.assertThrows(InvalidTicketException.class, () -> {
            service.exitParking("INVALID-TICKET-ID");
        }, "Should throw for invalid ticket");
    }

    // ===================================================================
    // AVAILABILITY TESTS
    // ===================================================================

    private void testIsParkingAvailable() {
        ParkingService service = setUp();

        runner.assertTrue(service.isParkingAvailable(VehicleType.CAR), "Should have car spots available");

        // Fill all car spots
        service.parkVehicle(new Car("CAR001"));
        service.parkVehicle(new Car("CAR002"));
        service.parkVehicle(new Car("CAR003"));

        runner.assertFalse(service.isParkingAvailable(VehicleType.CAR), "Should have no car spots available");
    }

    private void testGetAvailableSpots() {
        ParkingService service = setUp();

        runner.assertEquals(3, service.getAvailableSpots(VehicleType.MOTORBIKE), "Should have 3 motorbike spots");
        runner.assertEquals(3, service.getAvailableSpots(VehicleType.CAR), "Should have 3 car spots");
        runner.assertEquals(2, service.getAvailableSpots(VehicleType.TRUCK), "Should have 2 truck spots");

        service.parkVehicle(new Car("CAR001"));

        runner.assertEquals(2, service.getAvailableSpots(VehicleType.CAR), "Should have 2 car spots after parking 1");
    }
}

