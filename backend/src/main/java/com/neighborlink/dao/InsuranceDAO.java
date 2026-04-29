package com.neighborlink.dao;

import com.neighborlink.model.Insurance;
import com.neighborlink.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class InsuranceDAO {

    private final JdbcTemplate jdbc;
    public InsuranceDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Insurance> mapper = (rs, rn) -> {
        Insurance i = new Insurance();
        i.setPolicyId(rs.getInt("policy_id"));
        i.setRentalId(rs.getInt("rental_id"));
        i.setPremiumAmt(rs.getBigDecimal("premium_amt"));
        i.setCoverageLimit(rs.getBigDecimal("coverage_limit"));
        return i;
    };

    // SQL: INSERT INTO INSURANCE(rental_id, premium_amt, coverage_limit) VALUES(?,?,?)
    // API: POST /api/insurance
    public int create(Insurance ins) {
        String sql = "INSERT INTO INSURANCE(rental_id, premium_amt, coverage_limit) VALUES(?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ins.getRentalId());
            ps.setBigDecimal(2, ins.getPremiumAmt());
            ps.setBigDecimal(3, ins.getCoverageLimit());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    public List<Insurance> findAll() {
        return jdbc.query("SELECT * FROM INSURANCE", mapper);
    }

    public Optional<Insurance> findByRentalId(int rentalId) {
        List<Insurance> list = jdbc.query(
            "SELECT * FROM INSURANCE WHERE rental_id = ?", mapper, rentalId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
