package com.college;

/**
 * User-Defined SQL Functions / Stored Procedures registered into the SQL backend via JDBC ALIAS.
 * These are callable directly in SQL statements and via CallableStatement.
 */
public class DatabaseStoredFunctions {

    public static String calculateGrade(Integer marks) {
        if (marks == null) return "N/A";
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    public static String calculateAttendanceStatus(Double percentage) {
        if (percentage == null) return "UNKNOWN";
        if (percentage >= 85.0) return "EXEMPLARY";
        if (percentage >= 75.0) return "ELIGIBLE";
        if (percentage >= 65.0) return "CONDITIONAL";
        return "DETAINED";
    }

    public static Double calculateScholarship(Double gpa, Double fees) {
        if (gpa == null || fees == null) return 0.0;
        if (gpa >= 3.8) return fees * 0.50;
        if (gpa >= 3.5) return fees * 0.25;
        return 0.0;
    }
}
