package com.neighborlink.controller;

import com.neighborlink.dao.SocietyDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/societies")
public class SocietyController {

    private final SocietyDAO societyDAO;
    public SocietyController(SocietyDAO societyDAO) { this.societyDAO = societyDAO; }

    // GET /api/societies
    // SQL: SELECT * FROM SOCIETY
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(societyDAO.findAll());
    }

    // GET /api/societies/{id}
    // SQL: SELECT * FROM SOCIETY WHERE soc_id = ?
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return ResponseEntity.ok(societyDAO.findById(id).orElse(null));
    }
}
