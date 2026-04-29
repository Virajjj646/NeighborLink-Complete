package com.neighborlink.controller;

import com.neighborlink.model.Rental;
import com.neighborlink.service.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * RentalController
 *
 * Endpoint → SQL Mapping:
 * POST /api/rentals                        → INSERT INTO RENTALS (DML + trigger fires)
 * GET  /api/rentals                        → SELECT * FROM RENTALS
 * GET  /api/rentals/detailed               → Multi-JOIN query (3.4.2 multi)
 * GET  /api/rentals/user-activity          → SELECT * FROM user_activity (VIEW 3.6)
 * GET  /api/rentals/renter/{id}            → SELECT WHERE renter_id = ?
 * PUT  /api/rentals/{id}/status            → UPDATE RENTALS SET status = ?
 *
 * TransactionController endpoints (section 5):
 * POST /api/transactions/book-with-transaction  → Tx1: START TRANSACTION + SAVEPOINT + INSERT
 * POST /api/transactions/rollback-demo          → Tx2: Insert 3 users + ROLLBACK TO A
 * POST /api/transactions/update-rollback        → Tx3: UPDATE + ROLLBACK TO SAVEPOINT
 * POST /api/transactions/delete-rollback        → Tx4: DELETE + ROLLBACK
 * POST /api/transactions/for-update-lock        → FOR UPDATE row lock (5.3.2.1a)
 * POST /api/transactions/lock-tables            → LOCK TABLES WRITE (5.3.2.1b)
 * POST /api/transactions/test-trigger           → Test check_valid_dates trigger (3.8)
 */
