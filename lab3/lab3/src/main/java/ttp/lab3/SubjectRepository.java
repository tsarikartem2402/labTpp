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
public class SubjectRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Subject> rowMapper = (ResultSet rs, int rowNum) -> new Subject(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("credits"),
            rs.getInt("department_id")
    );

    public List<Subject> findAll() {
        return jdbcTemplate.query("SELECT * FROM subjects", rowMapper);
    }

    public Optional<Subject> findById(int id) {
        return jdbcTemplate.query("SELECT * FROM subjects WHERE id = ?", rowMapper, id).stream().findFirst();
    }

    public int save(Subject subject) {
        return jdbcTemplate.update("INSERT INTO subjects (name, credits, department_id) VALUES (?, ?, ?)",
                subject.name(), subject.credits(), subject.department_id());
    }

    public int update(Subject subject) {
        return jdbcTemplate.update("UPDATE subjects SET name = ?, credits = ?, department_id = ? WHERE id = ?",
                subject.name(), subject.credits(), subject.department_id(), subject.id());
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM subjects WHERE id = ?", id);
    }
}
