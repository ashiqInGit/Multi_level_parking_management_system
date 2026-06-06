#!/bin/bash

# ═══════════════════════════════════════════════════════════════
# Compile Script for Multi-Level Parking Management System
# ═══════════════════════════════════════════════════════════════

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║     Compiling Multi-Level Parking Management System           ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define paths
SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"

# Clean previous compilation
echo "→ Cleaning previous build..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Find all Java files
echo "→ Finding source files..."
JAVA_FILES=$(find "$SRC_DIR" -name "*.java")

# Count files
FILE_COUNT=$(echo "$JAVA_FILES" | wc -l | tr -d ' ')
echo "→ Found $FILE_COUNT Java files"

# Compile
echo "→ Compiling..."
javac -d "$OUT_DIR" $JAVA_FILES 2>&1

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "[SUCCESS] Compilation successful!"
    echo ""
    echo "To run the application, execute:"
    echo "  ./run.sh"
    echo ""
    echo "Or manually:"
    echo "  java -cp $OUT_DIR com.parking.ParkingApp"
else
    echo ""
    echo "[FAILED] Compilation failed!"
    exit 1
fi

