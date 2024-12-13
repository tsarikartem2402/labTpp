package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService {

    @Autowired
    private DataSource dataSource;

    // Метод для збереження/оновлення викладача
    public String saveTeacher(Teacher teacher) {
        String message;
        int teacherId = (teacher.id() == null || teacher.id().toString().trim().isEmpty()) ? 0 : teacher.id();
        
        String query = teacherId == 0
                ? "INSERT INTO teachers (first_name, last_name, position, department_id) VALUES (?, ?, ?, ?)"
                : "UPDATE teachers SET first_name = ?, last_name = ?, position = ?, department_id = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Валідація даних
            if (teacher.first_Name() == null || teacher.first_Name().trim().isEmpty() ||
                teacher.last_Name() == null || teacher.last_Name().trim().isEmpty() ||
                teacher.position() == null || teacher.position().trim().isEmpty() ||
                teacher.department_id() == null) {
                return "Помилка: всі поля повинні бути заповнені";
            }

            statement.setString(1, teacher.first_Name().trim());
            statement.setString(2, teacher.last_Name().trim());
            statement.setString(3, teacher.position().trim());
            statement.setLong(4, teacher.department_id());

            if (teacherId != 0) {
                statement.setInt(5, teacherId);
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                if (teacherId == 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return "Викладача успішно збережено з ID: " + generatedKeys.getInt(1);
                        }
                    }
                }
                message = "Викладача успішно оновлено!";
            } else {
                message = "Не вдалося зберегти викладача.";
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                message = "Помилка: викладач з таким ID вже існує";
            } else {
                message = "Помилка під час збереження викладача: " + e.getMessage();
            }
            e.printStackTrace();
        }
        return message;
    }

    // Метод для отримання всіх викладачів
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM teachers ORDER BY last_name, first_name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                teachers.add(mapRowToTeacher(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    // Метод для пошуку викладача за ID
    public Teacher findTeacherById(int id) {
        if (id <= 0) {
            return null;
        }

        String query = "SELECT * FROM teachers WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToTeacher(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для видалення викладача за ID
    public String deleteTeacher(int id) {
        if (id <= 0) {
            return "Помилка: некоректний ID викладача";
        }

        String query = "DELETE FROM teachers WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return "Викладача успішно видалено!";
            } else {
                return "Викладача з ID " + id + " не знайдено.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Помилка під час видалення викладача: " + e.getMessage();
        }
    }

    // Допоміжний метод для відображення викладача з ResultSet
    private Teacher mapRowToTeacher(ResultSet resultSet) throws SQLException {
        return new Teacher(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("position"),
                resultSet.getLong("department_id")
        );
    }
}