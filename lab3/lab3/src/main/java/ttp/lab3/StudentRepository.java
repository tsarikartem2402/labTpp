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
public class StudentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> rowMapper = new RowMapper<Student>() {
        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Student(
                rs.getInt("id"),
                rs.getString("first_name"),       
                rs.getString("last_name"),                  
                rs.getInt("year"),               
                rs.getString("group_name")
            );
        }
    };

    public List<Student> findAll() {
        return jdbcTemplate.query("SELECT id, first_name, last_name, year, group_name FROM students", rowMapper);
    }

    public Optional<Student> findById(int id) { 
        return jdbcTemplate.query("SELECT id, first_name, last_name, year, group_name FROM students WHERE id = ?", rowMapper, id)
                           .stream().findFirst();
    }

    public int save(Student student) {
 
        return jdbcTemplate.update(
                "INSERT INTO students (first_name, last_name, year, group_name) VALUES (?, ?, ?, ?)",
                student.first_name(), student.last_name(), student.year(), student.group_name()
        );
    }

    public int update(Student student) {
        return jdbcTemplate.update(
                "UPDATE students SET first_name = ?, last_name = ?, year = ?, group_name = ? WHERE id = ?",
                student.first_name(), student.last_name(), student.year(), student.group_name(), student.id()
        );
    }

    public int deleteById(int id) {
        return jdbcTemplate.update("DELETE FROM students WHERE id = ?", id);
    }
}
