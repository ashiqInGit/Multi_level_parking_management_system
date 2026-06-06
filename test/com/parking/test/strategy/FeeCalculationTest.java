package com.parking.test.strategy;

import com.parking.enums.VehicleType;
import com.parking.model.Car;
import com.parking.model.Motorbike;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.model.Truck;
import com.parking.strategy.CarFeeStrategy;
import com.parking.strategy.FeeCalculationStrategy;
import com.parking.strategy.FeeStrategyFactory;
import com.parking.strategy.MotorbikeFeeStrategy;
import com.parking.strategy.TruckFeeStrategy;
import com.parking.test.TestRunner;

import java.time.LocalDateTime;

/**
 * Tests for fee calculation strategies.
 * Verifies correct fee calculation for all vehicle types and durations.
 */
public class FeeCalculationTest {

    private final TestRunner runner;

    public FeeCalculationTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("FeeCalculationTest");

        // Motorbike fee tests
        runner.runTest("testMotorbikeBaseRate", this::testMotorbikeBaseRate);
        runner.runTest("testMotorbikeOneHour", this::testMotorbikeOneHour);
        runner.runTest("testMotorbikeTwoHours", this::testMotorbikeTwoHours);
        runner.runTest("testMotorbikeThreeHours", this::testMotorbikeThreeHours);

        // Car fee tests
        runner.runTest("testCarBaseRate", this::testCarBaseRate);
        runner.runTest("testCarOneHour", this::testCarOneHour);
        runner.runTest("testCarTwoHours", this::testCarTwoHours);
        runner.runTest("testCarThreeHours", this::testCarThreeHours);
        runner.runTest("testCarFiveHours", this::testCarFiveHours);

        // Truck fee tests
        runner.runTest("testTruckBaseRate", this::testTruckBaseRate);
        runner.runTest("testTruckTwoHours", this::testTruckTwoHours);
        runner.runTest("testTruckFourHours", this::testTruckFourHours);

        // Strategy factory tests
        runner.runTest("testStrategyFactoryReturnsCorrectStrategy", this::testStrategyFactoryReturnsCorrectStrategy);

