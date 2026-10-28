package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Faculty entity using JDBC PreparedStatements.
 */
public class FacultyDAO {

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT id, name, age, department, designation, faculty_code FROM faculties ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Faculty(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getInt("faculty_code")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching faculty: " + e.getMessage());
        }
        return list;
    }

    public boolean insertFaculty(Faculty f) {
        String sql = "INSERT INTO faculties (faculty_code, name, age, department, designation) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getId());
            ps.setString(2, f.getName());
            ps.setInt(3, f.getAge());
            ps.setString(4, f.getDepartment());
            ps.setString(5, f.getDesignation());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        f.setDbId(gk.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting faculty: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteFaculty(long dbId) {
        String sql = "DELETE FROM faculties WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dbId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting faculty: " + e.getMessage());
        }
        return false;
    }

    public int getFacultyCount() {
        String sql = "SELECT COUNT(*) FROM faculties";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting faculty: " + e.getMessage());
        }
        return 0;
    }
}
