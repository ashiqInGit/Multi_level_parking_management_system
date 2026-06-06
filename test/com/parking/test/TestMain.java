package com.parking.test;

import com.parking.test.factory.VehicleFactoryTest;
import com.parking.test.integration.ParkingSystemIntegrationTest;
import com.parking.test.model.ParkingTicketTest;
import com.parking.test.service.ParkingLotTest;
import com.parking.test.service.ParkingServiceTest;
import com.parking.test.strategy.FeeCalculationTest;

/**
 * Main entry point for running all tests.
 * Run this class to execute the complete test suite.
 */
public class TestMain {

    public static void main(String[] args) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("   MULTI-LEVEL PARKING SYSTEM - TEST SUITE");
        System.out.println("=".repeat(60));

        TestRunner runner = new TestRunner();

        // Run all test classes
        new FeeCalculationTest(runner).runAllTests();
        new ParkingTicketTest(runner).runAllTests();
        new VehicleFactoryTest(runner).runAllTests();
        new ParkingLotTest(runner).runAllTests();
        new ParkingServiceTest(runner).runAllTests();
        new ParkingSystemIntegrationTest(runner).runAllTests();

        // Print summary
        runner.printSummary();

        // Exit with appropriate code
        System.exit(runner.getFailedCount() > 0 ? 1 : 0);
    }
}

