package com.neighborlink.service;

import com.neighborlink.dao.*;
import com.neighborlink.model.*;
import com.neighborlink.exception.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.*;

/**
 * RentalService
 *
 * Implements all TCL concepts from Chapter 5 of the report:
 *  - Transaction 1: Rental Booking with SAVEPOINT  (section 5.3.1)
 *  - Transaction 2: Multiple Inserts + Rollback     (section 5.3.1 Tx2)
 *  - Transaction 3: Update + Rollback to SAVEPOINT  (section 5.3.1 Tx3)
 *  - Transaction 4: Delete + Rollback               (section 5.3.1 Tx4)
 *  - FOR UPDATE row-level lock                      (section 5.3.2.1a)
 *  - LOCK TABLES exclusive write lock               (section 5.3.2.1b)
 */
@Service
public class RentalService {

    private final RentalDAO    rentalDAO;
    private final InsuranceDAO insuranceDAO;
    private final ProductDAO   productDAO;
    private final JdbcTemplate jdbc;

    public RentalService(RentalDAO rentalDAO, InsuranceDAO insuranceDAO,
                         ProductDAO productDAO, JdbcTemplate jdbc) {
        this.rentalDAO    = rentalDAO;
        this.insuranceDAO = insuranceDAO;
        this.productDAO   = productDAO;
        this.jdbc         = jdbc;
    }

