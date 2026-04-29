package com.neighborlink.dao;

import com.neighborlink.model.Society;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class SocietyDAO {

    private final JdbcTemplate jdbc;
    public SocietyDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<Society> mapper = (rs, rn) -> {
        Society s = new Society();
        s.setSocId(rs.getInt("soc_id"));
        s.setSocName(rs.getString("soc_name"));
        s.setCity(rs.getString("city"));
        s.setAddress(rs.getString("address"));
        return s;
    };

    public List<Society> findAll() {
        return jdbc.query("SELECT * FROM SOCIETY", mapper);
    }

    public Optional<Society> findById(int id) {
        List<Society> list = jdbc.query("SELECT * FROM SOCIETY WHERE soc_id = ?", mapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
