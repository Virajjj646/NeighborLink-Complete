package com.neighborlink.controller;

import com.neighborlink.model.Insurance;
import com.neighborlink.service.InsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * InsuranceController
 *
 * POST /api/insurance              → INSERT INTO INSURANCE (One-to-One with Rental)
 * GET  /api/insurance              → SELECT * FROM INSURANCE
 * GET  /api/insurance/rental/{id}  → SELECT WHERE rental_id = ?
 */
@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {

    private final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    // POST /api/insurance
    // SQL: INSERT INTO INSURANCE(rental_id, premium_amt, coverage_limit) VALUES(?,?,?)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Insurance insurance) {
        return ResponseEntity.ok(insuranceService.create(insurance));
    }

    // GET /api/insurance
    // SQL: SELECT * FROM INSURANCE
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(insuranceService.getAll());
    }

    // GET /api/insurance/rental/{id}
    // SQL: SELECT * FROM INSURANCE WHERE rental_id = ?
    @GetMapping("/rental/{id}")
    public ResponseEntity<?> getByRental(@PathVariable int id) {
        return ResponseEntity.ok(
            insuranceService.getByRental(id)
                .orElse(null));
    }
}
