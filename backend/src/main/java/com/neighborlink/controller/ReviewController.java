package com.neighborlink.controller;

import com.neighborlink.model.Review;
import com.neighborlink.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * ReviewController
 *
 * POST   /api/reviews              → INSERT INTO REVIEWS (CHECK constraint on rating 1-5)
 *                                    TRIGGER update_trust_score_after_review fires here
 * GET    /api/reviews              → SELECT * FROM REVIEWS
 * GET    /api/reviews/detailed     → JOIN with USERS + PRODUCTS
 * GET    /api/reviews/trust/{id}   → AVG(rating) aggregation for owner trust score
 * DELETE /api/reviews/{id}         → DELETE FROM REVIEWS
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST /api/reviews
    // SQL: INSERT INTO REVIEWS(rental_id, reviewer_id, rating, comment) VALUES(?,?,?,?)
    // CHECK constraint: rating BETWEEN 1 AND 5
    // TRIGGER: update_trust_score_after_review fires AFTER INSERT
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.create(review));
    }

    // GET /api/reviews
    // SQL: SELECT * FROM REVIEWS ORDER BY review_date DESC
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    // GET /api/reviews/detailed
    // SQL: JOIN REVIEWS with USERS (reviewer) and PRODUCTS (via RENTALS)
    @GetMapping("/detailed")
    public ResponseEntity<?> getDetailed() {
        return ResponseEntity.ok(reviewService.getAllDetailed());
    }

    // GET /api/reviews/trust/{ownerId}
    // SQL: SELECT AVG(rv.rating), COUNT(rv.review_id)
    //      FROM REVIEWS rv JOIN RENTALS r ... JOIN PRODUCTS p ...
    //      WHERE p.owner_id = ?
    // Demonstrates: Trust Score aggregation
    @GetMapping("/trust/{ownerId}")
    public ResponseEntity<?> getTrustStats(@PathVariable int ownerId) {
        return ResponseEntity.ok(Map.of(
            "ownerId", ownerId,
            "trustStats", reviewService.getTrustStats(ownerId),
            "sql", "SELECT AVG(rv.rating) AS avg_rating, COUNT(rv.review_id) AS total_reviews " +
                   "FROM REVIEWS rv JOIN RENTALS r ON rv.rental_id = r.rental_id " +
                   "JOIN PRODUCTS p ON r.product_id = p.product_id WHERE p.owner_id = " + ownerId
        ));
    }

    // DELETE /api/reviews/{id}
    // SQL: DELETE FROM REVIEWS WHERE review_id = ?
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        return ResponseEntity.ok(reviewService.delete(id));
    }
}
