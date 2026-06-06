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

### Starting the Application

```bash
./compile.sh   # Compile the application
./run.sh       # Run the application
```

### Initial Setup

When you start the application, you'll be asked to configure the parking lot:

```
═══ PARKING LOT SETUP ═══

Use default configuration? (y/n): y

✓ Parking lot initialized with default configuration:
  • 3 floors
  • 10 motorbike spots per floor
  • 10 car spots per floor
  • 5 truck spots per floor
```

Or customize:
```
Use default configuration? (y/n): n
Enter parking lot name: My Parking
Enter parking lot address: 456 Oak Street
Enter number of floors: 2
Enter motorbike spots per floor: 5
Enter car spots per floor: 8
Enter truck spots per floor: 3
```

### Main Menu Options

```
┌───────────────────────────────────────┐
│            MAIN MENU                  │
├───────────────────────────────────────┤
│  1. Park Vehicle                      │
│  2. Exit Parking (by Ticket ID)       │
│  3. Exit Parking (by License Plate)   │
│  4. View Parking Status               │
│  5. View Parked Vehicles              │
│  6. View Fee Structure                │
│  7. Search Ticket                     │
│  0. Exit Application                  │
└───────────────────────────────────────┘
```

### 1. Park Vehicle

Select vehicle type, enter license plate and owner name:

```
═══ PARK VEHICLE ═══

Vehicle Types:
  1. Motorbike (30 spots available)
  2. Car (30 spots available)
  3. Truck (15 spots available)

Select vehicle type (1-3): 2
Enter license plate: KA01AB1234
Enter owner name: John Doe

✓ Vehicle parked successfully!

╔════════════════════════════════════════╗
║         PARKING TICKET                 ║
╠════════════════════════════════════════╣
║ Ticket ID  : TKT-A1B2C3D4              ║
║ Vehicle    : Car                       ║
║ License    : KA01AB1234                ║
║ Spot       : F1-C01                    ║
║ Entry Time : 2024-01-15 10:30:00       ║
╚════════════════════════════════════════╝

⚠️  Please keep your ticket ID safe: TKT-A1B2C3D4
```

### 2. Exit Parking (by Ticket ID)

```
═══ EXIT PARKING ═══

Enter ticket ID: TKT-A1B2C3D4

╔═══════════════════════════════════════════╗
║           EXIT RECEIPT                    ║
╠═══════════════════════════════════════════╣
║ Ticket ID    : TKT-A1B2C3D4               ║
║ Vehicle      : Car                        ║
║ License Plate: KA01AB1234                 ║
║ Entry Time   : 2024-01-15 10:30:00        ║
║ Exit Time    : 2024-01-15 12:45:00        ║
║ Duration     : 3 hour(s)                  ║
╠═══════════════════════════════════════════╣
║ TOTAL AMOUNT : ₹40.00                     ║
╠═══════════════════════════════════════════╣
║           THANK YOU! VISIT AGAIN          ║
╚═══════════════════════════════════════════╝

┌─────────────────────────────────────┐
│         FEE BREAKDOWN               │
├─────────────────────────────────────┤
│ Vehicle Type : Car                  │
│ Duration     : 3 hour(s)            │
│ Base Rate    : ₹20.00               │
│ Additional   : 2 hrs × ₹10 = ₹20.00 │
├─────────────────────────────────────┤
│ TOTAL FEE    : ₹40.00               │
└─────────────────────────────────────┘

✓ Payment processed successfully!
```

### 3. Exit Parking (by License Plate)

Alternative to ticket ID - useful if ticket is lost:

```
═══ EXIT PARKING ═══

Enter license plate: KA01AB1234
```

### 4. View Parking Status

See overall availability:

```
═══ PARKING STATUS ═══

╔══════════════════════════════════════════════════════╗
║  City Center Parking                                 ║
║  123 Main Street                                     ║
╠══════════════════════════════════════════════════════╣
║  OVERALL AVAILABILITY:                               ║
║    Motorbike: 28/30 spots available                  ║
║    Car: 25/30 spots available                        ║
║    Truck: 15/15 spots available                      ║
╠══════════════════════════════════════════════════════╣
║  FLOOR-WISE STATUS:                                  ║
║  Floor-1                                             ║
║    Motorbike: 9/10                                   ║
║    Car: 8/10                                         ║
║    Truck: 5/5                                        ║
║  ...                                                 ║
╚══════════════════════════════════════════════════════╝

Currently parked vehicles: 7
```

### 5. View Parked Vehicles

List all currently parked vehicles:

```
═══ PARKED VEHICLES ═══

Total vehicles parked: 3

┌─────────────────┬──────────────┬────────────┬────────────────────────┐
│ Ticket ID       │ License Plate│ Type       │ Entry Time             │
├─────────────────┼──────────────┼────────────┼────────────────────────┤
│ TKT-A1B2C3D4    │ KA01AB1234   │ Car        │ 2024-01-15 10:30:00    │
│ TKT-E5F6G7H8    │ MH02XY5678   │ Motorbike  │ 2024-01-15 11:00:00    │
│ TKT-I9J0K1L2    │ TN03ZZ9999   │ Truck      │ 2024-01-15 09:15:00    │
└─────────────────┴──────────────┴────────────┴────────────────────────┘
```

### 6. View Fee Structure

Display current pricing:

```
═══ FEE STRUCTURE ═══

┌────────────┬─────────────────────┬────────────────────────┐
│ Vehicle    │ Base Rate (1st hr)  │ Additional (per hour)  │
├────────────┼─────────────────────┼────────────────────────┤
│ Motorbike  │ ₹10.00              │ ₹5.00                  │
│ Car        │ ₹20.00              │ ₹10.00                 │
│ Truck      │ ₹40.00              │ ₹20.00                 │
└────────────┴─────────────────────┴────────────────────────┘

Notes:
  • Minimum charge is the base rate (for duration ≤ 1 hour)
  • Partial hours are rounded up to the next full hour
  • Formula: Base Rate + (Additional Hours × Hourly Rate)
```

### 7. Search Ticket

Look up ticket by ID or license plate:

```
═══ SEARCH TICKET ═══

Search by:
  1. Ticket ID
  2. License Plate
Enter choice (1-2): 2
Enter license plate: KA01AB1234

✓ Ticket found:
╔════════════════════════════════════════╗
║         PARKING TICKET                 ║
...
```

---

## Testing

*Coming in Phase 5*

---

## Development Phases

- [x] **Phase 1**: Project Setup & Core Models
- [x] **Phase 2**: Design Pattern Implementations
- [x] **Phase 3**: Core Services
- [x] **Phase 4**: Console Application
- [ ] **Phase 5**: Testing Suite
- [ ] **Phase 6**: Documentation Completion
