package com.parking.test.model;

import com.parking.enums.VehicleType;
import com.parking.model.Car;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.test.TestRunner;

import java.time.LocalDateTime;

/**
 * Tests for ParkingTicket duration calculation.
 */
public class ParkingTicketTest {

    private final TestRunner runner;

    public ParkingTicketTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("ParkingTicketTest");

        // Duration calculation tests
        runner.runTest("testDurationInMinutes30Min", this::testDurationInMinutes30Min);
        runner.runTest("testDurationInMinutes60Min", this::testDurationInMinutes60Min);
        runner.runTest("testDurationInMinutes90Min", this::testDurationInMinutes90Min);
        runner.runTest("testDurationInMinutes150Min", this::testDurationInMinutes150Min);

        // Duration in hours (rounded up) tests
        runner.runTest("testDurationInHours30MinRoundsTo1", this::testDurationInHours30MinRoundsTo1);
        runner.runTest("testDurationInHours60MinEquals1", this::testDurationInHours60MinEquals1);
        runner.runTest("testDurationInHours61MinRoundsTo2", this::testDurationInHours61MinRoundsTo2);
        runner.runTest("testDurationInHours120MinEquals2", this::testDurationInHours120MinEquals2);
        runner.runTest("testDurationInHours121MinRoundsTo3", this::testDurationInHours121MinRoundsTo3);

        // Ticket properties tests
        runner.runTest("testTicketIdGenerated", this::testTicketIdGenerated);
        runner.runTest("testTicketStoresVehicle", this::testTicketStoresVehicle);
        runner.runTest("testTicketStoresSpot", this::testTicketStoresSpot);
        runner.runTest("testTicketFeeInitiallyZero", this::testTicketFeeInitiallyZero);
        runner.runTest("testTicketNotPaidInitially", this::testTicketNotPaidInitially);
        runner.runTest("testMarkPaid", this::testMarkPaid);
    }

    // ===================================================================
    // DURATION IN MINUTES TESTS
    // ===================================================================

    private void testDurationInMinutes30Min() {
        ParkingTicket ticket = createTicketWithDuration(30);
        long duration = ticket.getDurationInMinutes();
        runner.assertEquals(30L, duration, "Duration should be 30 minutes");
    }

    private void testDurationInMinutes60Min() {
        ParkingTicket ticket = createTicketWithDuration(60);
        long duration = ticket.getDurationInMinutes();
        runner.assertEquals(60L, duration, "Duration should be 60 minutes");
    }

    private void testDurationInMinutes90Min() {
        ParkingTicket ticket = createTicketWithDuration(90);
        long duration = ticket.getDurationInMinutes();
        runner.assertEquals(90L, duration, "Duration should be 90 minutes");
    }

    private void testDurationInMinutes150Min() {
        ParkingTicket ticket = createTicketWithDuration(150);
        long duration = ticket.getDurationInMinutes();
        runner.assertEquals(150L, duration, "Duration should be 150 minutes");
    }

    // ===================================================================
    // DURATION IN HOURS (ROUNDED UP) TESTS
    // ===================================================================

    private void testDurationInHours30MinRoundsTo1() {
        ParkingTicket ticket = createTicketWithDuration(30);
        int hours = ticket.getDurationInHours();
        runner.assertEquals(1, hours, "30 minutes should round up to 1 hour");
    }

    private void testDurationInHours60MinEquals1() {
        ParkingTicket ticket = createTicketWithDuration(60);
        int hours = ticket.getDurationInHours();
        runner.assertEquals(1, hours, "60 minutes should equal 1 hour");
    }

    private void testDurationInHours61MinRoundsTo2() {
        ParkingTicket ticket = createTicketWithDuration(61);
        int hours = ticket.getDurationInHours();
        runner.assertEquals(2, hours, "61 minutes should round up to 2 hours");
    }

    private void testDurationInHours120MinEquals2() {
        ParkingTicket ticket = createTicketWithDuration(120);
        int hours = ticket.getDurationInHours();
        runner.assertEquals(2, hours, "120 minutes should equal 2 hours");
    }

    private void testDurationInHours121MinRoundsTo3() {
        ParkingTicket ticket = createTicketWithDuration(121);
        int hours = ticket.getDurationInHours();
        runner.assertEquals(3, hours, "121 minutes should round up to 3 hours");
    }

    // ===================================================================
    // TICKET PROPERTIES TESTS
    // ===================================================================

    private void testTicketIdGenerated() {
        ParkingTicket ticket = createTicket();
        runner.assertNotNull(ticket.getTicketId(), "Ticket ID should be generated");
        runner.assertTrue(ticket.getTicketId().startsWith("TKT-"), "Ticket ID should start with TKT-");
    }

    private void testTicketStoresVehicle() {
        Car car = new Car("TEST-001", "John");
        ParkingSpot spot = new ParkingSpot("F1-C01", 1, VehicleType.CAR);
        ParkingTicket ticket = new ParkingTicket(car, spot);

        runner.assertNotNull(ticket.getVehicle(), "Ticket should store vehicle");
        runner.assertEquals("TEST-001", ticket.getVehicle().getLicensePlate(), "License plate should match");
    }

    private void testTicketStoresSpot() {
        Car car = new Car("TEST-001", "John");
        ParkingSpot spot = new ParkingSpot("F1-C01", 1, VehicleType.CAR);
        ParkingTicket ticket = new ParkingTicket(car, spot);

        runner.assertNotNull(ticket.getSpot(), "Ticket should store spot");
        runner.assertEquals("F1-C01", ticket.getSpot().getSpotId(), "Spot ID should match");
    }

    private void testTicketFeeInitiallyZero() {
        ParkingTicket ticket = createTicket();
        runner.assertEquals(0.0, ticket.getFee(), 0.01, "Initial fee should be 0");
    }

    private void testTicketNotPaidInitially() {
        ParkingTicket ticket = createTicket();
        runner.assertFalse(ticket.isPaid(), "Ticket should not be paid initially");
    }

    private void testMarkPaid() {
        ParkingTicket ticket = createTicket();
        ticket.markPaid();
        runner.assertTrue(ticket.isPaid(), "Ticket should be marked as paid");
    }

    // ===================================================================
    // HELPER METHODS
    // ===================================================================

    private ParkingTicket createTicketWithDuration(int durationMinutes) {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(durationMinutes);
        LocalDateTime exitTime = LocalDateTime.now();

        Car car = new Car("TEST-001");
        ParkingSpot spot = new ParkingSpot("TEST-01", 1, VehicleType.CAR);
        ParkingTicket ticket = new ParkingTicket(car, spot, entryTime);
        ticket.markExit(exitTime);
        return ticket;
    }

    private ParkingTicket createTicket() {
        Car car = new Car("TEST-001");
        ParkingSpot spot = new ParkingSpot("TEST-01", 1, VehicleType.CAR);
        return new ParkingTicket(car, spot);
    }
}

