package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for SQL Views, Aggregations, and Analytical Reports.
 */
public class CollegeAnalyticsDAO {

    public List<Map<String, Object>> getStudentAcademicSummaries() {
        return executeQueryToMapList("SELECT * FROM v_student_academic_summary ORDER BY avg_marks DESC");
    }

    public List<Map<String, Object>> getCoursePerformance() {
        return executeQueryToMapList("SELECT * FROM v_course_performance ORDER BY avg_score DESC");
    }

    public List<Map<String, Object>> getDepartmentStats() {
        return executeQueryToMapList("SELECT * FROM v_department_stats ORDER BY faculty_count DESC");
    }

    public List<Map<String, Object>> getHonorsStudents() {
        return executeQueryToMapList("SELECT * FROM honors_students ORDER BY avg_marks DESC");
    }

    public List<Map<String, Object>> getRecentAuditLogs() {
        return executeQueryToMapList("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 10");
    }

    private List<Map<String, Object>> executeQueryToMapList(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error executing analytics query: " + e.getMessage());
        }
        return list;
    }
}
