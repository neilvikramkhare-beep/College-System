package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Student entity using JDBC PreparedStatements.
 */
public class StudentDAO {

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, name, age, enrolled_courses FROM students ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("enrolled_courses")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    public Student getStudentById(long id) {
        String sql = "SELECT id, name, age, enrolled_courses FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("enrolled_courses")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student by id: " + e.getMessage());
        }
        return null;
    }

    public boolean insertStudent(Student s) {
        String sql = "INSERT INTO students (name, age, enrolled_courses) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setInt(2, s.getAge());
            ps.setString(3, s.getEnrolledCourses());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        s.setId(gk.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteStudent(long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        }
        return false;
    }

    public int getStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
        }
        return 0;
    }
}
