package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private DataSource dataSource;

    // Method to save/update subject
    public String saveSubject(Subject subject) {
        String message;
        // Convert id to 0 if null or empty
        int subjectId = (subject.id() <= 0) ? 0 : subject.id();
        
        String query = subjectId == 0
                ? "INSERT INTO subjects (name, credits, department_id) VALUES (?, ?, ?)"
                : "UPDATE subjects SET name = ?, credits = ?, department_id = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Data validation
            if (subject.name() == null || subject.name().trim().isEmpty()) {
                return "Error: subject name must not be empty";
            }

            if (subject.credits() < 1 || subject.credits() > 30) {
                return "Error: credits must be between 1 and 30";
            }

            if (subject.department_id() <= 0) {
                return "Error: invalid department ID";
            }

            statement.setString(1, subject.name().trim());
            statement.setInt(2, subject.credits());
            statement.setInt(3, subject.department_id());

            if (subjectId != 0) {
                statement.setInt(4, subjectId);
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                if (subjectId == 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return "Subject successfully saved with ID: " + generatedKeys.getInt(1);
                        }
                    }
                }
                message = "Subject successfully updated!";
            } else {
                message = "Failed to save subject.";
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                message = "Error: subject with this ID already exists";
            } else {
                message = "Error while saving subject: " + e.getMessage();
            }
            e.printStackTrace();
        }
        return message;
    }

    // Method to get all subjects
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM subjects ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                subjects.add(mapRowToSubject(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    // Method to find subject by ID
    public Subject findSubjectById(int id) {
        if (id <= 0) {
            return null;
        }

        String query = "SELECT * FROM subjects WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToSubject(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to delete subject by ID
    public String deleteSubject(int id) {
        if (id <= 0) {
            return "Error: invalid subject ID";
        }

        String query = "DELETE FROM subjects WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return "Subject successfully deleted!";
            } else {
                return "Subject with ID " + id + " not found.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error while deleting subject: " + e.getMessage();
        }
    }

    // Find subjects by department ID (additional method)
    public List<Subject> findSubjectsByDepartment(int departmentId) {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM subjects WHERE department_id = ? ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    subjects.add(mapRowToSubject(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    // Helper method to map subject from ResultSet
    private Subject mapRowToSubject(ResultSet resultSet) throws SQLException {
        return new Subject(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("credits"),
                resultSet.getInt("department_id")
        );
    }
}