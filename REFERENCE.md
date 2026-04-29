# NeighborLink — Complete Project Reference

---

## 📁 FILE CHECKLIST (paste in IntelliJ in this order)

### SQL Files (run in MySQL Workbench)
- [ ] sql/schema.sql
- [ ] sql/data.sql

### Backend — root
- [ ] pom.xml

### src/main/resources/
- [ ] application.properties

### src/main/java/com/neighborlink/
- [ ] NeighborlinkApplication.java

### config/
- [ ] CorsConfig.java

### exception/
- [ ] GlobalExceptionHandler.java
- [ ] ResourceNotFoundException.java
- [ ] BadRequestException.java

### model/
- [ ] Society.java
- [ ] User.java
- [ ] Product.java
- [ ] Rental.java
- [ ] Insurance.java
- [ ] Review.java

### dao/
- [ ] SocietyDAO.java
- [ ] UserDAO.java
- [ ] ProductDAO.java
- [ ] RentalDAO.java
- [ ] InsuranceDAO.java
- [ ] ReviewDAO.java

### service/
- [ ] UserService.java
- [ ] ProductService.java
- [ ] RentalService.java
- [ ] InsuranceService.java
- [ ] ReviewService.java

### controller/
- [ ] UserController.java
- [ ] ProductController.java
- [ ] RentalController.java   (also contains /api/transactions/* endpoints)
- [ ] InsuranceController.java
- [ ] ReviewController.java
- [ ] SocietyController.java

---

## ⚙️ STEP-BY-STEP RUNNING INSTRUCTIONS

---

### STEP 1 — Install Prerequisites

Make sure you have installed:
- Java 17+ (check: `java -version`)
- Maven 3.8+ (check: `mvn -version`)
- MySQL 8.0+ (check: `mysql --version`)
- IntelliJ IDEA (Community or Ultimate)

---

### STEP 2 — Setup MySQL Database

Open MySQL Workbench (or terminal) and run:

```sql
-- In MySQL terminal
mysql -u root -p
```

Then run the schema file:

```sql
SOURCE /path/to/sql/schema.sql;
```

Then insert sample data:

```sql
SOURCE /path/to/sql/data.sql;
```

Or in MySQL Workbench:
1. File → Open SQL Script → select schema.sql → click Run (⚡)
2. File → Open SQL Script → select data.sql   → click Run (⚡)

Verify:
```sql
USE neighborlink_db;
SHOW TABLES;
SELECT * FROM USERS LIMIT 5;
SELECT * FROM PRODUCTS LIMIT 5;
```

Expected tables:
- SOCIETY, USERS, PRODUCTS, RENTALS, INSURANCE, REVIEWS
- VIEW: user_activity
- FUNCTION: total_rentals_by_user
- TRIGGERS: check_valid_dates, update_trust_score_after_review
- PROCEDURES: insert_user_safe, show_products

---

### STEP 3 — Configure application.properties

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/neighborlink_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL root password.

If you use a different MySQL user:
```properties
spring.datasource.username=neighborlink_user
spring.datasource.password=yourpassword
```

---

### STEP 4 — Open Project in IntelliJ

Option A — Spring Initializr (recommended):
1. Go to https://start.spring.io
2. Settings:
   - Project: Maven
   - Language: Java
   - Spring Boot: 3.2.5
   - Group: com.neighborlink
   - Artifact: neighborlink-backend
   - Java: 17
3. Add Dependencies:
   - Spring Web
   - Spring JDBC
   - MySQL Driver
   - Lombok
4. Click GENERATE → download ZIP
5. Unzip → Open in IntelliJ (File → Open → select folder)

Option B — IntelliJ directly:
1. File → New → Project
2. Select Spring Boot on the left
3. Fill same details as above
4. Add same dependencies
5. Click Create

Then paste all Java files into their correct packages.

---

### STEP 5 — Run Spring Boot

In IntelliJ:
1. Open `NeighborlinkApplication.java`
2. Click the green ▶ Run button next to the main method
3. Or right-click → Run 'NeighborlinkApplication'

In terminal:
```bash
cd neighborlink-backend
mvn spring-boot:run
```

You should see:
```
Started NeighborlinkApplication on port 8080
```

Test it:
```
http://localhost:8080/api/societies
```

Should return JSON with 4 societies.

---

### STEP 6 — Connect Stitch Frontend

In your Stitch-generated frontend, make sure the API base URL is:
```
http://localhost:8080/api
```

All fetch calls should point to this base.

CORS is already enabled in `CorsConfig.java` for all origins.

---

### STEP 7 — Test APIs with Postman

Import these requests into Postman:

**Base URL:** `http://localhost:8080/api`

#### 🔐 Auth
```
POST /users/register
Body: { "socId": 1, "username": "TestUser", "email": "test@mail.com", "flatNo": "A-100", "password": "pass123" }

POST /users/login
Body: { "email": "viraj@mail.com", "password": "password123" }
```

#### 👤 Users
```
GET  /users
GET  /users/1
GET  /users/count
GET  /users/renters
GET  /users/non-renters
GET  /users/union
GET  /users/1/trust-score
GET  /users/1/total-rentals
GET  /users/societies
```

#### 📦 Products
```
POST /products
Body: { "ownerId": 1, "title": "My Drill", "description": "Bosch drill", "pricePerDay": 200, "itemValue": 5000 }

GET  /products
GET  /products/1
GET  /products/stats
GET  /products/with-owners
GET  /products/left-join
GET  /products/above-average
GET  /products/multi-join
GET  /products/rental-counts
GET  /products/owner/1
GET  /products/1/availability?start=2026-06-01&end=2026-06-05

PUT  /products/1/price
Body: { "price": 350 }

DELETE /products/21
```

#### 🏠 Rentals
```
GET  /rentals
GET  /rentals/detailed
GET  /rentals/user-activity
GET  /rentals/renter/1

PUT  /rentals/1/status
Body: { "status": "COMPLETED" }
```

#### 🏦 Insurance
```
POST /insurance
Body: { "rentalId": 3, "premiumAmt": 45, "coverageLimit": 8000 }

GET  /insurance
GET  /insurance/rental/1
```

#### ⭐ Reviews
```
POST /reviews
Body: { "rentalId": 3, "reviewerId": 2, "rating": 5, "comment": "Excellent!" }

GET  /reviews
GET  /reviews/detailed
GET  /reviews/trust/1

DELETE /reviews/1
```

---

## 🧪 TRANSACTION & DBMS DEMO ENDPOINTS

These are the most important for your DBMS demo:

### Transaction 1 — Rental + Insurance with SAVEPOINT
```
POST /transactions/book-with-transaction
Body:
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
Response shows: ✅ Rental inserted → 📍 SAVEPOINT → ✅ Insurance → ✅ COMMITTED

### Transaction 2 — Insert 3 Users + ROLLBACK TO SAVEPOINT A
```
POST /transactions/rollback-demo
Body: { "socId": 1 }
```
Response shows: User A inserted → SAVEPOINT A → User B → User C → ROLLBACK TO A → only A saved

### Transaction 3 — Update Price + Partial Rollback
```
POST /transactions/update-rollback
Body:
{
  "productId1": 1,
  "productId2": 2,
  "price1": 1500,
  "price2": 2000
}
```
Response shows: product 1 updated → SAVEPOINT → product 2 updated → ROLLBACK TO sp1 → product 2 reverted

### Transaction 4 — Delete + Full Rollback
```
POST /transactions/delete-rollback
Body: { "reviewId": 1 }
```
Response shows: review deleted → ROLLBACK → review restored

### Trigger Test — Invalid Dates
```
POST /transactions/test-trigger
Body: { "productId": 1, "renterId": 2 }
```
Response shows: ✅ TRIGGER FIRED — invalid dates rejected

### Concurrency — FOR UPDATE Row Lock
```
POST /transactions/for-update-lock
Body: { "productId": 1, "newPrice": 1200 }
```
Response shows: 🔒 Row locked → UPDATE → ✅ COMMITTED

### Concurrency — LOCK TABLES WRITE
```
POST /transactions/lock-tables
Body: { "productId": 2, "newPrice": 1300 }
```
Response shows: 🔒 LOCK TABLES → UPDATE → 🔓 UNLOCK

---

## 📊 MAPPING: Report Queries → Backend Code → API Endpoint

| Report Section | SQL Query | Java Method | API Endpoint |
|---|---|---|---|
| 3.1.1 COUNT | SELECT COUNT(*) FROM USERS | userDAO.countAll() | GET /api/users/count |
| 3.1.1 GROUP | SELECT product_id, COUNT(*) FROM RENTALS GROUP BY product_id | productDAO.rentalCountPerProduct() | GET /api/products/rental-counts |
| 3.1.2 MAX | SELECT MAX(price_per_day) FROM PRODUCTS | productDAO.getStats() | GET /api/products/stats |
| 3.1.3 AVG | SELECT AVG(price_per_day) FROM PRODUCTS | productDAO.getStats() | GET /api/products/stats |
| 3.3.1 UNION | SELECT username FROM USERS UNION SELECT title FROM PRODUCTS | userDAO.unionUsernamesAndProducts() | GET /api/users/union |
| 3.3.2 IN | WHERE user_id IN (SELECT renter_id FROM RENTALS) | userDAO.findUsernamesWhoRented() | GET /api/users/renters |
| 3.3.3 NOT IN | WHERE user_id NOT IN (SELECT renter_id FROM RENTALS) | userDAO.findUsernamesWhoNeverRented() | GET /api/users/non-renters |
| 3.4.1 INNER JOIN | SELECT u.username, p.title FROM USERS u INNER JOIN PRODUCTS p | productDAO.findWithOwners() | GET /api/products/with-owners |
| 3.4.2 LEFT JOIN | SELECT u.username, p.title FROM USERS u LEFT JOIN PRODUCTS p | productDAO.findWithOwnersLeftJoin() | GET /api/products/left-join |
| 3.4.2 MULTI JOIN | SELECT ... FROM RENTALS JOIN USERS JOIN PRODUCTS JOIN USERS | rentalDAO.findDetailed() | GET /api/rentals/detailed |
| 3.5 SUBQUERY | WHERE price_per_day > (SELECT AVG(price_per_day) FROM PRODUCTS) | productDAO.findAboveAveragePrice() | GET /api/products/above-average |
| 3.6 VIEW | SELECT * FROM user_activity | rentalDAO.getUserActivity() | GET /api/rentals/user-activity |
| 3.7 FUNCTION | SELECT total_rentals_by_user(uid) | userDAO.callTotalRentalsByUser(id) | GET /api/users/{id}/total-rentals |
| 3.8 TRIGGER | BEFORE INSERT ON RENTALS — check_valid_dates | rentalService.testTriggerInvalidDates() | POST /api/transactions/test-trigger |
| 3.2.2 CHECK | rating BETWEEN 1 AND 5 | reviewService.create() | POST /api/reviews |
| 5.3.1 Tx1 | START TRANSACTION + SAVEPOINT + INSERT + COMMIT | rentalService.bookRentalWithTransaction() | POST /api/transactions/book-with-transaction |
| 5.3.1 Tx2 | INSERT×3 + ROLLBACK TO SAVEPOINT A | rentalService.simulateRollbackTransaction() | POST /api/transactions/rollback-demo |
| 5.3.1 Tx3 | UPDATE + SAVEPOINT + ROLLBACK TO sp1 | rentalService.simulateUpdateRollback() | POST /api/transactions/update-rollback |
| 5.3.1 Tx4 | DELETE + ROLLBACK | rentalService.simulateDeleteRollback() | POST /api/transactions/delete-rollback |
| 5.3.2.1a FOR UPDATE | SELECT ... FOR UPDATE + UPDATE + COMMIT | rentalService.simulateForUpdateLock() | POST /api/transactions/for-update-lock |
| 5.3.2.1b LOCK TABLES | LOCK TABLES WRITE + UPDATE + UNLOCK | rentalService.simulateLockTablesWrite() | POST /api/transactions/lock-tables |
| Trust Score | AVG(rating) JOIN chain → UPDATE USERS.trust_score | TRIGGER: update_trust_score_after_review | POST /api/reviews (trigger auto-fires) |
| DML INSERT | INSERT INTO USERS | userDAO.register() | POST /api/users/register |
| DML INSERT | INSERT INTO PRODUCTS | productDAO.create() | POST /api/products |
| DML INSERT | INSERT INTO RENTALS | rentalDAO.create() | POST /api/rentals |
| DML UPDATE | UPDATE RENTALS SET status | rentalDAO.updateStatus() | PUT /api/rentals/{id}/status |
| DML DELETE | DELETE FROM PRODUCTS | productDAO.delete() | DELETE /api/products/{id} |
| Concurrency | SET TRANSACTION ISOLATION LEVEL SERIALIZABLE | via Connection.setTransactionIsolation() | POST /api/transactions/for-update-lock |

---

## 🔧 COMMON ERRORS & FIXES

| Error | Fix |
|---|---|
| `Access denied for user 'root'@'localhost'` | Wrong password in application.properties |
| `Unknown database 'neighborlink_db'` | Run schema.sql first |
| `Table 'neighborlink_db.users' doesn't exist` | schema.sql did not execute fully — rerun |
| `Port 8080 already in use` | Change `server.port=8081` in application.properties |
| `CORS error in browser` | CorsConfig.java must be present, backend must be running |
| `Could not autowire JdbcTemplate` | Make sure spring-boot-starter-jdbc is in pom.xml |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | mysql-connector-j dependency missing in pom.xml |
| Trigger error on valid dates | Trigger is working correctly — use end_date > start_date |
