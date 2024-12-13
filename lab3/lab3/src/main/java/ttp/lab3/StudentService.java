package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private DataSource dataSource;

    // Метод для збереження/оновлення студента
    public String saveStudent(Student student) {
        String message;
        // Конвертація id в 0 якщо він null або порожній
        int studentId = (student.id() == null || student.id().toString().trim().isEmpty()) ? 0 : student.id();
        
        String query = studentId == 0
                ? "INSERT INTO students (first_name, last_name, year, group_name) VALUES (?, ?, ?, ?)"
                : "UPDATE students SET first_name = ?, last_name = ?, year = ?, group_name = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Валідація даних
            if (student.first_name() == null || student.first_name().trim().isEmpty() ||
                student.last_name() == null || student.last_name().trim().isEmpty() ||
                student.group_name() == null || student.group_name().trim().isEmpty()) {
                return "Помилка: всі поля повинні бути заповнені";
            }

            if (student.year() < 1 || student.year() > 6) {
                return "Помилка: рік навчання повинен бути від 1 до 6";
            }

            statement.setString(1, student.first_name().trim());
            statement.setString(2, student.last_name().trim());
            statement.setInt(3, student.year());
            statement.setString(4, student.group_name().trim());

            if (studentId != 0) {
                statement.setInt(5, studentId);
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                if (studentId == 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return "Студента успішно збережено з ID: " + generatedKeys.getInt(1);
                        }
                    }
                }
                message = "Студента успішно оновлено!";
            } else {
                message = "Не вдалося зберегти студента.";
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                message = "Помилка: студент з таким ID вже існує";
            } else {
                message = "Помилка під час збереження студента: " + e.getMessage();
            }
            e.printStackTrace();
        }
        return message;
    }

    // Метод для отримання всіх студентів з сортуванням
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY last_name, first_name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(mapRowToStudent(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Метод для пошуку студента за ID
    public Student findStudentById(int id) {
        if (id <= 0) {
            return null;
        }

        String query = "SELECT * FROM students WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToStudent(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для видалення студента за ID
    public String deleteStudent(int id) {
        if (id <= 0) {
            return "Помилка: некоректний ID студента";
        }

        String query = "DELETE FROM students WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return "Студента успішно видалено!";
            } else {
                return "Студента з ID " + id + " не знайдено.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Помилка під час видалення студента: " + e.getMessage();
        }
    }

    // Пошук студентів за прізвищем (додатковий метод)
    public List<Student> findStudentsByLastName(String lastName) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE last_name LIKE ? ORDER BY first_name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, lastName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapRowToStudent(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    // Допоміжний метод для відображення студента з ResultSet
    private Student mapRowToStudent(ResultSet resultSet) throws SQLException {
        return new Student(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("year"),
                resultSet.getString("group_name")
        );
    }
}