        // Edge cases
        runner.runTest("testZeroDurationChargesBaseRate", this::testZeroDurationChargesBaseRate);
        runner.runTest("testPartialHourRoundsUp", this::testPartialHourRoundsUp);
    }

    // ===================================================================
    // MOTORBIKE FEE TESTS (Base: 10, Hourly: 5)
    // ===================================================================

    private void testMotorbikeBaseRate() {
        MotorbikeFeeStrategy strategy = new MotorbikeFeeStrategy();
        runner.assertEquals(10.0, strategy.getBaseRate(), 0.01, "Motorbike base rate should be 10");
        runner.assertEquals(5.0, strategy.getHourlyRate(), 0.01, "Motorbike hourly rate should be 5");
    }

    private void testMotorbikeOneHour() {
        // 1 hour = base rate only = 10
        ParkingTicket ticket = createTicketWithDuration(VehicleType.MOTORBIKE, 60);
        FeeCalculationStrategy strategy = new MotorbikeFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(10.0, fee, 0.01, "Motorbike 1 hour fee should be 10");
    }

    private void testMotorbikeTwoHours() {
        // 2 hours = 10 + (1 * 5) = 15
        ParkingTicket ticket = createTicketWithDuration(VehicleType.MOTORBIKE, 120);
        FeeCalculationStrategy strategy = new MotorbikeFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(15.0, fee, 0.01, "Motorbike 2 hours fee should be 15");
    }

    private void testMotorbikeThreeHours() {
        // 3 hours = 10 + (2 * 5) = 20
        ParkingTicket ticket = createTicketWithDuration(VehicleType.MOTORBIKE, 180);
        FeeCalculationStrategy strategy = new MotorbikeFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(20.0, fee, 0.01, "Motorbike 3 hours fee should be 20");
    }

    // ===================================================================
    // CAR FEE TESTS (Base: 20, Hourly: 10)
    // ===================================================================

    private void testCarBaseRate() {
        CarFeeStrategy strategy = new CarFeeStrategy();
        runner.assertEquals(20.0, strategy.getBaseRate(), 0.01, "Car base rate should be 20");
        runner.assertEquals(10.0, strategy.getHourlyRate(), 0.01, "Car hourly rate should be 10");
    }

    private void testCarOneHour() {
        // 1 hour = base rate only = 20
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 60);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(20.0, fee, 0.01, "Car 1 hour fee should be 20");
    }

    private void testCarTwoHours() {
        // 2 hours = 20 + (1 * 10) = 30
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 120);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(30.0, fee, 0.01, "Car 2 hours fee should be 30");
    }

    private void testCarThreeHours() {
        // 3 hours = 20 + (2 * 10) = 40
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 180);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(40.0, fee, 0.01, "Car 3 hours fee should be 40");
    }

    private void testCarFiveHours() {
        // 5 hours = 20 + (4 * 10) = 60
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 300);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(60.0, fee, 0.01, "Car 5 hours fee should be 60");
    }

    // ===================================================================
    // TRUCK FEE TESTS (Base: 40, Hourly: 20)
    // ===================================================================

    private void testTruckBaseRate() {
        TruckFeeStrategy strategy = new TruckFeeStrategy();
        runner.assertEquals(40.0, strategy.getBaseRate(), 0.01, "Truck base rate should be 40");
        runner.assertEquals(20.0, strategy.getHourlyRate(), 0.01, "Truck hourly rate should be 20");
    }

    private void testTruckTwoHours() {
        // 2 hours = 40 + (1 * 20) = 60
        ParkingTicket ticket = createTicketWithDuration(VehicleType.TRUCK, 120);
        FeeCalculationStrategy strategy = new TruckFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(60.0, fee, 0.01, "Truck 2 hours fee should be 60");
    }

    private void testTruckFourHours() {
        // 4 hours = 40 + (3 * 20) = 100
        ParkingTicket ticket = createTicketWithDuration(VehicleType.TRUCK, 240);
        FeeCalculationStrategy strategy = new TruckFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(100.0, fee, 0.01, "Truck 4 hours fee should be 100");
    }

    // ===================================================================
    // STRATEGY FACTORY TESTS
    // ===================================================================

    private void testStrategyFactoryReturnsCorrectStrategy() {
        FeeCalculationStrategy motorbikeStrategy = FeeStrategyFactory.getStrategy(VehicleType.MOTORBIKE);
        FeeCalculationStrategy carStrategy = FeeStrategyFactory.getStrategy(VehicleType.CAR);
        FeeCalculationStrategy truckStrategy = FeeStrategyFactory.getStrategy(VehicleType.TRUCK);

        runner.assertTrue(motorbikeStrategy instanceof MotorbikeFeeStrategy, "Factory should return MotorbikeFeeStrategy");
        runner.assertTrue(carStrategy instanceof CarFeeStrategy, "Factory should return CarFeeStrategy");
        runner.assertTrue(truckStrategy instanceof TruckFeeStrategy, "Factory should return TruckFeeStrategy");
    }

    // ===================================================================
    // EDGE CASE TESTS
    // ===================================================================

    private void testZeroDurationChargesBaseRate() {
        // 0 minutes should still charge base rate (minimum 1 hour)
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 0);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(20.0, fee, 0.01, "Zero duration should charge base rate");
    }

    private void testPartialHourRoundsUp() {
        // 61 minutes = 2 hours (rounded up)
        // Car: 20 + (1 * 10) = 30
        ParkingTicket ticket = createTicketWithDuration(VehicleType.CAR, 61);
        FeeCalculationStrategy strategy = new CarFeeStrategy();
        double fee = strategy.calculateFee(ticket);
        runner.assertEquals(30.0, fee, 0.01, "61 minutes should round up to 2 hours");
    }

    // ===================================================================
    // HELPER METHODS
    // ===================================================================

    private ParkingTicket createTicketWithDuration(VehicleType type, int durationMinutes) {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(durationMinutes);
        LocalDateTime exitTime = LocalDateTime.now();

        ParkingSpot spot = new ParkingSpot("TEST-01", 1, type);
        ParkingTicket ticket;

        switch (type) {
            case MOTORBIKE:
                ticket = new ParkingTicket(new Motorbike("TEST-MB-001"), spot, entryTime);
                break;
            case CAR:
                ticket = new ParkingTicket(new Car("TEST-CAR-001"), spot, entryTime);
                break;
            case TRUCK:
                ticket = new ParkingTicket(new Truck("TEST-TRK-001"), spot, entryTime);
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type");
        }

        ticket.markExit(exitTime);
        return ticket;
    }
}

