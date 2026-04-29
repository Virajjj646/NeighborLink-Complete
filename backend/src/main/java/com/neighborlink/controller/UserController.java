package com.neighborlink.controller;

import com.neighborlink.dao.SocietyDAO;
import com.neighborlink.model.User;
import com.neighborlink.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * UserController
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  MAPPING: Report Queries → Backend Code → API Endpoint             │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: INSERT INTO USERS(...)                                         │
 * │ Method: userDAO.register(user)                                      │
 * │ API: POST /api/users/register                                       │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: SELECT COUNT(*) FROM USERS                                     │
 * │ Method: userDAO.countAll()                                          │
 * │ API: GET /api/users/count                                           │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: SELECT username FROM USERS WHERE user_id IN                    │
 * │      (SELECT renter_id FROM RENTALS)                                │
 * │ Method: userDAO.findUsernamesWhoRented()                            │
 * │ API: GET /api/users/renters                                         │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: SELECT username FROM USERS WHERE user_id NOT IN                │
 * │      (SELECT renter_id FROM RENTALS)                                │
 * │ Method: userDAO.findUsernamesWhoNeverRented()                       │
 * │ API: GET /api/users/non-renters                                     │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: SELECT username FROM USERS UNION SELECT title FROM PRODUCTS    │
 * │ Method: userDAO.unionUsernamesAndProducts()                         │
 * │ API: GET /api/users/union                                           │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: SELECT total_rentals_by_user(uid)  [FUNCTION call]             │
 * │ Method: userDAO.callTotalRentalsByUser(id)                          │
 * │ API: GET /api/users/{id}/total-rentals                              │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ SQL: AVG(rv.rating) ... JOIN chain for trust score                  │
 * │ Method: userDAO.getTrustScoreInfo(id)                               │
 * │ API: GET /api/users/{id}/trust-score                                │
 * └─────────────────────────────────────────────────────────────────────┘
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SocietyDAO  societyDAO;

    public UserController(UserService userService, SocietyDAO societyDAO) {
        this.userService = userService;
        this.societyDAO  = societyDAO;
    }

    // ── POST /api/users/register ──────────────────────────────
    // SQL: INSERT INTO USERS(soc_id, username, email, flat_no, password) VALUES(?,?,?,?,?)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    // ── POST /api/users/login ─────────────────────────────────
    // SQL: SELECT * FROM USERS WHERE email = ? AND password = ?
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        return ResponseEntity.ok(
            userService.login(creds.get("email"), creds.get("password")));
    }

    // ── GET /api/users ────────────────────────────────────────
    // SQL: SELECT * FROM USERS
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // ── GET /api/users/{id} ───────────────────────────────────
    // SQL: SELECT * FROM USERS WHERE user_id = ?
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // ── GET /api/users/count ──────────────────────────────────
    // SQL: SELECT COUNT(*) FROM USERS   [Aggregate - Section 3.1.1]
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(Map.of(
            "count", userService.countAll(),
            "sql",   "SELECT COUNT(*) FROM USERS"
        ));
    }

    // ── GET /api/users/renters ────────────────────────────────
    // SQL: SELECT username FROM USERS
    //      WHERE user_id IN (SELECT renter_id FROM RENTALS)
    //      [Set IN / Subquery - Section 3.3.2]
    @GetMapping("/renters")
    public ResponseEntity<?> renters() {
        return ResponseEntity.ok(Map.of(
            "renters", userService.getRenters(),
            "sql", "SELECT username FROM USERS WHERE user_id IN (SELECT renter_id FROM RENTALS)"
        ));
    }

    // ── GET /api/users/non-renters ────────────────────────────
    // SQL: SELECT username FROM USERS
    //      WHERE user_id NOT IN (SELECT renter_id FROM RENTALS)
    //      [Set NOT IN / EXCEPT - Section 3.3.3]
    @GetMapping("/non-renters")
    public ResponseEntity<?> nonRenters() {
        return ResponseEntity.ok(Map.of(
            "nonRenters", userService.getNonRenters(),
            "sql", "SELECT username FROM USERS WHERE user_id NOT IN (SELECT renter_id FROM RENTALS)"
        ));
    }

    // ── GET /api/users/union ──────────────────────────────────
    // SQL: SELECT username FROM USERS UNION SELECT title FROM PRODUCTS
    //      [Set UNION - Section 3.3.1]
    @GetMapping("/union")
    public ResponseEntity<?> union() {
        return ResponseEntity.ok(Map.of(
            "unionResult", userService.getUnionUsernamesProducts(),
            "sql", "SELECT username FROM USERS UNION SELECT title FROM PRODUCTS"
        ));
    }

    // ── GET /api/users/{id}/trust-score ──────────────────────
    // SQL: SELECT AVG(rv.rating), COUNT(rv.review_id)
    //      FROM USERS u
    //      LEFT JOIN PRODUCTS p  ON p.owner_id  = u.user_id
    //      LEFT JOIN RENTALS  r  ON r.product_id = p.product_id
    //      LEFT JOIN REVIEWS  rv ON rv.rental_id = r.rental_id
    //      WHERE u.user_id = ? GROUP BY u.user_id
    //      [Aggregation + JOIN chain for Trust Score]
    @GetMapping("/{id}/trust-score")
    public ResponseEntity<?> trustScore(@PathVariable int id) {
        return ResponseEntity.ok(userService.getTrustInfo(id));
    }

    // ── GET /api/users/{id}/total-rentals ────────────────────
    // SQL: SELECT total_rentals_by_user(?)
    //      [FUNCTION call - Section 3.7]
    @GetMapping("/{id}/total-rentals")
    public ResponseEntity<?> totalRentals(@PathVariable int id) {
        return ResponseEntity.ok(Map.of(
            "userId",       id,
            "totalRentals", userService.callTotalRentalsFunction(id),
            "sql",          "SELECT total_rentals_by_user(" + id + ")"
        ));
    }

    // ── GET /api/users/societies ──────────────────────────────
    // SQL: SELECT * FROM SOCIETY
    // Used by frontend register form dropdown
    @GetMapping("/societies")
    public ResponseEntity<?> getSocieties() {
        return ResponseEntity.ok(societyDAO.findAll());
    }
}
