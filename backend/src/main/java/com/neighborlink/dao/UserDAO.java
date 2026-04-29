package com.neighborlink.dao;

import com.neighborlink.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * UserDAO — Pure JDBC (no JPA/Hibernate).
 *
 * Report queries covered:
 *  - INSERT INTO USERS         (DML)
 *  - SELECT COUNT(*) FROM USERS (aggregate)
 *  - WHERE user_id IN (SELECT renter_id FROM RENTALS)  (subquery)
 *  - WHERE user_id NOT IN (...)                         (set NOT IN)
 *  - UNION of usernames and product titles              (set UNION)
 */
@Repository
public class UserDAO {

    private final JdbcTemplate jdbc;

    public UserDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    // RowMapper
    private final RowMapper<User> userMapper = (rs, rn) -> {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setSocId(rs.getInt("soc_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setFlatNo(rs.getString("flat_no"));
        u.setTrustScore(rs.getBigDecimal("trust_score"));
        return u;
    };

    // ── DML: Register user ─────────────────────────────────────
    // SQL: INSERT INTO USERS(soc_id, username, email, flat_no, password)
    //      VALUES (?, ?, ?, ?, ?)
    // API: POST /api/users/register
    public int register(User user) {
        String sql = "INSERT INTO USERS(soc_id, username, email, flat_no, password) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, user.getSocId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFlatNo());
            ps.setString(5, user.getPassword());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    // ── Login ──────────────────────────────────────────────────
    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM USERS WHERE email = ? AND password = ?";
        List<User> users = jdbc.query(sql, userMapper, email, password);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    // ── Get by ID ──────────────────────────────────────────────
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        List<User> list = jdbc.query(sql, userMapper, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // ── Get all ────────────────────────────────────────────────
    public List<User> findAll() {
        return jdbc.query("SELECT * FROM USERS", userMapper);
    }

    // ── Aggregate: COUNT (section 3.1.1) ───────────────────────
    // SQL: SELECT COUNT(*) FROM USERS;
    // API: GET /api/users/count
    public int countAll() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
    }

    // ── Subquery/Set IN: users who have rented (section 3.3.2) ─
    // SQL: SELECT username FROM USERS WHERE user_id IN (SELECT renter_id FROM RENTALS)
    // API: GET /api/users/renters
    public List<String> findUsernamesWhoRented() {
        String sql = "SELECT username FROM USERS WHERE user_id IN " +
                     "(SELECT renter_id FROM RENTALS)";
        return jdbc.queryForList(sql, String.class);
    }

    // ── Set NOT IN: users who never rented (section 3.3.3) ────
    // SQL: SELECT username FROM USERS WHERE user_id NOT IN (SELECT renter_id FROM RENTALS)
    // API: GET /api/users/non-renters
    public List<String> findUsernamesWhoNeverRented() {
        String sql = "SELECT username FROM USERS WHERE user_id NOT IN " +
                     "(SELECT renter_id FROM RENTALS)";
        return jdbc.queryForList(sql, String.class);
    }

    // ── Set UNION: usernames + product titles (section 3.3.1) ─
    // SQL: SELECT username FROM USERS UNION SELECT title FROM PRODUCTS
    // API: GET /api/users/union-products
    public List<String> unionUsernamesAndProducts() {
        String sql = "SELECT username AS name FROM USERS UNION SELECT title FROM PRODUCTS";
        return jdbc.queryForList(sql, String.class);
    }

    // ── Trust score aggregation ────────────────────────────────
    // SQL: SELECT trust_score FROM USERS WHERE user_id = ?
    // API: GET /api/users/{id}/trust-score
    public Map<String,Object> getTrustScoreInfo(int userId) {
        String sql = "SELECT u.username, u.trust_score, " +
                     "COUNT(rv.review_id) AS total_reviews, " +
                     "AVG(rv.rating) AS avg_rating " +
                     "FROM USERS u " +
                     "LEFT JOIN PRODUCTS p  ON p.owner_id  = u.user_id " +
                     "LEFT JOIN RENTALS  r  ON r.product_id = p.product_id " +
                     "LEFT JOIN REVIEWS  rv ON rv.rental_id = r.rental_id " +
                     "WHERE u.user_id = ? " +
                     "GROUP BY u.user_id, u.username, u.trust_score";
        List<Map<String,Object>> rows = jdbc.queryForList(sql, userId);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    // ── Function call: total_rentals_by_user (section 3.7) ────
    // SQL: SELECT total_rentals_by_user(?)
    // API: GET /api/users/{id}/total-rentals
    public int callTotalRentalsByUser(int userId) {
        return jdbc.queryForObject(
            "SELECT total_rentals_by_user(?)", Integer.class, userId);
    }
}