    // ────────────────────────────────────────────────────────────
    // TRANSACTION 1: Rental Booking with SAVEPOINT (Section 5.3.1)
    // ────────────────────────────────────────────────────────────
    // START TRANSACTION;
    //   INSERT INTO RENTALS(...) VALUES (...);         -- sp_before_insurance
    //   SAVEPOINT sp1;
    //   INSERT INTO INSURANCE(rental_id, ...) VALUES (...);
    //   SAVEPOINT sp2;
    //   ... commit or rollback to sp1
    // COMMIT;
    public Map<String, Object> bookRentalWithTransaction(
            int productId, int renterId,
            LocalDate startDate, LocalDate endDate,
            boolean withInsurance, BigDecimal premiumAmt, BigDecimal coverageLimit) {

        Map<String, Object> result = new LinkedHashMap<>();

        jdbc.execute((Connection con) -> {
            boolean prevAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            Savepoint sp1 = null;
            Savepoint sp2 = null;
            try {
                // 1. INSERT RENTAL
                String insertRental =
                    "INSERT INTO RENTALS(product_id, renter_id, start_date, end_date, status) " +
                    "VALUES (?, ?, ?, ?, 'PENDING')";
                PreparedStatement ps1 = con.prepareStatement(
                    insertRental, java.sql.Statement.RETURN_GENERATED_KEYS);
                ps1.setInt(1, productId);
                ps1.setInt(2, renterId);
                ps1.setDate(3, Date.valueOf(startDate));
                ps1.setDate(4, Date.valueOf(endDate));
                ps1.executeUpdate();

                int rentalId = -1;
                var keys = ps1.getGeneratedKeys();
                if (keys.next()) rentalId = keys.getInt(1);
                ps1.close();

                result.put("rentalId", rentalId);
                result.put("step1", "✅ Rental inserted (rental_id=" + rentalId + ")");

                // SAVEPOINT sp1 (after rental, before insurance)
                sp1 = con.setSavepoint("sp1");
                result.put("savepoint1", "📍 SAVEPOINT sp1 set");

                if (withInsurance && rentalId > 0) {
                    // 2. INSERT INSURANCE
                    String insertIns =
                        "INSERT INTO INSURANCE(rental_id, premium_amt, coverage_limit) VALUES(?,?,?)";
                    PreparedStatement ps2 = con.prepareStatement(insertIns);
                    ps2.setInt(1, rentalId);
                    ps2.setBigDecimal(2, premiumAmt != null ? premiumAmt : BigDecimal.valueOf(50));
                    ps2.setBigDecimal(3, coverageLimit != null ? coverageLimit : BigDecimal.valueOf(20000));
                    ps2.executeUpdate();
                    ps2.close();

                    sp2 = con.setSavepoint("sp2");
                    result.put("step2", "✅ Insurance inserted");
                    result.put("savepoint2", "📍 SAVEPOINT sp2 set");
                } else {
                    result.put("step2", "⏭ Insurance skipped");
                }

                // COMMIT
                con.commit();
                result.put("outcome", "✅ TRANSACTION COMMITTED successfully");
                result.put("success", true);

            } catch (Exception e) {
                // ROLLBACK
                try { con.rollback(); } catch (Exception ignored) {}
                result.put("outcome", "❌ ROLLBACK triggered: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prevAutoCommit);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // TRANSACTION 2: Simulate Insert → ROLLBACK TO SAVEPOINT A
    // Demonstrates: partial rollback (section 5.3.1 Tx2)
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> simulateRollbackTransaction(int socId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Simulates Transaction 2 from report: insert 3 users, rollback to A (removes B and C)");

        jdbc.execute((Connection con) -> {
            boolean prev = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                // Insert User A
                String insertSql = "INSERT INTO USERS(soc_id, username, email, flat_no, password) VALUES(?,?,?,?,?)";
                PreparedStatement psA = con.prepareStatement(insertSql);
                psA.setInt(1, socId);
                psA.setString(2, "TxDemo_UserA");
                psA.setString(3, "txdemo_a_" + System.currentTimeMillis() + "@test.com");
                psA.setString(4, "T-100");
                psA.setString(5, "demo");
                psA.executeUpdate(); psA.close();
                result.put("step1", "✅ INSERT User A");

                // SAVEPOINT A
                Savepoint spA = con.setSavepoint("A");
                result.put("savepoint_A", "📍 SAVEPOINT A created");

                // Insert User B
                PreparedStatement psB = con.prepareStatement(insertSql);
                psB.setInt(1, socId);
                psB.setString(2, "TxDemo_UserB");
                psB.setString(3, "txdemo_b_" + System.currentTimeMillis() + "@test.com");
                psB.setString(4, "T-101");
                psB.setString(5, "demo");
                psB.executeUpdate(); psB.close();
                result.put("step2", "✅ INSERT User B");

                // SAVEPOINT B
                Savepoint spB = con.setSavepoint("B");
                result.put("savepoint_B", "📍 SAVEPOINT B created");

                // Insert User C
                PreparedStatement psC = con.prepareStatement(insertSql);
                psC.setInt(1, socId);
                psC.setString(2, "TxDemo_UserC");
                psC.setString(3, "txdemo_c_" + System.currentTimeMillis() + "@test.com");
                psC.setString(4, "T-102");
                psC.setString(5, "demo");
                psC.executeUpdate(); psC.close();
                result.put("step3", "✅ INSERT User C");

                // ROLLBACK TO A  →  removes B and C
                con.rollback(spA);
                result.put("rollback", "↩️ ROLLBACK TO SAVEPOINT A — User B and C removed");

                // COMMIT (only A remains)
                con.commit();
                result.put("outcome", "✅ COMMITTED — Only User A persisted");
                result.put("success", true);

            } catch (Exception e) {
                try { con.rollback(); } catch (Exception ignored) {}
                result.put("outcome", "❌ ERROR + FULL ROLLBACK: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prev);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // TRANSACTION 3: Update Price + Rollback (section 5.3.1 Tx3)
    // START TRANSACTION;
    //   UPDATE PRODUCTS SET price_per_day = 1500 WHERE product_id = 1;
    //   SAVEPOINT sp1;
    //   UPDATE PRODUCTS SET price_per_day = 2000 WHERE product_id = 2;
    //   ROLLBACK TO sp1;  ← undoes 2nd update only
    // COMMIT;
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> simulateUpdateRollback(int productId1, int productId2,
                                                       BigDecimal price1, BigDecimal price2) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Transaction 3 from report: update 2 products, rollback 2nd update");

        jdbc.execute((Connection con) -> {
            boolean prev = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                // UPDATE product 1 (this will persist)
                PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?");
                ps1.setBigDecimal(1, price1);
                ps1.setInt(2, productId1);
                ps1.executeUpdate(); ps1.close();
                result.put("step1", "✅ Updated product " + productId1 + " price → " + price1);

                // SAVEPOINT sp1
                Savepoint sp1 = con.setSavepoint("sp1");
                result.put("savepoint", "📍 SAVEPOINT sp1 created");

                // UPDATE product 2 (this will be rolled back)
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?");
                ps2.setBigDecimal(1, price2);
                ps2.setInt(2, productId2);
                ps2.executeUpdate(); ps2.close();
                result.put("step2", "✅ Updated product " + productId2 + " price → " + price2);

                // ROLLBACK TO sp1 — only 2nd update undone
                con.rollback(sp1);
                result.put("rollback", "↩️ ROLLBACK TO sp1 — product " + productId2 + " update undone");

                // COMMIT
                con.commit();
                result.put("outcome", "✅ COMMITTED — product " + productId1 + " price change saved");
                result.put("success", true);

            } catch (Exception e) {
                try { con.rollback(); } catch (Exception ignored) {}
                result.put("outcome", "❌ FULL ROLLBACK: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prev);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // TRANSACTION 4: Delete + Rollback (section 5.3.1 Tx4)
    // START TRANSACTION;
    //   DELETE FROM REVIEWS WHERE review_id = ?;
    //   ROLLBACK;  ← undoes the delete
    // COMMIT;
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> simulateDeleteRollback(int reviewId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Transaction 4 from report: delete a review then ROLLBACK");

        jdbc.execute((Connection con) -> {
            boolean prev = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM REVIEWS WHERE review_id = ?");
                ps.setInt(1, reviewId);
                int rows = ps.executeUpdate(); ps.close();
                result.put("step1", "🗑️ DELETE executed — " + rows + " row(s) affected");

                // ROLLBACK — undo the delete
                con.rollback();
                result.put("rollback", "↩️ ROLLBACK — delete undone, review restored");
                result.put("outcome", "✅ Review " + reviewId + " still exists after rollback");
                result.put("success", true);

            } catch (Exception e) {
                try { con.rollback(); } catch (Exception ignored) {}
                result.put("outcome", "❌ ERROR: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prev);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // CONCURRENCY: FOR UPDATE Row-Level Lock (section 5.3.2.1a)
    // START TRANSACTION;
    //   SELECT * FROM PRODUCTS WHERE product_id = ? FOR UPDATE;
    //   UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?;
    // COMMIT;
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> simulateForUpdateLock(int productId, BigDecimal newPrice) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Concurrency: FOR UPDATE row-level lock (section 5.3.2.1a)");

        jdbc.execute((Connection con) -> {
            boolean prev = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                // SELECT FOR UPDATE — acquires row-level exclusive lock
                PreparedStatement ps1 = con.prepareStatement(
                    "SELECT * FROM PRODUCTS WHERE product_id = ? FOR UPDATE");
                ps1.setInt(1, productId);
                var rs = ps1.executeQuery();
                BigDecimal oldPrice = null;
                if (rs.next()) oldPrice = rs.getBigDecimal("price_per_day");
                rs.close(); ps1.close();
                result.put("step1", "🔒 SELECT FOR UPDATE — row locked (old price: " + oldPrice + ")");

                // UPDATE while holding lock
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?");
                ps2.setBigDecimal(1, newPrice);
                ps2.setInt(2, productId);
                ps2.executeUpdate(); ps2.close();
                result.put("step2", "✅ UPDATE executed (new price: " + newPrice + ")");

                con.commit();
                result.put("outcome", "✅ COMMITTED — lock released");
                result.put("success", true);

            } catch (Exception e) {
                try { con.rollback(); } catch (Exception ignored) {}
                result.put("outcome", "❌ ERROR + ROLLBACK: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prev);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // CONCURRENCY: LOCK TABLES WRITE (section 5.3.2.1b)
    // LOCK TABLES PRODUCTS WRITE;
    //   UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?;
    // UNLOCK TABLES;
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> simulateLockTablesWrite(int productId, BigDecimal newPrice) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Concurrency: LOCK TABLES WRITE exclusive lock (section 5.3.2.1b)");

        jdbc.execute((Connection con) -> {
            boolean prev = con.getAutoCommit();
            con.setAutoCommit(true);
            try {
                con.createStatement().execute("LOCK TABLES PRODUCTS WRITE");
                result.put("step1", "🔒 LOCK TABLES PRODUCTS WRITE acquired");

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?");
                ps.setBigDecimal(1, newPrice);
                ps.setInt(2, productId);
                ps.executeUpdate(); ps.close();
                result.put("step2", "✅ UPDATE executed under exclusive lock");

                con.createStatement().execute("UNLOCK TABLES");
                result.put("outcome", "🔓 UNLOCK TABLES — lock released");
                result.put("success", true);

            } catch (Exception e) {
                try { con.createStatement().execute("UNLOCK TABLES"); } catch (Exception ignored) {}
                result.put("outcome", "❌ ERROR: " + e.getMessage());
                result.put("success", false);
            } finally {
                con.setAutoCommit(prev);
            }
            return null;
        });
        return result;
    }

    // ────────────────────────────────────────────────────────────
    // TRIGGER TEST: Invalid Dates (section 3.8)
    // INSERT INTO RENTALS with end_date <= start_date
    // Should throw: SQLSTATE 45000 "Invalid rental dates"
    // ────────────────────────────────────────────────────────────
    public Map<String, Object> testTriggerInvalidDates(int productId, int renterId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", "Tests trigger check_valid_dates: end_date <= start_date");
        result.put("sql", "INSERT INTO RENTALS VALUES (..., '2026-05-10', '2026-05-08')");

        try {
            // end_date BEFORE start_date — trigger fires SIGNAL SQLSTATE '45000'
            jdbc.update(
                "INSERT INTO RENTALS(product_id, renter_id, start_date, end_date) VALUES(?,?,?,?)",
                productId, renterId,
                Date.valueOf(LocalDate.of(2026, 5, 10)),
                Date.valueOf(LocalDate.of(2026, 5, 8)));

            result.put("outcome", "⚠️ Insert succeeded — trigger did NOT fire (unexpected)");
            result.put("success", false);
        } catch (Exception e) {
            result.put("outcome", "✅ TRIGGER FIRED — insert rejected as expected");
            result.put("triggerMessage", e.getMessage());
            result.put("success", true);
        }
        return result;
    }

    // Simple getters delegating to DAO
    public List<Rental> findAll()                        { return rentalDAO.findAll(); }
    public List<Rental> findByRenter(int id)             { return rentalDAO.findByRenter(id); }
    public List<Map<String,Object>> findDetailed()       { return rentalDAO.findDetailed(); }
    public List<Map<String,Object>> getUserActivity()    { return rentalDAO.getUserActivity(); }
    public int updateStatus(int id, String s)            { return rentalDAO.updateStatus(id, s); }
}
