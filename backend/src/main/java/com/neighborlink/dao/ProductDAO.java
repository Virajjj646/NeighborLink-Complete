package com.neighborlink.dao;

import com.neighborlink.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * ProductDAO — Pure JDBC.
 *
 * Report queries covered:
 *  - INSERT INTO PRODUCTS          (DML)
 *  - SELECT MAX(price_per_day)     (aggregate MAX - section 3.1.2)
 *  - SELECT AVG(price_per_day)     (aggregate AVG - section 3.1.3)
 *  - INNER JOIN with USERS         (section 3.4.1)
 *  - LEFT JOIN with USERS          (section 3.4.2)
 *  - WHERE price_per_day > (SELECT AVG...) (subquery - section 3.5)
 */
@Repository
public class ProductDAO {

    private final JdbcTemplate jdbc;

    public ProductDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Product> productMapper = (rs, rn) -> {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setOwnerId(rs.getInt("owner_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPricePerDay(rs.getBigDecimal("price_per_day"));
        p.setItemValue(rs.getBigDecimal("item_value"));
        return p;
    };

    // ── DML: Create listing ────────────────────────────────────
    // SQL: INSERT INTO PRODUCTS(owner_id, title, description, price_per_day, item_value)
    //      VALUES (?, ?, ?, ?, ?)
    // API: POST /api/products
    public int create(Product p) {
        String sql = "INSERT INTO PRODUCTS(owner_id, title, description, price_per_day, item_value) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, p.getOwnerId());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPricePerDay());
            ps.setBigDecimal(5, p.getItemValue());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    // ── Get all products ───────────────────────────────────────
    public List<Product> findAll() {
        return jdbc.query("SELECT * FROM PRODUCTS", productMapper);
    }

    // ── Get by ID ──────────────────────────────────────────────
    public Optional<Product> findById(int id) {
        List<Product> list = jdbc.query(
            "SELECT * FROM PRODUCTS WHERE product_id = ?", productMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // ── Get by owner ───────────────────────────────────────────
    public List<Product> findByOwner(int ownerId) {
        return jdbc.query(
            "SELECT * FROM PRODUCTS WHERE owner_id = ?", productMapper, ownerId);
    }

    // ── Aggregate MAX (section 3.1.2) ──────────────────────────
    // SQL: SELECT MAX(price_per_day) FROM PRODUCTS
    // API: GET /api/products/stats/max-price
    public Map<String,Object> getStats() {
        String sql = "SELECT MAX(price_per_day) AS max_price, " +
                     "       AVG(price_per_day) AS avg_price, " +
                     "       MIN(price_per_day) AS min_price, " +
                     "       COUNT(*) AS total_products " +
                     "FROM PRODUCTS";
        return jdbc.queryForMap(sql);
    }

    // ── INNER JOIN: user + product (section 3.4.1) ─────────────
    // SQL: SELECT u.username, p.title FROM USERS u
    //      INNER JOIN PRODUCTS p ON u.user_id = p.owner_id
    // API: GET /api/products/with-owners
    public List<Map<String,Object>> findWithOwners() {
        String sql = "SELECT u.username, p.title, p.price_per_day, p.product_id " +
                     "FROM USERS u " +
                     "INNER JOIN PRODUCTS p ON u.user_id = p.owner_id";
        return jdbc.queryForList(sql);
    }

    // ── LEFT JOIN: all users + their products (section 3.4.2) ──
    // SQL: SELECT u.username, p.title FROM USERS u LEFT JOIN PRODUCTS p ON u.user_id = p.owner_id
    // API: GET /api/products/left-join
    public List<Map<String,Object>> findWithOwnersLeftJoin() {
        String sql = "SELECT u.username, p.title FROM USERS u " +
                     "LEFT JOIN PRODUCTS p ON u.user_id = p.owner_id";
        return jdbc.queryForList(sql);
    }

    // ── Subquery: above-average price (section 3.5) ────────────
    // SQL: SELECT title FROM PRODUCTS WHERE price_per_day > (SELECT AVG(price_per_day) FROM PRODUCTS)
    // API: GET /api/products/above-average
    public List<String> findAboveAveragePrice() {
        String sql = "SELECT title FROM PRODUCTS " +
                     "WHERE price_per_day > (SELECT AVG(price_per_day) FROM PRODUCTS)";
        return jdbc.queryForList(sql, String.class);
    }

    // ── MULTI JOIN: rentals with renter, owner, product (section 3.4.2 multi join) ──
    // SQL: SELECT r.rental_id, renter.username AS renter, owner.username AS owner, p.title, r.status
    //      FROM RENTALS r
    //      JOIN USERS renter ON r.renter_id = renter.user_id
    //      JOIN PRODUCTS p   ON r.product_id = p.product_id
    //      JOIN USERS owner  ON p.owner_id   = owner.user_id
    // API: GET /api/products/rentals-multi-join
    public List<Map<String,Object>> multiJoinRentals() {
        String sql = "SELECT r.rental_id, renter.username AS renter, " +
                     "       owner.username AS owner, p.title, r.status " +
                     "FROM RENTALS r " +
                     "JOIN USERS renter ON r.renter_id  = renter.user_id " +
                     "JOIN PRODUCTS p   ON r.product_id = p.product_id " +
                     "JOIN USERS owner  ON p.owner_id   = owner.user_id";
        return jdbc.queryForList(sql);
    }

    // ── COUNT per product (section 3.1.1 GROUP BY) ─────────────
    // SQL: SELECT product_id, COUNT(*) AS total_rentals FROM RENTALS GROUP BY product_id
    // API: GET /api/products/rental-counts
    public List<Map<String,Object>> rentalCountPerProduct() {
        String sql = "SELECT product_id, COUNT(*) AS total_rentals " +
                     "FROM RENTALS GROUP BY product_id";
        return jdbc.queryForList(sql);
    }

    // ── DML: Update product price ──────────────────────────────
    // SQL: UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?
    // API: PUT /api/products/{id}/price
    public int updatePrice(int productId, java.math.BigDecimal newPrice) {
        return jdbc.update(
            "UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?",
            newPrice, productId);
    }

    // ── DML: Delete product ────────────────────────────────────
    // SQL: DELETE FROM PRODUCTS WHERE product_id = ?
    // API: DELETE /api/products/{id}
    public int delete(int productId) {
        return jdbc.update("DELETE FROM PRODUCTS WHERE product_id = ?", productId);
    }

    // ── Availability check (date logic) ───────────────────────
    // SQL: SELECT * FROM RENTALS WHERE product_id = ?
    //      AND status NOT IN ('CANCELLED')
    //      AND (start_date <= ? AND end_date >= ?)
    // API: GET /api/products/{id}/availability?start=...&end=...
    public boolean isAvailable(int productId, java.time.LocalDate start, java.time.LocalDate end) {
        String sql = "SELECT COUNT(*) FROM RENTALS WHERE product_id = ? " +
                     "AND status NOT IN ('CANCELLED') " +
                     "AND (start_date <= ? AND end_date >= ?)";
        Integer count = jdbc.queryForObject(sql, Integer.class, productId,
                java.sql.Date.valueOf(end), java.sql.Date.valueOf(start));
        return count != null && count == 0;
    }
}
