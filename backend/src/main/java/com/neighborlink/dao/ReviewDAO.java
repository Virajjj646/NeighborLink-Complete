package com.neighborlink.dao;

import com.neighborlink.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class ReviewDAO {

    private final JdbcTemplate jdbc;
    public ReviewDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Review> mapper = (rs, rn) -> {
        Review r = new Review();
        r.setReviewId(rs.getInt("review_id"));
        r.setRentalId(rs.getInt("rental_id"));
        r.setReviewerId(rs.getInt("reviewer_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        if (rs.getTimestamp("review_date") != null)
            r.setReviewDate(rs.getTimestamp("review_date").toLocalDateTime());
        return r;
    };

    // SQL: INSERT INTO REVIEWS(rental_id, reviewer_id, rating, comment) VALUES(?,?,?,?)
    // Trigger update_trust_score_after_review fires here
    // API: POST /api/reviews
    public int create(Review review) {
        String sql = "INSERT INTO REVIEWS(rental_id, reviewer_id, rating, comment) VALUES(?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getRentalId());
            ps.setInt(2, review.getReviewerId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    public List<Review> findAll() {
        return jdbc.query("SELECT * FROM REVIEWS ORDER BY review_date DESC", mapper);
    }

    public List<Map<String,Object>> findAllDetailed() {
        String sql = "SELECT rv.review_id, u.username AS reviewer, " +
                     "       p.title AS product, rv.rating, rv.comment, rv.review_date " +
                     "FROM REVIEWS rv " +
                     "JOIN USERS    u ON rv.reviewer_id = u.user_id " +
                     "JOIN RENTALS  r ON rv.rental_id   = r.rental_id " +
                     "JOIN PRODUCTS p ON r.product_id   = p.product_id " +
                     "ORDER BY rv.review_date DESC";
        return jdbc.queryForList(sql);
    }

    // Trust score aggregation for a user's owned products
    public Map<String,Object> getOwnerTrustStats(int ownerId) {
        String sql = "SELECT AVG(rv.rating) AS avg_rating, COUNT(rv.review_id) AS total_reviews " +
                     "FROM REVIEWS rv " +
                     "JOIN RENTALS  r ON rv.rental_id   = r.rental_id " +
                     "JOIN PRODUCTS p ON r.product_id   = p.product_id " +
                     "WHERE p.owner_id = ?";
        return jdbc.queryForMap(sql, ownerId);
    }

    public int delete(int reviewId) {
        return jdbc.update("DELETE FROM REVIEWS WHERE review_id = ?", reviewId);
    }
}
