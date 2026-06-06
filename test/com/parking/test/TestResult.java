package com.parking.test;

/**
 * Represents the result of a single test case.
 */
public class TestResult {
    private final String testName;
    private final boolean passed;
    private final String message;
    private final long executionTimeMs;

    public TestResult(String testName, boolean passed, String message, long executionTimeMs) {
        this.testName = testName;
        this.passed = passed;
        this.message = message;
        this.executionTimeMs = executionTimeMs;
    }

    public String getTestName() {
        return testName;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String toString() {
        String status = passed ? "[PASS]" : "[FAIL]";
        String result = String.format("  %s %s", status, testName);
        if (!passed && message != null && !message.isEmpty()) {
            result += " - " + message;
        }
        return result;
    }
}

