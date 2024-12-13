package ttp.lab3;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<User> getUserRowMapper() {
        return (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("role")
        );
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE username = ?", 
                getUserRowMapper(), 
                username
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int save(User user) {
        return jdbcTemplate.update(
            "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
            user.username(),
            user.password(),
            user.role()
        );
    }
}