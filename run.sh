#!/bin/bash

# ═══════════════════════════════════════════════════════════════
# Run Script for Multi-Level Parking Management System
# ═══════════════════════════════════════════════════════════════

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define paths
OUT_DIR="$SCRIPT_DIR/out"

# Check if compiled classes exist
if [ ! -d "$OUT_DIR" ] || [ -z "$(ls -A $OUT_DIR 2>/dev/null)" ]; then
    echo "╔═══════════════════════════════════════════════════════════════╗"
    echo "║     Application not compiled!                                 ║"
    echo "╚═══════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Please compile first using:"
    echo "  ./compile.sh"
    echo ""
    exit 1
fi

# Run the application
java -cp "$OUT_DIR" com.parking.ParkingApp

