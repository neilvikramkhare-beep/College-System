package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Course entity using JDBC PreparedStatements.
 */
public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT id, course_name, course_code, credits FROM courses ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(
                        rs.getLong("id"),
                        rs.getString("course_name"),
                        rs.getString("course_code"),
                        rs.getInt("credits")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return list;
    }

    public Course getCourseById(long id) {
        String sql = "SELECT id, course_name, course_code, credits FROM courses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getLong("id"),
                            rs.getString("course_name"),
                            rs.getString("course_code"),
                            rs.getInt("credits")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course by id: " + e.getMessage());
        }
        return null;
    }

    public boolean insertCourse(Course c) {
        String sql = "INSERT INTO courses (course_name, course_code, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getCourseCode());
            ps.setInt(3, c.getCredits());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        c.setId(gk.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting course: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCourse(long id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        }
        return false;
    }

    public int getCourseCount() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting courses: " + e.getMessage());
        }
        return 0;
    }
}
