package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BerichtService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateBericht(int id) {
        System.out.println("jdbctemplate is: " + jdbcTemplate);
        System.out.println("code has been reached aka updateBericht");
        String sql = "UPDATE Berichten SET message='TEST2' WHERE id=" + id;
        return jdbcTemplate.update(sql);
    }

    public Bericht getBericht(int id) {
        String sql = "SELECT * FROM BERICHTEN WHERE id=" + id;
        return (Bericht) jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper(Bericht.class));
    }
    public int getCountOfBerichten() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BERICHTEN", Integer.class);
    }
}
