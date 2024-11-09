package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Departments> rowMapper = (ResultSet rs, int rowNum) -> new Departments(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
    );

    public List<Departments> findAll() {
        return jdbcTemplate.query("SELECT * FROM departments", rowMapper);
    }

    public Optional<Departments> findById(int id) {
        return jdbcTemplate.query("SELECT * FROM departments WHERE id = ?", rowMapper, id).stream().findFirst();
    }

    public int save(Departments department) {
        return jdbcTemplate.update("INSERT INTO departments (name, description) VALUES (?, ?)",
                department.name(), department.description());
    }

    public int update(Departments department) {
        return jdbcTemplate.update("UPDATE departments SET name = ?, description = ? WHERE id = ?",
                department.name(), department.description(), department.id());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM departments WHERE id = ?", id);
    }
}
