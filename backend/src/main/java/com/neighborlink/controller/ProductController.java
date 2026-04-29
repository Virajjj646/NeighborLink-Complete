package com.neighborlink.controller;

import com.neighborlink.model.Product;
import com.neighborlink.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * ProductController
 *
 * Endpoint → SQL Mapping:
 * POST   /api/products                    → INSERT INTO PRODUCTS (DML)
 * GET    /api/products                    → SELECT * FROM PRODUCTS
 * GET    /api/products/{id}               → SELECT WHERE product_id = ?
 * GET    /api/products/stats              → MAX, AVG, MIN, COUNT (Aggregates 3.1.2, 3.1.3)
 * GET    /api/products/with-owners        → INNER JOIN USERS (3.4.1)
 * GET    /api/products/left-join          → LEFT JOIN USERS (3.4.2)
 * GET    /api/products/above-average      → WHERE price > (SELECT AVG...) (Subquery 3.5)
 * GET    /api/products/multi-join         → Multi-table JOIN (3.4.2 multi)
 * GET    /api/products/rental-counts      → GROUP BY product_id COUNT (3.1.1)
 * GET    /api/products/{id}/availability  → Date overlap logic
 * GET    /api/products/owner/{ownerId}    → SELECT WHERE owner_id = ?
 * PUT    /api/products/{id}/price         → UPDATE PRODUCTS SET price_per_day
 * DELETE /api/products/{id}              → DELETE FROM PRODUCTS
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST /api/products
    // SQL: INSERT INTO PRODUCTS(owner_id, title, description, price_per_day, item_value) VALUES(?,?,?,?,?)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.create(product));
    }

    // GET /api/products
    // SQL: SELECT * FROM PRODUCTS
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // GET /api/products/{id}
    // SQL: SELECT * FROM PRODUCTS WHERE product_id = ?
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // GET /api/products/owner/{ownerId}
    // SQL: SELECT * FROM PRODUCTS WHERE owner_id = ?
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getByOwner(@PathVariable int ownerId) {
        return ResponseEntity.ok(productService.getByOwner(ownerId));
    }

    // GET /api/products/stats
    // SQL: SELECT MAX(price_per_day), AVG(price_per_day), MIN(price_per_day), COUNT(*) FROM PRODUCTS
    // Demonstrates: MAX (3.1.2), AVG (3.1.3), COUNT (3.1.1)
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(productService.getStats());
    }

    // GET /api/products/with-owners
    // SQL: SELECT u.username, p.title FROM USERS u INNER JOIN PRODUCTS p ON u.user_id = p.owner_id
    // Demonstrates: INNER JOIN (3.4.1)
    @GetMapping("/with-owners")
    public ResponseEntity<?> getWithOwners() {
        return ResponseEntity.ok(productService.getWithOwners());
    }

    // GET /api/products/left-join
    // SQL: SELECT u.username, p.title FROM USERS u LEFT JOIN PRODUCTS p ON u.user_id = p.owner_id
    // Demonstrates: LEFT JOIN (3.4.2)
    @GetMapping("/left-join")
    public ResponseEntity<?> getLeftJoin() {
        return ResponseEntity.ok(productService.getLeftJoin());
    }

    // GET /api/products/above-average
    // SQL: SELECT title FROM PRODUCTS WHERE price_per_day > (SELECT AVG(price_per_day) FROM PRODUCTS)
    // Demonstrates: Subquery (3.5)
    @GetMapping("/above-average")
    public ResponseEntity<?> getAboveAverage() {
        return ResponseEntity.ok(Map.of(
            "products", productService.getAboveAverage(),
            "sql", "SELECT title FROM PRODUCTS WHERE price_per_day > (SELECT AVG(price_per_day) FROM PRODUCTS)"
        ));
    }

    // GET /api/products/multi-join
    // SQL: SELECT r.rental_id, renter.username, owner.username, p.title, r.status
    //      FROM RENTALS r JOIN USERS renter ... JOIN PRODUCTS p ... JOIN USERS owner ...
    // Demonstrates: Multi-table JOIN (3.4.2 multi)
    @GetMapping("/multi-join")
    public ResponseEntity<?> getMultiJoin() {
        return ResponseEntity.ok(productService.getMultiJoin());
    }

    // GET /api/products/rental-counts
    // SQL: SELECT product_id, COUNT(*) AS total_rentals FROM RENTALS GROUP BY product_id
    // Demonstrates: GROUP BY + COUNT (3.1.1)
    @GetMapping("/rental-counts")
    public ResponseEntity<?> getRentalCounts() {
        return ResponseEntity.ok(productService.getRentalCounts());
    }

    // GET /api/products/{id}/availability?start=2026-05-01&end=2026-05-05
    // SQL: SELECT COUNT(*) FROM RENTALS WHERE product_id=? AND date overlap check
    // Demonstrates: Date logic / availability calendar
    @GetMapping("/{id}/availability")
    public ResponseEntity<?> checkAvailability(
            @PathVariable int id,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(
            productService.checkAvailability(id, LocalDate.parse(start), LocalDate.parse(end)));
    }

    // PUT /api/products/{id}/price
    // SQL: UPDATE PRODUCTS SET price_per_day = ? WHERE product_id = ?
    @PutMapping("/{id}/price")
    public ResponseEntity<?> updatePrice(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {
        BigDecimal price = new BigDecimal(body.get("price").toString());
        return ResponseEntity.ok(productService.updatePrice(id, price));
    }

    // DELETE /api/products/{id}
    // SQL: DELETE FROM PRODUCTS WHERE product_id = ?
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        return ResponseEntity.ok(productService.delete(id));
    }
}
