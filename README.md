## Fraud Detection API Documentation

# PROJECT OVERVIEW
* Created an API which detects fraud through a JSON transaction
* Technologies Used
    - Java 23
    - Gradle
    - Spring Boot 3.5.7
    - Junit 5
    - SLF4J with Logback
* Key Features Implemented 
    - REST API endpoint for real-time fraud detection
    - 4 independent fraud signal evaluators
    - Transaction history tracking (in-memory)
    - Location history and velocity checks
    - Comprehensive test suite (33 tests)
    - Global error handling with validation


## ARCHITECTURE
Layered Architecture
- Controller - REST Endpoints
- Service - Business logic orchestration
- DTO's(Data Transfer Object) - Request/Response models
- Domain - 4 fraud signal evaluators
- Entity - Transaction history storage

# DEPENDENCY FLOW
```
Controller → Service → Evaluators
                ↓
          History Service
```
Dependencies flow inward (Controller depends on Service, Service depends on Evaluators)
No circular dependencies - clean separation of concerns

4 Fraud Signal Evaluators
- Location (mismatch detection + new location tracking)
- IP Address (blacklist + private IP detection)
- Transaction (amount/item thresholds + velocity check)
- Card Details (name mismatch + suspicious patterns)

# GETTING STARTED 

# Prerequisites
- Java 23 (or Java 17+)
- Gradle 8.14.3 (included via wrapper)

# Build the project
./gradlew build

# Run tests
./gradlew test

# Start the server
./gradlew bootRun

# Server runs on http://localhost:8080


## RUNNING ON ANOTHER COMPUTER - HOW DO I RUN THIS?

Prerequisites
Ensure you have the following installed:
1. Java 23 (or Java 17+)
   - Check: java -version
   - Download: https://www.oracle.com/java/technologies/downloads/

2. Git (to clone the repository)
   - Check: git --version
   - Download: https://git-scm.com/downloads

Note: You do NOT need to install Gradle - the project includes a Gradle wrapper that handles it automatically!

Step-by-Step Setup

1. Clone the Repository
# Clone the project 
git clone https://github.com/apoorvagadkari/Fraud-Detection-API

# Navigate
cd Fraud-Detection-API

# Navigate into the project folder further
cd fraud-detection-api

2. Build the Project
# On Mac/Linux:
./gradlew build

# On Windows:
gradlew.bat build

This will:
- Download all dependencies
- Compile the code
- Run all tests
- Create executable JAR file

3. Run Tests (Optional)
# On Mac/Linux:
./gradlew test

# On Windows:
gradlew.bat test


4. Start the Server
# On Mac/Linux:
./gradlew bootRun

# On Windows:
gradlew.bat bootRun

Wait for:
Tomcat started on port 8080 (http) with context path '/'
Started DemoApplication in X.XXX seconds

The server is now running on http://localhost:8080
You can check by clicking on the URl above but you can skip as well, will still work fine


Now open a different terminal and again go to the same url (in fraud-detection-api)

# Testing the API

Using cURL (Mac/Linux/Windows Git Bash) 

curl -X POST http://localhost:8080/api/score-transaction \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Smith",
    "ipAddress": "8.8.8.8",
    "location": {"city": "Boston", "state": "MA"},
    "paymentDetails": {
      "cardLast4": "4567",
      "nameOnCard": "John Smith",
      "cardAmount": 150.00
    },
    "transactionDetails": {
      "merchantName": "Electronics Store",
      "merchantLocation": {"city": "Boston", "state": "MA"},
      "purchasedItemCount": 3
    }
  }'


Stopping the Server
Press Ctrl + C in the terminal where the server is running

Troubleshooting

Problem: "java: command not found"
- Solution: Install Java 23 from https://www.oracle.com/java/technologies/downloads/

Problem: "Port 8080 already in use"
- Solution:For Mac- lsof -i :8080
            kill -9 <PID>

            For Windows - netstat -ano | findstr :8080
                          taskkill /PID <PID> /F

Problem: "Permission denied: ./gradlew"
- Solution (Mac/Linux): chmod +x gradlew

Problem: Tests failing
- Solution: ./gradlew clean build



POST ENDPOINT
**POST**  https://example.com/api/score-transaction


REQUEST EXAMPLE
{
  "customerName": "John Doe",
  "ipAddress": "8.8.8.8",
  "location": {"city": "Boston", "state": "MA"},
  "paymentDetails": {
    "cardLast4": "1234",
    "nameOnCard": "John Doe",
    "cardAmount": 150.00
  },
  "transactionDetails": {
    "merchantName": "Electronics Store",
    "merchantLocation": {"city": "Boston", "state": "MA"},
    "purchasedItemCount": 3
  }
}

RESPONSE EXAMPLE
{
  "signals": [
    {
      "signal": "location",
      "potentialFraud": true,
      "details": ["New location for customer: Boston, MA"]
    },
    {
      "signal": "ipAddress",
      "potentialFraud": false,
      "details": ["IP address is not known to be fraudulent"]
    },
    {
      "signal": "transaction",
      "potentialFraud": false,
      "details": ["Transaction velocity normal: 0 transactions in last 10 minutes"]
    },
    {
      "signal": "cardDetails",
      "potentialFraud": false,
      "details": ["Customer name matches name on card"]
    }
  ]
}


# FRAUD DETECTION RULES

Location Signals:
Customer location ≠ merchant location
First time purchasing from this city/state

IP Address Signals:
IP in blacklist (192.168.1.100, 10.0.0.50, 172.16.0.200)
Private IP ranges (10., 192.168., 172.*)

Transaction Signals:
Amount > $1,000
Item count > 10
More than 3 transactions in 10 minutes (velocity)

Card Details Signals:
Name on card ≠ customer name
Card number has repeating digits pattern (e.g., 1111, 2222)



## TESTING
# Run all tests (32 tests across 6 test classes)
./gradlew test

# Test categories:
# - Unit tests: LocationSignalEvaluatorTest (4)
# - Unit tests: IpAddressSignalEvaluatorTest (5)
# - Unit tests: TransactionSignalEvaluatorTest (4)
# - Unit tests: CardDetailsSignalEvaluatorTest (7)
# - Service tests: FraudDetectionServiceTest (4)
# - Integration tests: FraudDetectionControllerIntegrationTest (8)





# DESIGN DECISIONS
In-memory storage: TransactionHistoryService uses ConcurrentHashMap for simplicity (production would use database)
Independent evaluators: Each fraud signal runs independently for modularity
Stateless evaluators: Only TransactionHistoryService maintains state
Validation: Jakarta Bean Validation for request validation
Error handling: GlobalExceptionHandler for consistent error responses

# FUTURE SCOPE
Use machine learning for anomaly detection
Use a proper database
Create a dashboard

## QUICK SUMMARY OF THINGS I HAVE ADDED APART FROM THE ONE DISCUSSED ABOVE
Code Clarity - Proper class names, comments, file structure
Tests - Unit, Service and Integration Testing
Patterns - SOLID,DRY
Abstractions - Single structure
Clear boundaries between API and Domain Layer, Controller and Service Layer, Doamin and Storage, Service and Domain
Maintainabilty - Comments, Self documented code, easy to modify methods, easy to fix bugs and don't have ripple effect across classes, easy to mock for testing
Less changes to the codebase even if we want to modify a feature
Usage of in-memory storage using a ConcurrentHashMap to track past records of users -- check a partcular's location transaction history and how many transactions have been made by a particular user in the last 10 minutes
