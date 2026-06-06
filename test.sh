#!/bin/bash

# ===================================================================
# Test Script for Multi-Level Parking Management System
# ===================================================================

echo ""
echo "============================================================"
echo "   Compiling and Running Test Suite"
echo "============================================================"
echo ""

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define paths
SRC_DIR="$SCRIPT_DIR/src"
TEST_DIR="$SCRIPT_DIR/test"
OUT_DIR="$SCRIPT_DIR/out"

# Clean previous compilation
echo "-> Cleaning previous build..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Find all Java files (both src and test)
echo "-> Finding source files..."
SRC_FILES=$(find "$SRC_DIR" -name "*.java")
TEST_FILES=$(find "$TEST_DIR" -name "*.java")

# Count files
SRC_COUNT=$(echo "$SRC_FILES" | wc -l | tr -d ' ')
TEST_COUNT=$(echo "$TEST_FILES" | wc -l | tr -d ' ')
echo "-> Found $SRC_COUNT source files and $TEST_COUNT test files"

# Compile
echo "-> Compiling..."
javac -d "$OUT_DIR" $SRC_FILES $TEST_FILES 2>&1

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo ""
    echo "[FAILED] Compilation failed!"
    exit 1
fi

echo "-> Compilation successful"
echo ""
echo "============================================================"
echo "   Running Tests"
echo "============================================================"

# Run tests
java -cp "$OUT_DIR" com.parking.test.TestMain

# Capture exit code
EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo "============================================================"
    echo "   All tests passed!"
    echo "============================================================"
else
    echo "============================================================"
    echo "   Some tests failed!"
    echo "============================================================"
fi

exit $EXIT_CODE

