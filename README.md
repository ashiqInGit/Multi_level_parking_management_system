# Multi-Level Parking Management System

A console-based parking management system built in Java that manages parking for multiple vehicle types across multiple floors. The system calculates fees based on vehicle type and parking duration.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Fee Calculation](#fee-calculation)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [Usage](#usage)
- [Testing](#testing)

---

## Features
- Multi-floor parking lot support
- Vehicle types: **Motorbike**, **Car**, **Truck**
- Automatic spot allocation based on vehicle type
- Entry/Exit ticket management
- Duration-based fee calculation
- Real-time parking status display

---

## Architecture

### Design Patterns

| Pattern | Class | Purpose |
|---------|-------|---------|
| **Factory** | `VehicleFactory` | Creates vehicle instances based on type |
| **Strategy** | `FeeCalculationStrategy` | Pluggable fee calculation per vehicle type |
| **Singleton** | `ParkingLot` | Single parking lot instance management |

### Class Diagram (Core Models)

```
                    ┌──────────────────┐
                    │    <<abstract>>  │
                    │     Vehicle      │
                    ├──────────────────┤
                    │ - licensePlate   │
                    │ - ownerName      │
                    ├──────────────────┤
                    │ + getType()      │
                    └────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
   │  Motorbike  │   │     Car     │   │    Truck    │
   └─────────────┘   └─────────────┘   └─────────────┘

   ┌─────────────────┐      ┌─────────────────┐
   │  ParkingFloor   │──────│   ParkingSpot   │
   ├─────────────────┤      ├─────────────────┤
   │ - floorNumber   │      │ - spotId        │
   │ - spotsByType   │      │ - vehicleType   │
   └─────────────────┘      │ - status        │
                            │ - parkedVehicle │
                            └─────────────────┘

   ┌─────────────────┐
   │  ParkingTicket  │
   ├─────────────────┤
   │ - ticketId      │
   │ - vehicle       │
   │ - spot          │
   │ - entryTime     │
   │ - exitTime      │
   │ - fee           │
   └─────────────────┘
```

---

## Fee Calculation

### Pricing Structure

| Vehicle Type | Base Rate (1st hour) | Additional Rate (per hour) |
|--------------|---------------------|---------------------------|
| Motorbike    | ₹10                 | ₹5/hour                   |
| Car          | ₹20                 | ₹10/hour                  |
| Truck        | ₹40                 | ₹20/hour                  |

### Calculation Formula

```
Total Fee = Base Rate + (Additional Hours × Additional Rate)

Where:
- Additional Hours = ceil(Total Duration in Minutes / 60) - 1
- Minimum charge = Base Rate (for duration ≤ 1 hour)
```

### Examples

| Vehicle | Duration | Calculation | Total Fee |
|---------|----------|-------------|-----------|
| Motorbike | 30 mins | ₹10 (base) | ₹10 |
| Motorbike | 2 hours | ₹10 + (1 × ₹5) | ₹15 |
| Car | 1 hour 15 mins | ₹20 + (1 × ₹10) | ₹30 |
| Car | 3 hours | ₹20 + (2 × ₹10) | ₹40 |
| Truck | 4 hours | ₹40 + (3 × ₹20) | ₹100 |

---

## Project Structure

```
Multi_level_parking_management_system/
├── README.md
├── src/
│   └── com/parking/
│       ├── ParkingApp.java              # Main entry point
│       ├── enums/
│       │   ├── VehicleType.java         # Vehicle types enum
│       │   └── SpotStatus.java          # Spot status enum
│       ├── model/
│       │   ├── Vehicle.java             # Abstract vehicle class
│       │   ├── Motorbike.java           # Motorbike implementation
│       │   ├── Car.java                 # Car implementation
│       │   ├── Truck.java               # Truck implementation
│       │   ├── ParkingSpot.java         # Parking spot model
│       │   ├── ParkingFloor.java        # Parking floor model
│       │   └── ParkingTicket.java       # Parking ticket model
│       ├── factory/
│       │   └── VehicleFactory.java      # Vehicle factory
│       ├── strategy/
│       │   ├── FeeCalculationStrategy.java
│       │   ├── MotorbikeFeeStrategy.java
│       │   ├── CarFeeStrategy.java
│       │   └── TruckFeeStrategy.java
│       ├── service/
│       │   ├── ParkingLot.java          # Singleton parking lot
│       │   ├── ParkingService.java      # Parking operations
│       │   └── TicketService.java       # Ticket management
│       └── exception/
│           ├── ParkingFullException.java
│           ├── InvalidTicketException.java
│           └── VehicleAlreadyParkedException.java
├── test/
│   └── com/parking/
│       ├── TestRunner.java              # Test runner
│       └── ...                          # Test classes
├── compile.sh                           # Compile script
├── run.sh                               # Run script
└── test.sh                              # Test script
```

---

## Build & Run

### Prerequisites
- Java JDK 8 or higher
- Terminal/Command Prompt

### Compile
```bash
# Using compile script
./compile.sh

# Or manually
mkdir -p out
javac -d out $(find src -name "*.java")
```

### Run
```bash
# Using run script
./run.sh

# Or manually
java -cp out com.parking.ParkingApp
```

### Run Tests
```bash
# Using test script
./test.sh
```

---

## Usage

*Coming in Phase 4*

---

## Testing

*Coming in Phase 5*

---

## Development Phases

- [x] **Phase 1**: Project Setup & Core Models
- [x] **Phase 2**: Core Services
- [ ] **Phase 3**: Console Application
- [ ] **Phase 4**: Testing Suite
- [ ] **Phase 5**: Documentation Completion