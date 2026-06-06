package com.parking.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight test runner with assertion utilities.
 * No external dependencies required.
 */
public class TestRunner {

    private final List<TestResult> results = new ArrayList<>();
    private String currentTestClass = "";
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    // ===================================================================
    // ASSERTION METHODS
    // ===================================================================

    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    public void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message + " - Object is null");
        }
    }

    public void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError(message + " - Expected null but got: " + obj);
        }
    }

    public void assertThrows(Class<? extends Throwable> expectedType, Runnable runnable, String message) {
        try {
            runnable.run();
            throw new AssertionError(message + " - Expected exception: " + expectedType.getSimpleName() + ", but none was thrown");
        } catch (Throwable t) {
            if (!expectedType.isInstance(t)) {
                throw new AssertionError(message + " - Expected: " + expectedType.getSimpleName() + ", Actual: " + t.getClass().getSimpleName());
            }
        }
    }

    // ===================================================================
    // TEST EXECUTION METHODS
    // ===================================================================

    public void startTestClass(String className) {
        this.currentTestClass = className;
        System.out.println("\nRunning: " + className);
        System.out.println("-".repeat(50));
    }

    public void runTest(String testName, Runnable test) {
        totalTests++;
        long startTime = System.currentTimeMillis();

        try {
            test.run();
            long endTime = System.currentTimeMillis();
            TestResult result = new TestResult(testName, true, "", endTime - startTime);
            results.add(result);
            passedTests++;
            System.out.println(result);
        } catch (AssertionError | Exception e) {
            long endTime = System.currentTimeMillis();
            TestResult result = new TestResult(testName, false, e.getMessage(), endTime - startTime);
            results.add(result);
            failedTests++;
            System.out.println(result);
        }
    }

    // ===================================================================
    // SUMMARY METHODS
    // ===================================================================

    public void printSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST RESULTS SUMMARY");
        System.out.println("=".repeat(60));
        System.out.printf("Total:  %d tests%n", totalTests);
        System.out.printf("Passed: %d tests%n", passedTests);
        System.out.printf("Failed: %d tests%n", failedTests);
        System.out.println("=".repeat(60));

        if (failedTests > 0) {
            System.out.println("\nFailed Tests:");
            for (TestResult result : results) {
                if (!result.isPassed()) {
                    System.out.println("  - " + result.getTestName() + ": " + result.getMessage());
                }
            }
        }

        System.out.println("\nResult: " + (failedTests == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED"));
    }

    public int getFailedCount() {
        return failedTests;
    }

    public int getPassedCount() {
        return passedTests;
    }

    public int getTotalCount() {
        return totalTests;
    }

    public void reset() {
        results.clear();
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        currentTestClass = "";
    }
}

