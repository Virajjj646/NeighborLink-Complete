package com.neighborlink.dao;

import com.neighborlink.model.Rental;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * RentalDAO — Pure JDBC.
 *
 * Report queries covered:
 *  - INSERT INTO RENTALS with TRANSACTION + SAVEPOINT   (section 5.3.1)
 *  - TRIGGER test: invalid dates (section 3.8)
 *  - FOR UPDATE row-level locking (section 5.3.2.1)
 *  - LOCK TABLES exclusive lock (section 5.3.2.1b)
 *  - UPDATE RENTALS status
 *  - DELETE + ROLLBACK (transaction 4)
 */
@Repository
public class RentalDAO {

    private final JdbcTemplate jdbc;

    public RentalDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Rental> rentalMapper = (rs, rn) -> {
        Rental r = new Rental();
        r.setRentalId(rs.getInt("rental_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setRenterId(rs.getInt("renter_id"));
        r.setStartDate(rs.getDate("start_date").toLocalDate());
        r.setEndDate(rs.getDate("end_date").toLocalDate());
        r.setStatus(rs.getString("status"));
        return r;
    };

    // ── DML: Simple insert (trigger fires here) ────────────────
    // SQL: INSERT INTO RENTALS(product_id, renter_id, start_date, end_date, status)
    //      VALUES (?, ?, ?, ?, 'PENDING')
    // API: POST /api/rentals
    public int create(Rental rental) {
        String sql = "INSERT INTO RENTALS(product_id, renter_id, start_date, end_date, status) " +
                     "VALUES (?, ?, ?, ?, 'PENDING')";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, rental.getProductId());
            ps.setInt(2, rental.getRenterId());
            ps.setDate(3, Date.valueOf(rental.getStartDate()));
            ps.setDate(4, Date.valueOf(rental.getEndDate()));
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    // ── Get all rentals ────────────────────────────────────────
    public List<Rental> findAll() {
        return jdbc.query("SELECT * FROM RENTALS ORDER BY rental_id DESC", rentalMapper);
    }

    // ── Get by ID ──────────────────────────────────────────────
    public Optional<Rental> findById(int id) {
        List<Rental> list = jdbc.query(
            "SELECT * FROM RENTALS WHERE rental_id = ?", rentalMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // ── Get by renter ──────────────────────────────────────────
    public List<Rental> findByRenter(int renterId) {
        return jdbc.query(
            "SELECT * FROM RENTALS WHERE renter_id = ? ORDER BY rental_id DESC",
            rentalMapper, renterId);
    }

    // ── Get by product ─────────────────────────────────────────
    public List<Rental> findByProduct(int productId) {
        return jdbc.query(
            "SELECT * FROM RENTALS WHERE product_id = ?", rentalMapper, productId);
    }

    // ── Multi JOIN: full rental details (section 3.4.2 MULTI JOIN) ─
    // SQL: SELECT r.rental_id, renter.username AS renter, owner.username AS owner,
    //             p.title, r.status FROM RENTALS r
    //      JOIN USERS renter ON r.renter_id  = renter.user_id
    //      JOIN PRODUCTS p   ON r.product_id = p.product_id
    //      JOIN USERS owner  ON p.owner_id   = owner.user_id
    // API: GET /api/rentals/detailed
    public List<Map<String,Object>> findDetailed() {
        String sql = "SELECT r.rental_id, renter.username AS renter, " +
                     "       owner.username AS owner, p.title, " +
                     "       r.start_date, r.end_date, r.status " +
                     "FROM RENTALS r " +
                     "JOIN USERS renter ON r.renter_id  = renter.user_id " +
                     "JOIN PRODUCTS p   ON r.product_id = p.product_id " +
                     "JOIN USERS owner  ON p.owner_id   = owner.user_id " +
                     "ORDER BY r.rental_id DESC";
        return jdbc.queryForList(sql);
    }

    // ── DML: Update status ─────────────────────────────────────
    // SQL: UPDATE RENTALS SET status = ? WHERE rental_id = ?
    // API: PUT /api/rentals/{id}/status
    public int updateStatus(int rentalId, String status) {
        return jdbc.update(
            "UPDATE RENTALS SET status = ? WHERE rental_id = ?", status, rentalId);
    }

    // ── DML: Delete rental ─────────────────────────────────────
    public int delete(int rentalId) {
        return jdbc.update("DELETE FROM RENTALS WHERE rental_id = ?", rentalId);
    }

    // ── VIEW: user_activity (section 3.6) ─────────────────────
    // SQL: SELECT * FROM user_activity
    // API: GET /api/rentals/user-activity
    public List<Map<String,Object>> getUserActivity() {
        return jdbc.queryForList("SELECT * FROM user_activity ORDER BY total_rentals DESC");
    }

    // ── Raw select for concurrency demo ───────────────────────
    public List<Map<String,Object>> findByProductForUpdate(int productId) {
        return jdbc.queryForList(
            "SELECT * FROM RENTALS WHERE product_id = ?", productId);
    }
}
