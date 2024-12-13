package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DataSource dataSource;

    // Method to save/update department
    public String saveDepartment(Departments department) {
        String message;
        // Convert id to 0 if null or empty
        int departmentId = (department.id() == null) ? 0 : department.id();
        
        String query = departmentId == 0
                ? "INSERT INTO departments (name, description) VALUES (?, ?)"
                : "UPDATE departments SET name = ?, description = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Data validation
            if (department.name() == null || department.name().trim().isEmpty()) {
                return "Error: department name must not be empty";
            }

            statement.setString(1, department.name().trim());
            statement.setString(2, department.description() != null ? department.description().trim() : "");

            if (departmentId != 0) {
                statement.setInt(3, departmentId);
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                if (departmentId == 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return "Department successfully saved with ID: " + generatedKeys.getInt(1);
                        }
                    }
                }
                message = "Department successfully updated!";
            } else {
                message = "Failed to save department.";
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                message = "Error: department with this ID already exists";
            } else {
                message = "Error while saving department: " + e.getMessage();
            }
            e.printStackTrace();
        }
        return message;
    }

    // Method to get all departments
    public List<Departments> getAllDepartments() {
        List<Departments> departments = new ArrayList<>();
        String query = "SELECT * FROM departments ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                departments.add(mapRowToDepartment(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    // Method to find department by ID
    public Departments findDepartmentById(int id) {
        if (id <= 0) {
            return null;
        }

        String query = "SELECT * FROM departments WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToDepartment(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to delete department by ID
    public String deleteDepartment(int id) {
        if (id <= 0) {
            return "Error: invalid department ID";
        }

        // First check if there are any subjects associated with this department
        if (hasDependentSubjects(id)) {
            return "Error: Cannot delete department because it has associated subjects";
        }

        String query = "DELETE FROM departments WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return "Department successfully deleted!";
            } else {
                return "Department with ID " + id + " not found.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error while deleting department: " + e.getMessage();
        }
    }

    // Helper method to check if department has associated subjects
    private boolean hasDependentSubjects(int departmentId) {
        String query = "SELECT COUNT(*) FROM subjects WHERE department_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Find departments by name (search functionality)
    public List<Departments> findDepartmentsByName(String name) {
        List<Departments> departments = new ArrayList<>();
        String query = "SELECT * FROM departments WHERE name LIKE ? ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    departments.add(mapRowToDepartment(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    // Helper method to map department from ResultSet
    private Departments mapRowToDepartment(ResultSet resultSet) throws SQLException {
        return new Departments(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }
}