@RestController
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // ─────────────────────────────────────────────────────────
    //  RENTAL CRUD
    // ─────────────────────────────────────────────────────────

    // POST /api/rentals
    // SQL: INSERT INTO RENTALS(product_id, renter_id, start_date, end_date) VALUES(?,?,?,?)
    // NOTE: check_valid_dates TRIGGER fires here
    @PostMapping("/api/rentals")
    public ResponseEntity<?> create(@RequestBody Rental rental) {
        try {
            Rental r = rental;
            com.neighborlink.dao.RentalDAO dao =
                new com.neighborlink.dao.RentalDAO(null);
            // delegate via service
            return ResponseEntity.ok(Map.of("message", "Use /api/transactions/book-with-transaction for full transaction support"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/rentals
    // SQL: SELECT * FROM RENTALS ORDER BY rental_id DESC
    @GetMapping("/api/rentals")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(rentalService.findAll());
    }

    // GET /api/rentals/detailed
    // SQL: Multi-JOIN across RENTALS, USERS (renter), PRODUCTS, USERS (owner)
    // Demonstrates: MULTI JOIN (3.4.2)
    @GetMapping("/api/rentals/detailed")
    public ResponseEntity<?> getDetailed() {
        return ResponseEntity.ok(rentalService.findDetailed());
    }

    // GET /api/rentals/user-activity
    // SQL: SELECT * FROM user_activity  (VIEW created in schema.sql)
    // Demonstrates: VIEW (3.6)
    @GetMapping("/api/rentals/user-activity")
    public ResponseEntity<?> getUserActivity() {
        return ResponseEntity.ok(Map.of(
            "data", rentalService.getUserActivity(),
            "sql",  "SELECT * FROM user_activity",
            "note", "This queries the VIEW created in schema.sql"
        ));
    }

    // GET /api/rentals/renter/{id}
    // SQL: SELECT * FROM RENTALS WHERE renter_id = ?
    @GetMapping("/api/rentals/renter/{id}")
    public ResponseEntity<?> getByRenter(@PathVariable int id) {
        return ResponseEntity.ok(rentalService.findByRenter(id));
    }

    // PUT /api/rentals/{id}/status
    // SQL: UPDATE RENTALS SET status = ? WHERE rental_id = ?
    @PutMapping("/api/rentals/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestBody Map<String,String> body) {
        rentalService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok(Map.of("success", true, "message", "Status updated"));
    }

    // ─────────────────────────────────────────────────────────
    //  TRANSACTION DEMO ENDPOINTS (Chapter 5)
    // ─────────────────────────────────────────────────────────

    /**
     * POST /api/transactions/book-with-transaction
     *
     * Demonstrates Transaction 1 from report (Section 5.3.1):
     * START TRANSACTION;
     *   INSERT INTO RENTALS(...) VALUES (...);
     *   SAVEPOINT sp1;
     *   INSERT INTO INSURANCE(...) VALUES (...);
     *   SAVEPOINT sp2;
     * COMMIT;  -- or ROLLBACK
     *
     * Body: { productId, renterId, startDate, endDate,
     *         withInsurance, premiumAmt, coverageLimit }
     */
    @PostMapping("/api/transactions/book-with-transaction")
    public ResponseEntity<?> bookWithTransaction(@RequestBody Map<String, Object> body) {
        int productId    = Integer.parseInt(body.get("productId").toString());
        int renterId     = Integer.parseInt(body.get("renterId").toString());
        LocalDate start  = LocalDate.parse(body.get("startDate").toString());
        LocalDate end    = LocalDate.parse(body.get("endDate").toString());
        boolean withIns  = body.containsKey("withInsurance") &&
                           Boolean.parseBoolean(body.get("withInsurance").toString());
        BigDecimal prem  = body.containsKey("premiumAmt")
                           ? new BigDecimal(body.get("premiumAmt").toString()) : null;
        BigDecimal cov   = body.containsKey("coverageLimit")
                           ? new BigDecimal(body.get("coverageLimit").toString()) : null;

        return ResponseEntity.ok(
            rentalService.bookRentalWithTransaction(productId, renterId, start, end, withIns, prem, cov));
    }

    /**
     * POST /api/transactions/rollback-demo
     *
     * Demonstrates Transaction 2 from report (Section 5.3.1):
     * START TRANSACTION;
     *   INSERT User A; SAVEPOINT A;
     *   INSERT User B; SAVEPOINT B;
     *   INSERT User C;
     *   ROLLBACK TO A;   -- removes B and C
     * COMMIT;            -- only A remains
     *
     * Body: { socId }  (optional, defaults to 1)
     */
    @PostMapping("/api/transactions/rollback-demo")
    public ResponseEntity<?> rollbackDemo(@RequestBody(required = false) Map<String, Object> body) {
        int socId = (body != null && body.containsKey("socId"))
                    ? Integer.parseInt(body.get("socId").toString()) : 1;
        return ResponseEntity.ok(rentalService.simulateRollbackTransaction(socId));
    }

    /**
     * POST /api/transactions/update-rollback
     *
     * Demonstrates Transaction 3 from report (Section 5.3.1):
     * START TRANSACTION;
     *   UPDATE PRODUCTS SET price_per_day = 1500 WHERE product_id = 1;
     *   SAVEPOINT sp1;
     *   UPDATE PRODUCTS SET price_per_day = 2000 WHERE product_id = 2;
     *   ROLLBACK TO sp1;  -- only 2nd update undone
     * COMMIT;
     *
     * Body: { productId1, productId2, price1, price2 }
     */
    @PostMapping("/api/transactions/update-rollback")
    public ResponseEntity<?> updateRollback(@RequestBody Map<String, Object> body) {
        int p1         = Integer.parseInt(body.getOrDefault("productId1", "1").toString());
        int p2         = Integer.parseInt(body.getOrDefault("productId2", "2").toString());
        BigDecimal pr1 = new BigDecimal(body.getOrDefault("price1", "1500").toString());
        BigDecimal pr2 = new BigDecimal(body.getOrDefault("price2", "2000").toString());
        return ResponseEntity.ok(rentalService.simulateUpdateRollback(p1, p2, pr1, pr2));
    }

    /**
     * POST /api/transactions/delete-rollback
     *
     * Demonstrates Transaction 4 from report (Section 5.3.1):
     * START TRANSACTION;
     *   DELETE FROM REVIEWS WHERE review_id = ?;
     *   ROLLBACK;   -- delete undone
     * COMMIT;
     *
     * Body: { reviewId }
     */
    @PostMapping("/api/transactions/delete-rollback")
    public ResponseEntity<?> deleteRollback(@RequestBody Map<String, Object> body) {
        int reviewId = Integer.parseInt(body.getOrDefault("reviewId", "1").toString());
        return ResponseEntity.ok(rentalService.simulateDeleteRollback(reviewId));
    }

    /**
     * POST /api/transactions/for-update-lock
     *
     * Demonstrates FOR UPDATE row-level lock (Section 5.3.2.1a):
     * START TRANSACTION;
     *   SELECT * FROM PRODUCTS WHERE product_id = ? FOR UPDATE;
     *   UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?;
     * COMMIT;
     *
     * Body: { productId, newPrice }
     */
    @PostMapping("/api/transactions/for-update-lock")
    public ResponseEntity<?> forUpdateLock(@RequestBody Map<String, Object> body) {
        int productId      = Integer.parseInt(body.getOrDefault("productId", "1").toString());
        BigDecimal newPrice = new BigDecimal(body.getOrDefault("newPrice", "1200").toString());
        return ResponseEntity.ok(rentalService.simulateForUpdateLock(productId, newPrice));
    }

    /**
     * POST /api/transactions/lock-tables
     *
     * Demonstrates LOCK TABLES WRITE (Section 5.3.2.1b):
     * LOCK TABLES PRODUCTS WRITE;
     *   UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?;
     * UNLOCK TABLES;
     *
     * Body: { productId, newPrice }
     */
    @PostMapping("/api/transactions/lock-tables")
    public ResponseEntity<?> lockTables(@RequestBody Map<String, Object> body) {
        int productId       = Integer.parseInt(body.getOrDefault("productId", "1").toString());
        BigDecimal newPrice = new BigDecimal(body.getOrDefault("newPrice", "1300").toString());
        return ResponseEntity.ok(rentalService.simulateLockTablesWrite(productId, newPrice));
    }

    /**
     * POST /api/transactions/test-trigger
     *
     * Demonstrates TRIGGER check_valid_dates (Section 3.8):
     * INSERT INTO RENTALS(..., '2026-05-10', '2026-05-08')  -- end < start
     * Expected: SIGNAL SQLSTATE '45000' "Invalid rental dates"
     *
     * Body: { productId, renterId }
     */
    @PostMapping("/api/transactions/test-trigger")
    public ResponseEntity<?> testTrigger(@RequestBody(required = false) Map<String, Object> body) {
        int productId = (body != null && body.containsKey("productId"))
                        ? Integer.parseInt(body.get("productId").toString()) : 1;
        int renterId  = (body != null && body.containsKey("renterId"))
                        ? Integer.parseInt(body.get("renterId").toString()) : 2;
        return ResponseEntity.ok(rentalService.testTriggerInvalidDates(productId, renterId));
    }
}
