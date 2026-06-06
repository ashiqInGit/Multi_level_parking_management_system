package com.parking.test.factory;

import com.parking.enums.VehicleType;
import com.parking.factory.VehicleFactory;
import com.parking.model.Car;
import com.parking.model.Motorbike;
import com.parking.model.Truck;
import com.parking.model.Vehicle;
import com.parking.test.TestRunner;

/**
 * Tests for VehicleFactory.
 */
public class VehicleFactoryTest {

    private final TestRunner runner;

    public VehicleFactoryTest(TestRunner runner) {
        this.runner = runner;
    }

    public void runAllTests() {
        runner.startTestClass("VehicleFactoryTest");

        // Creation by VehicleType enum
        runner.runTest("testCreateMotorbikeByEnum", this::testCreateMotorbikeByEnum);
        runner.runTest("testCreateCarByEnum", this::testCreateCarByEnum);
        runner.runTest("testCreateTruckByEnum", this::testCreateTruckByEnum);

        // Creation by string type
        runner.runTest("testCreateMotorbikeByString", this::testCreateMotorbikeByString);
        runner.runTest("testCreateCarByString", this::testCreateCarByString);
        runner.runTest("testCreateTruckByString", this::testCreateTruckByString);
        runner.runTest("testCreateByCaseInsensitiveString", this::testCreateByCaseInsensitiveString);

        // Vehicle properties
        runner.runTest("testVehicleLicensePlateUppercase", this::testVehicleLicensePlateUppercase);
        runner.runTest("testVehicleOwnerName", this::testVehicleOwnerName);
        runner.runTest("testVehicleDefaultOwner", this::testVehicleDefaultOwner);

        // Error cases
        runner.runTest("testNullTypeThrowsException", this::testNullTypeThrowsException);
        runner.runTest("testInvalidStringTypeThrowsException", this::testInvalidStringTypeThrowsException);
        runner.runTest("testEmptyLicensePlateThrowsException", this::testEmptyLicensePlateThrowsException);
        runner.runTest("testNullLicensePlateThrowsException", this::testNullLicensePlateThrowsException);
    }

    // ===================================================================
    // CREATION BY VEHICLETYPE ENUM TESTS
    // ===================================================================

    private void testCreateMotorbikeByEnum() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.MOTORBIKE, "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Motorbike, "Should create Motorbike instance");
        runner.assertEquals(VehicleType.MOTORBIKE, vehicle.getType(), "Vehicle type should be MOTORBIKE");
    }

    private void testCreateCarByEnum() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.CAR, "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Car, "Should create Car instance");
        runner.assertEquals(VehicleType.CAR, vehicle.getType(), "Vehicle type should be CAR");
    }

    private void testCreateTruckByEnum() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.TRUCK, "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Truck, "Should create Truck instance");
        runner.assertEquals(VehicleType.TRUCK, vehicle.getType(), "Vehicle type should be TRUCK");
    }

    // ===================================================================
    // CREATION BY STRING TYPE TESTS
    // ===================================================================

    private void testCreateMotorbikeByString() {
        Vehicle vehicle = VehicleFactory.createVehicle("MOTORBIKE", "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Motorbike, "Should create Motorbike from string");
    }

    private void testCreateCarByString() {
        Vehicle vehicle = VehicleFactory.createVehicle("CAR", "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Car, "Should create Car from string");
    }

    private void testCreateTruckByString() {
        Vehicle vehicle = VehicleFactory.createVehicle("TRUCK", "MH01AB1234", "John");
        runner.assertTrue(vehicle instanceof Truck, "Should create Truck from string");
    }

    private void testCreateByCaseInsensitiveString() {
        Vehicle v1 = VehicleFactory.createVehicle("car", "MH01AB1234", "John");
        Vehicle v2 = VehicleFactory.createVehicle("Car", "MH01AB5678", "Jane");
        Vehicle v3 = VehicleFactory.createVehicle("CAR", "MH01AB9999", "Bob");

        runner.assertTrue(v1 instanceof Car, "Should handle lowercase");
        runner.assertTrue(v2 instanceof Car, "Should handle mixed case");
        runner.assertTrue(v3 instanceof Car, "Should handle uppercase");
    }

    // ===================================================================
    // VEHICLE PROPERTIES TESTS
    // ===================================================================

    private void testVehicleLicensePlateUppercase() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.CAR, "mh01ab1234", "John");
        runner.assertEquals("MH01AB1234", vehicle.getLicensePlate(), "License plate should be uppercase");
    }

    private void testVehicleOwnerName() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.CAR, "MH01AB1234", "John Doe");
        runner.assertEquals("John Doe", vehicle.getOwnerName(), "Owner name should match");
    }

    private void testVehicleDefaultOwner() {
        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.CAR, "MH01AB1234");
        runner.assertEquals("Unknown", vehicle.getOwnerName(), "Default owner should be Unknown");
    }

    // ===================================================================
    // ERROR CASE TESTS
    // ===================================================================

    private void testNullTypeThrowsException() {
        runner.assertThrows(IllegalArgumentException.class, () -> {
            VehicleFactory.createVehicle((VehicleType) null, "MH01AB1234", "John");
        }, "Null type should throw exception");
    }

    private void testInvalidStringTypeThrowsException() {
        runner.assertThrows(IllegalArgumentException.class, () -> {
            VehicleFactory.createVehicle("BICYCLE", "MH01AB1234", "John");
        }, "Invalid string type should throw exception");
    }

    private void testEmptyLicensePlateThrowsException() {
        runner.assertThrows(IllegalArgumentException.class, () -> {
            VehicleFactory.createVehicle(VehicleType.CAR, "", "John");
        }, "Empty license plate should throw exception");
    }

    private void testNullLicensePlateThrowsException() {
        runner.assertThrows(IllegalArgumentException.class, () -> {
            VehicleFactory.createVehicle(VehicleType.CAR, null, "John");
        }, "Null license plate should throw exception");
    }
}

