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
public class TeacherRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Teacher> rowMapper = new RowMapper<Teacher>() {
        @Override
        public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Teacher(
                rs.getInt("id"),                     
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("position"),
                rs.getLong("department_id")
            );
        }
    };

    public List<Teacher> findAll() {
        return jdbcTemplate.query("SELECT id, first_name, last_name, position, department_id FROM teachers", rowMapper);
    }

    public Optional<Teacher> findById(int id) {
        return jdbcTemplate.query("SELECT id, first_name, last_name, position, department_id FROM teachers WHERE id = ?", rowMapper, id)
                           .stream().findFirst();
    }

    public int save(Teacher teacher) {
  
        return jdbcTemplate.update(
                "INSERT INTO teachers (first_name, last_name, position, department_id) VALUES (?, ?, ?, ?)",
                teacher.first_Name(), teacher.last_Name(), teacher.position(), teacher.department_id()
        );
    }

    public int update(Teacher teacher) {
        return jdbcTemplate.update(
                "UPDATE teachers SET first_name = ?, last_name = ?, position = ?, department_id = ? WHERE id = ?",
                teacher.first_Name(), teacher.last_Name(), teacher.position(), teacher.department_id(), teacher.id()
        );
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM teachers WHERE id = ?", id);
    }
}
