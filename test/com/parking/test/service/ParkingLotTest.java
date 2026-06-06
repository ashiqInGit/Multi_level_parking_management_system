package com.parking.test.service;

import com.parking.enums.VehicleType;
import com.parking.model.ParkingFloor;
import com.parking.model.ParkingSpot;
import com.parking.service.ParkingLot;
import com.parking.test.TestRunner;

/**
 * Tests for ParkingLot singleton.
 */
public class ParkingLotTest {

    private final TestRunner runner;

    public ParkingLotTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("ParkingLotTest");

        // Singleton tests
        runner.runTest("testSingletonInstance", this::testSingletonInstance);
        runner.runTest("testResetInstance", this::testResetInstance);

        // Initialization tests
        runner.runTest("testInitializeWithDefaults", this::testInitializeWithDefaults);
        runner.runTest("testInitializeWithCustomConfig", this::testInitializeWithCustomConfig);
        runner.runTest("testInitializeTwiceThrowsException", this::testInitializeTwiceThrowsException);

        // Spot allocation tests
        runner.runTest("testFindAvailableSpotMotorbike", this::testFindAvailableSpotMotorbike);
        runner.runTest("testFindAvailableSpotCar", this::testFindAvailableSpotCar);
        runner.runTest("testFindAvailableSpotTruck", this::testFindAvailableSpotTruck);

        // Spot count tests
        runner.runTest("testAvailableSpotCount", this::testAvailableSpotCount);
        runner.runTest("testTotalSpotCount", this::testTotalSpotCount);

        // Floor tests
        runner.runTest("testGetFloor", this::testGetFloor);
        runner.runTest("testGetFloorInvalidNumber", this::testGetFloorInvalidNumber);
    }

    private void setUp() {
        ParkingLot.resetInstance();
    }

    // ===================================================================
    // SINGLETON TESTS
    // ===================================================================

    private void testSingletonInstance() {
        setUp();
        ParkingLot lot1 = ParkingLot.getInstance();
        ParkingLot lot2 = ParkingLot.getInstance();
        runner.assertTrue(lot1 == lot2, "Should return same instance");
    }

    private void testResetInstance() {
        setUp();
        ParkingLot lot1 = ParkingLot.getInstance();
        lot1.initialize("Test Lot 1");

        ParkingLot.resetInstance();

        ParkingLot lot2 = ParkingLot.getInstance();
        runner.assertFalse(lot2.isInitialized(), "Reset should create uninitialized instance");
    }

    // ===================================================================
    // INITIALIZATION TESTS
    // ===================================================================

    private void testInitializeWithDefaults() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test Lot");

        runner.assertTrue(lot.isInitialized(), "Should be initialized");
        runner.assertEquals("Test Lot", lot.getName(), "Name should match");
        runner.assertEquals(3, lot.getNumberOfFloors(), "Should have 3 floors by default");
    }

    private void testInitializeWithCustomConfig() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Custom Lot", 2, 5, 8, 3);

        runner.assertEquals(2, lot.getNumberOfFloors(), "Should have 2 floors");
        runner.assertEquals(10, lot.getTotalSpotCount(VehicleType.MOTORBIKE), "Should have 10 motorbike spots (5*2)");
        runner.assertEquals(16, lot.getTotalSpotCount(VehicleType.CAR), "Should have 16 car spots (8*2)");
        runner.assertEquals(6, lot.getTotalSpotCount(VehicleType.TRUCK), "Should have 6 truck spots (3*2)");
    }

    private void testInitializeTwiceThrowsException() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test Lot");

        runner.assertThrows(IllegalStateException.class, () -> {
            lot.initialize("Another Lot");
        }, "Should throw exception when initializing twice");
    }

    // ===================================================================
    // SPOT ALLOCATION TESTS
    // ===================================================================

    private void testFindAvailableSpotMotorbike() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 1, 5, 5, 2);

        ParkingSpot spot = lot.findAvailableSpot(VehicleType.MOTORBIKE);
        runner.assertNotNull(spot, "Should find available motorbike spot");
        runner.assertEquals(VehicleType.MOTORBIKE, spot.getVehicleType(), "Spot type should be MOTORBIKE");
    }

    private void testFindAvailableSpotCar() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 1, 5, 5, 2);

        ParkingSpot spot = lot.findAvailableSpot(VehicleType.CAR);
        runner.assertNotNull(spot, "Should find available car spot");
        runner.assertEquals(VehicleType.CAR, spot.getVehicleType(), "Spot type should be CAR");
    }

    private void testFindAvailableSpotTruck() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 1, 5, 5, 2);

        ParkingSpot spot = lot.findAvailableSpot(VehicleType.TRUCK);
        runner.assertNotNull(spot, "Should find available truck spot");
        runner.assertEquals(VehicleType.TRUCK, spot.getVehicleType(), "Spot type should be TRUCK");
    }

    // ===================================================================
    // SPOT COUNT TESTS
    // ===================================================================

    private void testAvailableSpotCount() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 2, 5, 10, 3);

        runner.assertEquals(10, lot.getAvailableSpotCount(VehicleType.MOTORBIKE), "Should have 10 available motorbike spots");
        runner.assertEquals(20, lot.getAvailableSpotCount(VehicleType.CAR), "Should have 20 available car spots");
        runner.assertEquals(6, lot.getAvailableSpotCount(VehicleType.TRUCK), "Should have 6 available truck spots");
    }

    private void testTotalSpotCount() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 3, 4, 6, 2);

        runner.assertEquals(12, lot.getTotalSpotCount(VehicleType.MOTORBIKE), "Should have 12 total motorbike spots");
        runner.assertEquals(18, lot.getTotalSpotCount(VehicleType.CAR), "Should have 18 total car spots");
        runner.assertEquals(6, lot.getTotalSpotCount(VehicleType.TRUCK), "Should have 6 total truck spots");
    }

    // ===================================================================
    // FLOOR TESTS
    // ===================================================================

    private void testGetFloor() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 3, 5, 5, 2);

        ParkingFloor floor1 = lot.getFloor(1);
        ParkingFloor floor2 = lot.getFloor(2);
        ParkingFloor floor3 = lot.getFloor(3);

        runner.assertNotNull(floor1, "Floor 1 should exist");
        runner.assertNotNull(floor2, "Floor 2 should exist");
        runner.assertNotNull(floor3, "Floor 3 should exist");
        runner.assertEquals(1, floor1.getFloorNumber(), "Floor number should be 1");
    }

    private void testGetFloorInvalidNumber() {
        setUp();
        ParkingLot lot = ParkingLot.getInstance();
        lot.initialize("Test", 2, 5, 5, 2);

        ParkingFloor floor0 = lot.getFloor(0);
        ParkingFloor floor5 = lot.getFloor(5);

        runner.assertNull(floor0, "Floor 0 should not exist");
        runner.assertNull(floor5, "Floor 5 should not exist");
    }
}

