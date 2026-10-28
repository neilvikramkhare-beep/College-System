package com.college;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * DatabaseManager handles JDBC connection lifecycle, configuration,
 * schema initialization, database metadata inspection, and formatted query printing.
 */
public class DatabaseManager {

    private static String dbUrl = "jdbc:h2:./data/collegedb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;MODE=MySQL";
    private static String dbUser = "sa";
    private static String dbPassword = "";
    private static String dbDriver = "org.h2.Driver";

    static {
        loadConfiguration();
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver class not found: " + dbDriver + " - " + e.getMessage());
        }
    }

    private static void loadConfiguration() {
        // 1. Try reading application.properties
        File propFile = new File("application.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                Properties props = new Properties();
                props.load(in);
                if (props.containsKey("spring.datasource.url")) {
                    dbUrl = props.getProperty("spring.datasource.url");
                }
                if (props.containsKey("spring.datasource.username")) {
                    dbUser = props.getProperty("spring.datasource.username");
                }
                if (props.containsKey("spring.datasource.password")) {
                    dbPassword = props.getProperty("spring.datasource.password");
                }
                if (props.containsKey("spring.datasource.driver-class-name")) {
                    dbDriver = props.getProperty("spring.datasource.driver-class-name");
                }
            } catch (Exception ignored) {
            }
        }

        // 2. Override with system properties or environment variables if present
        if (System.getProperty("DB_URL") != null) dbUrl = System.getProperty("DB_URL");
        if (System.getenv("DB_URL") != null) dbUrl = System.getenv("DB_URL");
        if (System.getProperty("DB_USER") != null) dbUser = System.getProperty("DB_USER");
        if (System.getenv("DB_USER") != null) dbUser = System.getenv("DB_USER");
        if (System.getProperty("DB_PASSWORD") != null) dbPassword = System.getProperty("DB_PASSWORD");
        if (System.getenv("DB_PASSWORD") != null) dbPassword = System.getenv("DB_PASSWORD");

        // Ensure data directory exists if using file-based H2
        if (dbUrl.contains("./data/")) {
            File dataDir = new File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static void shutdown() {
        try {
            Class<?> mysqlCleanup = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            mysqlCleanup.getMethod("checkedShutdown").invoke(null);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Initializes all relational SQL tables, foreign key constraints, indexes, views, and functions.
     */
    public static void initializeSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 1. Colleges Table
            stmt.execute("CREATE TABLE IF NOT EXISTS colleges (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "location VARCHAR(100) NOT NULL, " +
                    "established_year INT NOT NULL" +
                    ")");

            // 2. Faculties Table
            stmt.execute("CREATE TABLE IF NOT EXISTS faculties (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "faculty_code INT UNIQUE NOT NULL, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT CHECK (age >= 21 AND age <= 80), " +
                    "department VARCHAR(50) NOT NULL, " +
                    "designation VARCHAR(50) NOT NULL, " +
                    "salary DECIMAL(10, 2) DEFAULT 55000.00" +
                    ")");

            // 3. Courses Table
            stmt.execute("CREATE TABLE IF NOT EXISTS courses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "course_code VARCHAR(20) UNIQUE NOT NULL, " +
                    "course_name VARCHAR(100) NOT NULL, " +
                    "credits INT NOT NULL CHECK (credits > 0 AND credits <= 10), " +
                    "faculty_id INT, " +
                    "CONSTRAINT fk_course_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL" +
                    ")");

            // 4. Students Table
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT CHECK (age >= 16 AND age <= 70), " +
                    "email VARCHAR(100) UNIQUE, " +
                    "enrolled_courses VARCHAR(255), " +
                    "gpa DECIMAL(3, 2) DEFAULT 0.00, " +
                    "enrollment_date DATE DEFAULT CURRENT_DATE" +
                    ")");

            // 5. Admissions Table
            stmt.execute("CREATE TABLE IF NOT EXISTS admissions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "application_no INT UNIQUE NOT NULL, " +
                    "semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8), " +
                    "status VARCHAR(20) DEFAULT 'ADMITTED', " +
                    "fees_paid DECIMAL(10, 2) DEFAULT 0.00, " +
                    "admission_date DATE DEFAULT CURRENT_DATE, " +
                    "CONSTRAINT fk_admission_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE" +
                    ")");

            // 6. Attendances Table
            stmt.execute("CREATE TABLE IF NOT EXISTS attendances (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "course_id INT, " +
                    "academic_year INT NOT NULL, " +
                    "attendance_percentage FLOAT NOT NULL CHECK (attendance_percentage >= 0 AND attendance_percentage <= 100), " +
                    "status VARCHAR(20) DEFAULT 'REGULAR', " +
                    "CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_att_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL" +
                    ")");

            // 7. Examinations Table
            stmt.execute("CREATE TABLE IF NOT EXISTS examinations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "course_id INT, " +
                    "subject_name VARCHAR(100) NOT NULL, " +
                    "exam_type VARCHAR(50) NOT NULL, " +
                    "marks INT NOT NULL CHECK (marks >= 0 AND marks <= 100), " +
                    "grade VARCHAR(5), " +
                    "CONSTRAINT fk_exam_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_exam_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                    ")");

            // 8. Reviews Table
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT, " +
                    "faculty_id INT, " +
                    "academic_year INT NOT NULL, " +
                    "rating INT CHECK (rating BETWEEN 1 AND 5), " +
                    "review_text VARCHAR(255), " +
                    "CONSTRAINT fk_rev_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL, " +
                    "CONSTRAINT fk_rev_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE" +
                    ")");

            // 9. Timetables Table
            stmt.execute("CREATE TABLE IF NOT EXISTS timetables (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "course_id INT, " +
                    "faculty_id INT, " +
                    "day_of_week VARCHAR(15) NOT NULL, " +
                    "room_number VARCHAR(20) NOT NULL, " +
                    "slot_time VARCHAR(30) NOT NULL, " +
                    "CONSTRAINT fk_time_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_time_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL" +
                    ")");

            // 10. Syllabi Table
            stmt.execute("CREATE TABLE IF NOT EXISTS syllabi (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "course_id INT NOT NULL, " +
                    "module_number INT NOT NULL, " +
                    "module_title VARCHAR(100) NOT NULL, " +
                    "topics VARCHAR(255) NOT NULL, " +
                    "hours_allocated INT DEFAULT 10, " +
                    "CONSTRAINT fk_syl_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                    ")");

            // 11. Enrollments Table (M:N Student to Course)
            stmt.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                    "student_id INT NOT NULL, " +
                    "course_id INT NOT NULL, " +
                    "enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (student_id, course_id), " +
                    "CONSTRAINT fk_enr_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_enr_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE" +
                    ")");

            // 12. Audit Logs Table (Transaction and Activity Auditing)
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "action_type VARCHAR(50) NOT NULL, " +
                    "details VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // 13. Honors Students Table (Populated via INSERT INTO ... SELECT)
            stmt.execute("CREATE TABLE IF NOT EXISTS honors_students (" +
                    "student_id INT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "avg_marks DECIMAL(5, 2), " +
                    "honors_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // 14. Create Indexes for query optimization
            try {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_student_name ON students(name)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_faculty_dept ON faculties(department)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_exam_marks ON examinations(marks)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_exam_student_course ON examinations(student_id, course_id)");
            } catch (SQLException ignored) {
            }

            // 15. Create SQL Views
            stmt.execute("CREATE OR REPLACE VIEW v_student_academic_summary AS " +
                    "SELECT s.id AS student_id, s.name AS student_name, s.age, " +
                    "COUNT(DISTINCT e.id) AS exams_taken, " +
                    "COALESCE(ROUND(AVG(e.marks), 2), 0.0) AS avg_marks, " +
                    "COALESCE(MAX(e.marks), 0) AS max_marks, " +
                    "COALESCE(ROUND(AVG(a.attendance_percentage), 1), 0.0) AS avg_attendance, " +
                    "CASE " +
                    "   WHEN AVG(e.marks) >= 85 THEN 'DISTINCTION' " +
                    "   WHEN AVG(e.marks) >= 70 THEN 'FIRST CLASS' " +
                    "   WHEN AVG(e.marks) >= 50 THEN 'SECOND CLASS' " +
                    "   WHEN AVG(e.marks) IS NULL THEN 'NO EXAMS' " +
                    "   ELSE 'NEEDS IMPROVEMENT' " +
                    "END AS academic_status " +
                    "FROM students s " +
                    "LEFT JOIN examinations e ON s.id = e.student_id " +
                    "LEFT JOIN attendances a ON s.id = a.student_id " +
                    "GROUP BY s.id, s.name, s.age");

            stmt.execute("CREATE OR REPLACE VIEW v_course_performance AS " +
                    "SELECT c.id AS course_id, c.course_code, c.course_name, c.credits, " +
                    "f.name AS instructor_name, " +
                    "COUNT(e.id) AS total_submissions, " +
                    "COALESCE(ROUND(AVG(e.marks), 2), 0.0) AS avg_score, " +
                    "COALESCE(MIN(e.marks), 0) AS min_score, " +
                    "COALESCE(MAX(e.marks), 0) AS max_score " +
                    "FROM courses c " +
                    "LEFT JOIN faculties f ON c.faculty_id = f.id " +
                    "LEFT JOIN examinations e ON c.id = e.course_id " +
                    "GROUP BY c.id, c.course_code, c.course_name, c.credits, f.name");

            stmt.execute("CREATE OR REPLACE VIEW v_department_stats AS " +
                    "SELECT department, " +
                    "COUNT(id) AS faculty_count, " +
                    "ROUND(AVG(salary), 2) AS avg_salary, " +
                    "MIN(salary) AS min_salary, " +
                    "MAX(salary) AS max_salary " +
                    "FROM faculties " +
                    "GROUP BY department");

            // 16. Register Stored SQL Aliases / Functions (User-Defined Functions)
            try {
                stmt.execute("CREATE ALIAS IF NOT EXISTS CALCULATE_GRADE FOR \"com.college.DatabaseStoredFunctions.calculateGrade\"");
                stmt.execute("CREATE ALIAS IF NOT EXISTS CALCULATE_ATTENDANCE_STATUS FOR \"com.college.DatabaseStoredFunctions.calculateAttendanceStatus\"");
                stmt.execute("CREATE ALIAS IF NOT EXISTS CALCULATE_SCHOLARSHIP FOR \"com.college.DatabaseStoredFunctions.calculateScholarship\"");
            } catch (SQLException ignored) {
                // Some database backends (like external MySQL) use stored routines instead of aliases
            }
        }
    }

    /**
     * Prints DatabaseMetaData information about capabilities, drivers, and schema.
     */
    public static void printDatabaseInfo(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("===============================================================================");
            System.out.println("                        DATABASE BACKEND METADATA                              ");
            System.out.println("===============================================================================");
            System.out.println("Database Product Name      : " + meta.getDatabaseProductName());
            System.out.println("Database Product Version   : " + meta.getDatabaseProductVersion());
            System.out.println("Database Major Version     : " + meta.getDatabaseMajorVersion());
            System.out.println("Database Minor Version     : " + meta.getDatabaseMinorVersion());
            System.out.println("JDBC Driver Name           : " + meta.getDriverName());
            System.out.println("JDBC Driver Version        : " + meta.getDriverVersion());
            System.out.println("JDBC Driver Major Version  : " + meta.getDriverMajorVersion());
            System.out.println("JDBC Specification Version : " + meta.getJDBCMajorVersion() + "." + meta.getJDBCMinorVersion());
            System.out.println("Connection URL             : " + meta.getURL());
            System.out.println("Connected Username         : " + meta.getUserName());
            System.out.println("Read-Only Mode             : " + conn.isReadOnly());
            System.out.println("Auto-Commit Mode           : " + conn.getAutoCommit());
            System.out.println("Transaction Isolation Level: " + getIsolationLevelName(conn.getTransactionIsolation()));
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("CAPABILITIES & FEATURE SUPPORT:");
            System.out.println(" - Supports Transactions       : " + meta.supportsTransactions());
            System.out.println(" - Supports Savepoints         : " + meta.supportsSavepoints());
            System.out.println(" - Supports Batch Updates      : " + meta.supportsBatchUpdates());
            System.out.println(" - Supports Stored Procedures  : " + meta.supportsStoredProcedures());
            System.out.println(" - Supports Outer Joins        : " + meta.supportsOuterJoins());
            System.out.println(" - Supports Full Outer Joins   : " + meta.supportsFullOuterJoins());
            System.out.println(" - Supports Union              : " + meta.supportsUnion());
            System.out.println(" - Supports Union All          : " + meta.supportsUnionAll());
            System.out.println(" - Supports Group By           : " + meta.supportsGroupBy());
            System.out.println(" - Supports Subqueries in IN   : " + meta.supportsSubqueriesInIns());
            System.out.println(" - Supports Subqueries in EXST : " + meta.supportsSubqueriesInExists());
            System.out.println(" - Supports Correlated Subq.   : " + meta.supportsCorrelatedSubqueries());
            System.out.println(" - Max Connections             : " + meta.getMaxConnections());
            System.out.println(" - Max Table Name Length       : " + meta.getMaxTableNameLength());
            System.out.println("===============================================================================");
        } catch (SQLException e) {
            System.err.println("Error reading database metadata: " + e.getMessage());
        }
    }

    private static String getIsolationLevelName(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE: return "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED: return "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED: return "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ: return "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE: return "SERIALIZABLE";
            default: return "UNKNOWN (" + level + ")";
        }
    }

    /**
     * Executes a query, measures execution duration, and formats the output into an ASCII grid table.
     */
    public static void executeAndPrintQuery(Connection conn, String sql, String title) {
        System.out.println("\n--- [SQL OPERATION] " + title + " ---");
        System.out.println("Query: " + sql);
        long startTime = System.nanoTime();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
            printResultSet(rs);
            System.out.println("(Query executed in " + elapsedMs + " ms)");
        } catch (SQLException e) {
            System.err.println("SQL Execution Failed: " + e.getMessage());
        }
    }

    /**
     * Renders a JDBC ResultSet into an aligned ASCII table using ResultSetMetaData.
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        List<String> headers = new ArrayList<>();
        List<Integer> colWidths = new ArrayList<>();

        for (int i = 1; i <= columnCount; i++) {
            String label = meta.getColumnLabel(i);
            headers.add(label);
            colWidths.add(Math.max(label.length(), 6));
        }

        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String val = rs.getString(i);
                if (val == null) val = "NULL";
                row.add(val);
                if (val.length() > colWidths.get(i - 1)) {
                    colWidths.set(i - 1, Math.min(val.length(), 40));
                }
            }
            rows.add(row);
        }

        // Print header separator
        StringBuilder border = new StringBuilder("+");
        for (int w : colWidths) {
            border.append("-".repeat(w + 2)).append("+");
        }
        System.out.println(border);

        // Print column headers
        StringBuilder headerLine = new StringBuilder("|");
        for (int i = 0; i < columnCount; i++) {
            headerLine.append(String.format(" %-" + colWidths.get(i) + "s |", headers.get(i)));
        }
        System.out.println(headerLine);
        System.out.println(border);

        // Print rows
        if (rows.isEmpty()) {
            System.out.println("| (No records returned)" + " ".repeat(Math.max(0, border.length() - 25)) + "|");
        } else {
            for (List<String> row : rows) {
                StringBuilder rowLine = new StringBuilder("|");
                for (int i = 0; i < columnCount; i++) {
                    String val = row.get(i);
                    if (val.length() > 40) {
                        val = val.substring(0, 37) + "...";
                    }
                    rowLine.append(String.format(" %-" + colWidths.get(i) + "s |", val));
                }
                System.out.println(rowLine);
            }
        }
        System.out.println(border);
        System.out.println("Total Rows: " + rows.size());
    }
}
