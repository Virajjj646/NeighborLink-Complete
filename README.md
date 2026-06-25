# 🏘️ NeighborLink — Community Marketplace Platform

A full-stack web application that enables neighbors in residential communities to buy, rent, and insure items with each other. Built with a Spring Boot backend, MySQL database, and interactive HTML/CSS/JavaScript frontend.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Testing with Postman](#testing-with-postman)
- [DBMS Features Demo](#dbms-features-demo)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

**NeighborLink** is a community marketplace platform designed to facilitate sharing and renting of items between neighbors in residential societies. The platform includes:

- 👥 **User Management**: Registration, login, trust scoring based on reviews
- 📦 **Product Listing**: Own items for rent with pricing and availability tracking
- 🔄 **Rental System**: Book rentals with transaction tracking and status management
- 🛡️ **Insurance**: Optional coverage for rentals with premium and coverage limits
- ⭐ **Reviews & Ratings**: Trust-based system to maintain community integrity
- 🏘️ **Multi-Society Support**: Organized by residential societies

This is a **DBMS lab project** demonstrating advanced database concepts including transactions, triggers, stored procedures, views, and complex SQL queries.

---

## ✨ Key Features

### User Features
- **User Registration & Authentication**: Secure login with email and password
- **Profile Management**: Track trust score, rental history, and activity
- **Trust Scoring**: Automatic calculation based on review ratings

### Product Management
- **Create Listings**: List items available for rent
- **Browse Catalog**: View all available products with owner information
- **Availability Checking**: Check rental availability by date range
- **Pricing**: Dynamic pricing per day with average price calculations

### Rental System
- **Book Rentals**: Request rentals with date ranges
- **Status Tracking**: Monitor rental lifecycle (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- **Transaction Safety**: All operations use database transactions with savepoints
- **Concurrency Control**: Row-level locking and serializable isolation levels

### Insurance Coverage
- **Optional Coverage**: Add insurance to rentals for peace of mind
- **Premium Calculation**: Customizable premiums and coverage limits
- **Rental Integration**: Automatically linked to rentals

### Reviews & Ratings
- **5-Star Rating System**: Rate rental experiences
- **Comments**: Detailed feedback on products and services
- **Automatic Trust Updates**: Triggers update user trust scores

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Backend** | Spring Boot | 3.2.5 |
| **Language** | Java | 17 |
| **Database** | MySQL | 8.0+ |
| **Data Access** | JDBC (Spring JDBC) | 3.2.5 |
| **Frontend** | HTML5, CSS3, JavaScript | ES6+ |
| **Build Tool** | Maven | 3.8+ |
| **IDE** | IntelliJ IDEA | Community/Ultimate |

**Key Dependencies:**
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-jdbc` - Database connectivity
- `mysql-connector-j` - MySQL driver
- `lombok` - Reduce boilerplate code

---

## 📁 Project Structure

```
NeighborLink-Complete/
├── backend/                           # Spring Boot Backend
│   ├── pom.xml                       # Maven dependencies
│   ├── .gitignore                    # Git ignore rules
│   └── src/main/
│       ├── java/com/neighborlink/
│       │   ├── NeighborlinkApplication.java    # Main entry point
│       │   ├── config/
│       │   │   └── CorsConfig.java            # CORS configuration
│       │   ├── exception/
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── ResourceNotFoundException.java
│       │   │   └── BadRequestException.java
│       │   ├── model/                         # Entity classes
│       │   │   ├── Society.java
│       │   │   ├── User.java
│       │   │   ├── Product.java
│       │   │   ├── Rental.java
│       │   │   ├── Insurance.java
│       │   │   └── Review.java
│       │   ├── dao/                          # Data Access Objects
│       │   │   ├── SocietyDAO.java
│       │   │   ├── UserDAO.java
│       │   │   ├── ProductDAO.java
│       │   │   ├── RentalDAO.java
│       │   │   ├── InsuranceDAO.java
│       │   │   └── ReviewDAO.java
│       │   ├── service/                      # Business Logic
│       │   │   ├── UserService.java
│       │   │   ├── ProductService.java
│       │   │   ├── RentalService.java
│       │   │   ├── InsuranceService.java
│       │   │   └── ReviewService.java
│       │   └── controller/                   # REST Controllers
│       │       ├── UserController.java
│       │       ├── ProductController.java
│       │       ├── RentalController.java
│       │       ├── InsuranceController.java
│       │       ├── ReviewController.java
│       │       └── SocietyController.java
│       └── resources/
│           └── application.properties        # Configuration
│
├── frontend/                          # HTML/CSS/JavaScript Frontend
│   ├── index.html                    # Home page
│   ├── dashboard.html                # User dashboard
│   ├── create_listing.html           # Create product listing
│   ├── browse_listings.html          # Browse products
│   ├── my_rentals.html               # View rentals
│   ├── dbms_lab.html                 # DBMS features demo
│   ├── style.css                     # Styling
│   ├── config.js                     # API configuration
│   └── utils.js                      # Helper functions
│
├── sql/                              # Database Scripts
│   ├── schema.sql                    # Table definitions, triggers, procedures
│   └── data.sql                      # Sample data
│
└── REFERENCE.md                      # Detailed setup guide
```

---

## 📋 Prerequisites

Before running the project, ensure you have installed:

- **Java 17+** — [Download](https://www.oracle.com/java/technologies/downloads/#java17)
  ```bash
  java -version
  ```

- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
  ```bash
  mvn -version
  ```

- **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/mysql/)
  ```bash
  mysql --version
  ```

- **IntelliJ IDEA** (optional but recommended) — [Download](https://www.jetbrains.com/idea/download/)

---

## 🚀 Setup Instructions

### Step 1: Clone the Repository

```bash
git clone https://github.com/Virajjj646/NeighborLink-Complete.git
cd NeighborLink-Complete
```

### Step 2: Setup MySQL Database

1. Open MySQL terminal or MySQL Workbench:
   ```bash
   mysql -u root -p
   ```

2. Execute the schema script:
   ```sql
   SOURCE /path/to/sql/schema.sql;
   ```

3. Load sample data:
   ```sql
   SOURCE /path/to/sql/data.sql;
   ```

4. Verify the setup:
   ```sql
   USE neighborlink_db;
   SHOW TABLES;
   SELECT COUNT(*) FROM USERS;
   ```

Expected tables: `SOCIETY`, `USERS`, `PRODUCTS`, `RENTALS`, `INSURANCE`, `REVIEWS`

### Step 3: Configure Application Properties

Edit `backend/src/main/resources/application.properties`:

```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/neighborlink_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE

# Server Port
server.port=8080

# Logging Level
logging.level.root=INFO
logging.level.com.neighborlink=DEBUG
```

Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL root password.

### Step 4: Open in IntelliJ IDEA

**Option A: Create from Spring Initializr**
1. Go to https://start.spring.io
2. Configure:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.2.5
   - Group: com.neighborlink
   - Artifact: neighborlink-backend
   - Java: 17
3. Add dependencies:
   - Spring Web
   - Spring JDBC
   - MySQL Driver
   - Lombok
4. Download and open in IntelliJ

**Option B: Open Directly**
1. File → Open → Select the cloned folder
2. IntelliJ should auto-detect the Maven project
3. Wait for dependencies to download

---

## ▶️ Running the Application

### Start the Backend

**Using IntelliJ:**
1. Open `src/main/java/com/neighborlink/NeighborlinkApplication.java`
2. Click the green ▶ Run button
3. Or right-click → Run 'NeighborlinkApplication'

**Using Terminal:**
```bash
cd backend
mvn spring-boot:run
```

You should see:
```
Started NeighborlinkApplication on port 8080
```

### Open the Frontend

1. Navigate to the `frontend/` directory
2. Open `index.html` in your web browser
3. Or serve using a local web server:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js (npx)
   npx http-server
   ```

### Test the Connection

```bash
curl http://localhost:8080/api/societies
```

Should return JSON with societies:
```json
[
  {"socId": 1, "socName": "Green Valley Society"},
  {"socId": 2, "socName": "Sunshine Apartments"},
  ...
]
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api
```

### Authentication

#### Register User
```http
POST /users/register
Content-Type: application/json

{
  "socId": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "flatNo": "A-101",
  "password": "securepass123"
}
```

#### Login
```http
POST /users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securepass123"
}
```

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | List all users |
| GET | `/users/:id` | Get user details |
| GET | `/users/count` | Count total users |
| GET | `/users/renters` | Users who have rented items |
| GET | `/users/non-renters` | Users who haven't rented |
| GET | `/users/union` | UNION: usernames and product titles |
| GET | `/users/:id/trust-score` | Get user's trust score |
| GET | `/users/:id/total-rentals` | Total rentals by user |

### Product Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/products` | Create new product listing |
| GET | `/products` | List all products |
| GET | `/products/:id` | Get product details |
| GET | `/products/stats` | Get price statistics |
| GET | `/products/with-owners` | Products with owner information (JOIN) |
| GET | `/products/left-join` | Products with optional owners (LEFT JOIN) |
| GET | `/products/above-average` | Products priced above average |
| GET | `/products/rental-counts` | Rental count per product (GROUP BY) |
| GET | `/products/owner/:userId` | Products by specific owner |
| GET | `/products/:id/availability?start=2026-06-01&end=2026-06-05` | Check availability |
| PUT | `/products/:id/price` | Update product price |
| DELETE | `/products/:id` | Delete product |

**Create Product Example:**
```json
{
  "ownerId": 1,
  "title": "Bosch Cordless Drill",
  "description": "18V professional drill in excellent condition",
  "pricePerDay": 200,
  "itemValue": 5000
}
```

### Rental Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/rentals` | Create new rental |
| GET | `/rentals` | List all rentals |
| GET | `/rentals/detailed` | Rentals with user and product details |
| GET | `/rentals/user-activity` | User activity view |
| GET | `/rentals/renter/:renterId` | Rentals by specific renter |
| PUT | `/rentals/:id/status` | Update rental status |

**Book Rental Example:**
```json
{
  "productId": 1,
  "renterId": 3,
  "startDate": "2026-07-01",
  "endDate": "2026-07-05"
}
```

### Insurance Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/insurance` | Add insurance to rental |
| GET | `/insurance` | List all insurance records |
| GET | `/insurance/rental/:rentalId` | Insurance for specific rental |

**Add Insurance Example:**
```json
{
  "rentalId": 1,
  "premiumAmt": 50,
  "coverageLimit": 20000
}
```

### Reviews & Ratings

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/reviews` | Create review (updates trust score) |
| GET | `/reviews` | List all reviews |
| GET | `/reviews/detailed` | Reviews with detailed information |
| GET | `/reviews/trust/:userId` | Reviews for specific user |
| DELETE | `/reviews/:id` | Delete review |

**Create Review Example:**
```json
{
  "rentalId": 1,
  "reviewerId": 2,
  "rating": 5,
  "comment": "Excellent product and smooth transaction!"
}
```

### Society Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/societies` | List all societies |
| GET | `/societies/:id` | Get society details |

---

## 🗄️ Database Schema

### Core Tables

#### SOCIETY
```sql
CREATE TABLE SOCIETY (
  soc_id INT PRIMARY KEY AUTO_INCREMENT,
  soc_name VARCHAR(100) NOT NULL,
  address VARCHAR(200),
  city VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### USERS
```sql
CREATE TABLE USERS (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  soc_id INT,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  flat_no VARCHAR(20),
  password_hash VARCHAR(255),
  trust_score DECIMAL(3,2) DEFAULT 0.0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (soc_id) REFERENCES SOCIETY(soc_id)
);
```

#### PRODUCTS
```sql
CREATE TABLE PRODUCTS (
  product_id INT PRIMARY KEY AUTO_INCREMENT,
  owner_id INT NOT NULL,
  title VARCHAR(150),
  description TEXT,
  price_per_day DECIMAL(10,2),
  item_value DECIMAL(10,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES USERS(user_id)
);
```

#### RENTALS
```sql
CREATE TABLE RENTALS (
  rental_id INT PRIMARY KEY AUTO_INCREMENT,
  product_id INT NOT NULL,
  renter_id INT NOT NULL,
  start_date DATE,
  end_date DATE,
  status ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'PENDING',
  total_cost DECIMAL(10,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES PRODUCTS(product_id),
  FOREIGN KEY (renter_id) REFERENCES USERS(user_id)
);
```

#### INSURANCE
```sql
CREATE TABLE INSURANCE (
  insurance_id INT PRIMARY KEY AUTO_INCREMENT,
  rental_id INT NOT NULL,
  premium_amt DECIMAL(10,2),
  coverage_limit DECIMAL(10,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (rental_id) REFERENCES RENTALS(rental_id)
);
```

#### REVIEWS
```sql
CREATE TABLE REVIEWS (
  review_id INT PRIMARY KEY AUTO_INCREMENT,
  rental_id INT NOT NULL,
  reviewer_id INT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (rental_id) REFERENCES RENTALS(rental_id),
  FOREIGN KEY (reviewer_id) REFERENCES USERS(user_id)
);
```

### Views

#### user_activity
Shows rental activity by user with counts and dates.

### Stored Functions

#### total_rentals_by_user(user_id)
Returns total number of rentals for a user.

### Triggers

#### check_valid_dates
Validates rental dates (end_date > start_date) before insert.

#### update_trust_score_after_review
Updates user trust score after new review is added.

---

## 🧪 Testing with Postman

### Import Requests

1. [Download Postman](https://www.postman.com/downloads/)
2. Create a new collection
3. Add requests using the API endpoints above

### Test Workflow

1. **Create a user:**
   ```
   POST /users/register
   ```

2. **Create a product:**
   ```
   POST /products
   ```

3. **Book a rental:**
   ```
   POST /rentals
   ```

4. **Add insurance:**
   ```
   POST /insurance
   ```

5. **Leave a review:**
   ```
   POST /reviews
   ```

6. **Verify trust score updated:**
   ```
   GET /users/{id}/trust-score
   ```

---

## 🎓 DBMS Features Demo

### Transaction Management

#### Demo 1: Rental with Insurance (Transaction + SAVEPOINT)
```http
POST /api/transactions/book-with-transaction
Content-Type: application/json

{
  "productId": 1,
  "renterId": 3,
  "startDate": "2026-07-01",
  "endDate": "2026-07-05",
  "withInsurance": true,
  "premiumAmt": 50,
  "coverageLimit": 20000
}
```

Shows: Transaction started → Rental inserted → SAVEPOINT created → Insurance added → COMMIT

#### Demo 2: Rollback to Savepoint
```http
POST /api/transactions/rollback-demo
Content-Type: application/json

{ "socId": 1 }
```

Shows: User A inserted → SAVEPOINT A → User B inserted → User C inserted → ROLLBACK TO A → Only User A persisted

### Concurrency Control

#### Demo 3: Row-Level Locking (FOR UPDATE)
```http
POST /api/transactions/for-update-lock
Content-Type: application/json

{
  "productId": 1,
  "newPrice": 1200
}
```

Shows: Row locked → UPDATE executed → Commit with lock release

#### Demo 4: Table-Level Locking
```http
POST /api/transactions/lock-tables
Content-Type: application/json

{
  "productId": 2,
  "newPrice": 1300
}
```

Shows: LOCK TABLES WRITE → UPDATE → UNLOCK TABLES

### Query Demonstrations

All queries are accessible via GET endpoints on `/api/products`, `/api/users`, `/api/rentals`:

- **COUNT**: `/users/count`
- **GROUP BY**: `/products/rental-counts`
- **JOIN**: `/products/with-owners`
- **LEFT JOIN**: `/products/left-join`
- **SUBQUERY**: `/products/above-average`
- **VIEW**: `/rentals/user-activity`
- **UNION**: `/users/union`
- **IN/NOT IN**: `/users/renters`, `/users/non-renters`

---

## 📚 Database Concepts Demonstrated

This project showcases:

- ✅ **Transactions** - ACID compliance with rollback
- ✅ **Savepoints** - Partial rollback capability
- ✅ **Triggers** - Automatic actions on data changes
- ✅ **Stored Procedures** - Reusable SQL logic
- ✅ **Stored Functions** - Returning calculated values
- ✅ **Views** - Pre-defined queries
- ✅ **JOINs** - INNER, LEFT, MULTI joins
- ✅ **Subqueries** - Nested queries
- ✅ **Aggregate Functions** - COUNT, MAX, AVG, SUM
- ✅ **GROUP BY** - Data aggregation
- ✅ **Concurrency Control** - Locks and isolation levels
- ✅ **CHECK Constraints** - Data validation
- ✅ **Foreign Keys** - Referential integrity

---

## 🐛 Troubleshooting

### Common Issues

| Error | Solution |
|-------|----------|
| `Access denied for user 'root'@'localhost'` | Verify MySQL password in `application.properties` |
| `Unknown database 'neighborlink_db'` | Run `schema.sql` first to create database |
| `Table doesn't exist` | Ensure `schema.sql` executed completely |
| `Port 8080 already in use` | Change `server.port` in `application.properties` |
| `CORS error in browser` | Verify `CorsConfig.java` is present and backend running |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add `mysql-connector-j` to `pom.xml` |
| `Could not autowire JdbcTemplate` | Add `spring-boot-starter-jdbc` dependency |

### Debug Mode

Enable debug logging in `application.properties`:
```properties
logging.level.com.neighborlink=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.jdbc=DEBUG
```

---

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [JDBC Guide](https://docs.oracle.com/javase/tutorial/jdbc/)
- [REFERENCE.md](./REFERENCE.md) - Detailed setup and API mapping

---

## 👨‍💻 Author

**Viraj** — [GitHub Profile](https://github.com/Virajjj646)

---

## 📝 License

This project is open source and available for educational purposes.

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

---

**Last Updated:** June 2026

For questions or issues, please open an [issue](https://github.com/Virajjj646/NeighborLink-Complete/issues) on GitHub.
