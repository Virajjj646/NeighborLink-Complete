package com.neighborlink.service;

import com.neighborlink.dao.InsuranceDAO;
import com.neighborlink.model.Insurance;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class InsuranceService {
    private final InsuranceDAO insuranceDAO;
    public InsuranceService(InsuranceDAO insuranceDAO) { this.insuranceDAO = insuranceDAO; }

    public Map<String,Object> create(Insurance ins) {
        int id = insuranceDAO.create(ins);
        return Map.of("success", true, "policyId", id, "message", "Insurance policy created");
    }

    public List<Insurance> getAll()               { return insuranceDAO.findAll(); }
    public Optional<Insurance> getByRental(int id){ return insuranceDAO.findByRentalId(id); }
}
