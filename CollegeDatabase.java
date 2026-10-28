package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CollegeDatabase encapsulates comprehensive SQL backend operations using JDBC.
 * It provides DDL, DML, DQL, TCL, Window Functions, Stored Functions,
 * Batch Processing, and Metadata Inspection.
 */
public class CollegeDatabase {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private boolean isDatabaseOpen = false;
    private boolean isConnectionOpen = false;
    private boolean isStatementOpen = false;
    private boolean isResultSetOpen = false;

    public void runOperations() {
        System.out.println("===============================================================================");
        System.out.println("   STARTING COMPREHENSIVE JDBC SQL DATABASE OPERATIONS FOR COLLEGE ERP         ");
        System.out.println("===============================================================================");

        try {
            // -------------------------------------------------------------
            // PHASE 1: CONNECT TO SQL BACKEND & INSPECT METADATA
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 1] Establishing JDBC Connection to SQL Backend...");
            connection = DatabaseManager.getConnection();
            isConnectionOpen = (connection != null && !connection.isClosed());
            isDatabaseOpen = isConnectionOpen;
            System.out.println(" Connected successfully to: " + DatabaseManager.getDbUrl());

            DatabaseManager.printDatabaseInfo(connection);

            // -------------------------------------------------------------
            // PHASE 2: DDL (DATA DEFINITION LANGUAGE) OPERATIONS
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 2] Executing DDL (Data Definition Language) Operations...");
            statement = connection.createStatement();
            isStatementOpen = true;

            // 1. Initialize complete schema (Tables, Constraints, Views, Indexes, Functions)
            DatabaseManager.initializeSchema(connection);
            System.out.println(" Schema Initialized: 13 Tables, Foreign Keys, CHECK constraints created.");

            // 2. ALTER TABLE - Add column, alter defaults
            try {
                statement.execute("ALTER TABLE students ADD COLUMN IF NOT EXISTS contact_no VARCHAR(20) DEFAULT 'N/A'");
                statement.execute("ALTER TABLE faculties ALTER COLUMN salary SET DEFAULT 60000.00");
                System.out.println(" ALTER TABLE executed: Added column 'contact_no', updated default salary.");
            } catch (SQLException e) {
                System.out.println(" ALTER TABLE notification: " + e.getMessage());
            }

            // 3. Inspect Table Metadata via DatabaseMetaData
            inspectCatalogTables();

            // -------------------------------------------------------------
            // PHASE 3: DML (DATA MANIPULATION LANGUAGE) & BATCH INSERTS
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 3] Executing DML (Data Manipulation Language) Operations...");
            seedCollegeData();

            // -------------------------------------------------------------
            // PHASE 4: DQL (DATA QUERY LANGUAGE) & ADVANCED SQL QUERIES
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 4] Executing DQL & Advanced Built-in SQL Queries...");
            executeDQLOperations();

            // -------------------------------------------------------------
            // PHASE 5: TCL (TRANSACTION CONTROL LANGUAGE) OPERATIONS
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 5] Executing TCL (Transaction Control Language) Operations...");
            executeTCLOperations();

            // -------------------------------------------------------------
            // PHASE 6: ADVANCED JDBC FEATURES (Scrollable RS, Callable, etc.)
            // -------------------------------------------------------------
            System.out.println("\n>>> [PHASE 6] Executing Advanced JDBC Operations...");
            executeAdvancedJDBCOperations();

            System.out.println("\n===============================================================================");
            System.out.println("   ALL SQL DATABASE OPERATIONS COMPLETED SUCCESSFULLY!                         ");
            System.out.println("===============================================================================");

        } catch (Exception e) {
            System.err.println("Database operations encountered an error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inspectCatalogTables() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
            System.out.println("\n--- [METADATA] Active Database Tables and Views ---");
            List<String> tableList = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String tableType = tables.getString("TABLE_TYPE");
                tableList.add(tableType + ": " + tableName);
            }
            for (String t : tableList) {
                System.out.println("  * " + t);
            }
        }
    }

    private void seedCollegeData() throws SQLException {
        // Clear old audit / honors data for a clean demonstration run
        statement.execute("DELETE FROM audit_logs");
        statement.execute("DELETE FROM honors_students");

        // 1. Single Insert with RETURN_GENERATED_KEYS
        String checkCollege = "SELECT COUNT(*) FROM colleges";
        try (ResultSet rs = statement.executeQuery(checkCollege)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertCollegeSql = "INSERT INTO colleges (name, location, established_year) VALUES (?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertCollegeSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "Northbridge Institute of Technology");
                    ps.setString(2, "Boston, MA");
                    ps.setInt(3, 1987);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (gk.next()) {
                            System.out.println(" Single INSERT with Generated Key: College created with ID: " + gk.getInt(1));
                        }
                    }
                }
            }
        }

        // 2. Batch Insert Faculties (if not already seeded)
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM faculties")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String facultySql = "INSERT INTO faculties (faculty_code, name, age, department, designation, salary) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(facultySql)) {
                    Object[][] faculties = {
                            {101, "Dr. Alan Turing", 48, "Computer Science", "Professor", 95000.00},
                            {102, "Dr. Ada Lovelace", 36, "Computer Science", "Associate Professor", 88000.00},
                            {103, "Dr. Isaac Newton", 52, "Mathematics", "Department Head", 102000.00},
                            {104, "Dr. Marie Curie", 44, "Physics", "Professor", 98000.00},
                            {105, "Dr. Richard Feynman", 40, "Physics", "Associate Professor", 87000.00},
                            {106, "Prof. Claude Shannon", 38, "Information Theory", "Assistant Professor", 76000.00}
                    };
                    for (Object[] f : faculties) {
                        ps.setInt(1, (int) f[0]);
                        ps.setString(2, (String) f[1]);
                        ps.setInt(3, (int) f[2]);
                        ps.setString(4, (String) f[3]);
                        ps.setString(5, (String) f[4]);
                        ps.setDouble(6, (double) f[5]);
                        ps.addBatch();
                    }
                    int[] results = ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted " + results.length + " faculty members using addBatch().");
                }
            }
        }

        // 3. Batch Insert Courses
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM courses")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String courseSql = "INSERT INTO courses (course_code, course_name, credits, faculty_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(courseSql)) {
                    Object[][] courses = {
                            {"CS-204", "Data Structures & Algorithms", 4, 1},
                            {"CS-301", "Operating Systems", 4, 1},
                            {"CS-405", "Artificial Intelligence", 3, 2},
                            {"MATH-101", "Calculus & Linear Algebra", 3, 3},
                            {"MATH-202", "Discrete Mathematics", 3, 3},
                            {"PHY-101", "Classical Mechanics", 4, 4},
                            {"PHY-302", "Quantum Computing", 3, 5}
                    };
                    for (Object[] c : courses) {
                        ps.setString(1, (String) c[0]);
                        ps.setString(2, (String) c[1]);
                        ps.setInt(3, (int) c[2]);
                        ps.setInt(4, (int) c[3]);
                        ps.addBatch();
                    }
                    int[] results = ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted " + results.length + " courses using addBatch().");
                }
            }
        }

        // 4. Batch Insert Students
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String studentSql = "INSERT INTO students (name, age, email, enrolled_courses, gpa) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(studentSql)) {
                    Object[][] students = {
                            {"Alice Johnson", 19, "alice@college.edu", "CS-204, MATH-101", 3.90},
                            {"Bob Smith", 20, "bob@college.edu", "CS-204, CS-301", 3.65},
                            {"Charlie Brown", 21, "charlie@college.edu", "MATH-101, PHY-101", 3.40},
                            {"Diana Prince", 19, "diana@college.edu", "CS-405, PHY-302", 3.95},
                            {"Evan Wright", 22, "evan@college.edu", "CS-301, MATH-202", 3.10},
                            {"Fiona Gallagher", 20, "fiona@college.edu", "PHY-101, CS-204", 3.55}
                    };
                    for (Object[] s : students) {
                        ps.setString(1, (String) s[0]);
                        ps.setInt(2, (int) s[1]);
                        ps.setString(3, (String) s[2]);
                        ps.setString(4, (String) s[3]);
                        ps.setDouble(5, (double) s[4]);
                        ps.addBatch();
                    }
                    int[] results = ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted " + results.length + " students using addBatch().");
                }
            }
        }

        // 5. Batch Insert Admissions
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM admissions")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String admSql = "INSERT INTO admissions (student_id, application_no, semester, status, fees_paid) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(admSql)) {
                    for (int i = 1; i <= 6; i++) {
                        ps.setInt(1, i);
                        ps.setInt(2, 202600 + i);
                        ps.setInt(3, 2);
                        ps.setString(4, "ENROLLED");
                        ps.setDouble(5, 12500.00 + (i * 500));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println(" Batch INSERT: Populated student admissions.");
                }
            }
        }

        // 6. Batch Insert Examinations
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM examinations")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String examSql = "INSERT INTO examinations (student_id, course_id, subject_name, exam_type, marks) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(examSql)) {
                    Object[][] exams = {
                            {1, 1, "Data Structures", "Midterm", 92},
                            {1, 4, "Calculus", "Midterm", 88},
                            {2, 1, "Data Structures", "Midterm", 78},
                            {2, 2, "Operating Systems", "Midterm", 84},
                            {3, 4, "Calculus", "Midterm", 68},
                            {3, 6, "Classical Mechanics", "Midterm", 74},
                            {4, 3, "Artificial Intelligence", "Midterm", 96},
                            {4, 7, "Quantum Computing", "Midterm", 94},
                            {5, 2, "Operating Systems", "Midterm", 62},
                            {5, 5, "Discrete Mathematics", "Midterm", 70},
                            {6, 1, "Data Structures", "Midterm", 85},
                            {6, 6, "Classical Mechanics", "Midterm", 80}
                    };
                    for (Object[] e : exams) {
                        ps.setInt(1, (int) e[0]);
                        ps.setInt(2, (int) e[1]);
                        ps.setString(3, (String) e[2]);
                        ps.setString(4, (String) e[3]);
                        ps.setInt(5, (int) e[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted student examination marks.");
                }
            }
        }

        // 7. Computed UPDATE with CASE Expression
        statement.executeUpdate("UPDATE examinations SET grade = " +
                "CASE " +
                "   WHEN marks >= 90 THEN 'A+' " +
                "   WHEN marks >= 80 THEN 'A' " +
                "   WHEN marks >= 70 THEN 'B' " +
                "   WHEN marks >= 60 THEN 'C' " +
                "   ELSE 'F' " +
                "END");
        System.out.println(" Computed UPDATE: Automatically evaluated and assigned letter grades via CASE statement.");

        // 8. Batch Insert Attendances
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM attendances")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String attSql = "INSERT INTO attendances (student_id, course_id, academic_year, attendance_percentage, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(attSql)) {
                    Object[][] atts = {
                            {1, 1, 2026, 94.5f, "EXEMPLARY"},
                            {2, 1, 2026, 88.0f, "REGULAR"},
                            {3, 4, 2026, 76.5f, "REGULAR"},
                            {4, 3, 2026, 98.0f, "EXEMPLARY"},
                            {5, 2, 2026, 68.0f, "CONDITIONAL"},
                            {6, 6, 2026, 85.0f, "REGULAR"}
                    };
                    for (Object[] a : atts) {
                        ps.setInt(1, (int) a[0]);
                        ps.setInt(2, (int) a[1]);
                        ps.setInt(3, (int) a[2]);
                        ps.setFloat(4, (float) a[3]);
                        ps.setString(5, (String) a[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted attendance records.");
                }
            }
        }

        // 9. Batch Insert Reviews
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM reviews")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String revSql = "INSERT INTO reviews (student_id, faculty_id, academic_year, rating, review_text) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(revSql)) {
                    Object[][] revs = {
                            {1, 1, 2026, 5, "Incredible teacher, made algorithms intuitive and exciting!"},
                            {2, 1, 2026, 5, "Dr. Turing's lectures are legendary."},
                            {3, 3, 2026, 4, "Challenging math assignments but very fair grading."},
                            {4, 2, 2026, 5, "Dr. Lovelace is an exceptional mentor in AI projects."}
                    };
                    for (Object[] r : revs) {
                        ps.setInt(1, (int) r[0]);
                        ps.setInt(2, (int) r[1]);
                        ps.setInt(3, (int) r[2]);
                        ps.setInt(4, (int) r[3]);
                        ps.setString(5, (String) r[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println(" Batch INSERT: Inserted faculty reviews.");
                }
            }
        }

        // 10. Batch Insert Timetables & Syllabi
        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM timetables")) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.execute("INSERT INTO timetables (course_id, faculty_id, day_of_week, room_number, slot_time) VALUES " +
                        "(1, 1, 'Monday', 'Room-101', '09:00 - 10:30 AM'), " +
                        "(2, 1, 'Tuesday', 'Lab-3', '11:00 - 12:30 PM'), " +
                        "(3, 2, 'Wednesday', 'Room-204', '01:30 - 03:00 PM'), " +
                        "(4, 3, 'Thursday', 'Room-105', '10:00 - 11:30 AM')");
                System.out.println(" INSERT: Populated timetable schedule.");
            }
        }

        try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM syllabi")) {
            if (rs.next() && rs.getInt(1) == 0) {
                statement.execute("INSERT INTO syllabi (course_id, module_number, module_title, topics, hours_allocated) VALUES " +
                        "(1, 1, 'Linear Structures', 'Arrays, Linked Lists, Stacks, Queues', 12), " +
                        "(1, 2, 'Non-linear Structures', 'Trees, Graphs, Hash Tables', 16), " +
                        "(3, 1, 'Search & Knowledge', 'Heuristics, A* Search, Logic Representation', 14)");
                System.out.println(" INSERT: Populated course syllabi.");
            }
        }

        // 11. Subquery Insert: INSERT INTO ... SELECT
        statement.execute("INSERT INTO honors_students (student_id, name, avg_marks) " +
                "SELECT s.id, s.name, ROUND(AVG(e.marks), 2) " +
                "FROM students s " +
                "JOIN examinations e ON s.id = e.student_id " +
                "GROUP BY s.id, s.name " +
                "HAVING AVG(e.marks) >= 85.0");
        System.out.println(" Subquery INSERT (INSERT INTO ... SELECT): Populated honors_students list for marks >= 85.");
    }

    private void executeDQLOperations() throws SQLException {
        // Operation 1: Basic SELECT with Projection, Filtering, and Aliasing
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT id AS student_id, name AS student_name, age, email, gpa " +
                        "FROM students " +
                        "WHERE age >= 20 " +
                        "ORDER BY gpa DESC",
                "Filtering & Projection (WHERE, ORDER BY)");

        // Operation 2: Pattern Matching & IN Operator
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT course_code, course_name, credits " +
                        "FROM courses " +
                        "WHERE course_name LIKE '%Computer%' OR course_name LIKE '%Data%' OR credits IN (3, 4)",
                "Pattern Matching (LIKE) and Set Membership (IN)");

        // Operation 3: Range Check with BETWEEN
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT student_id, subject_name, exam_type, marks, grade " +
                        "FROM examinations " +
                        "WHERE marks BETWEEN 80 AND 95 " +
                        "ORDER BY marks DESC",
                "Range Verification (BETWEEN ... AND ...)");

        // Operation 4: Aggregation and GROUP BY with HAVING clause
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT subject_name, " +
                        "COUNT(student_id) AS student_count, " +
                        "ROUND(AVG(marks), 2) AS average_score, " +
                        "MIN(marks) AS lowest_mark, " +
                        "MAX(marks) AS highest_mark " +
                        "FROM examinations " +
                        "GROUP BY subject_name " +
                        "HAVING AVG(marks) >= 75.0 " +
                        "ORDER BY average_score DESC",
                "Aggregations & Grouping (COUNT, AVG, MIN, MAX, GROUP BY, HAVING)");

        // Operation 5: INNER JOIN (Students x Examinations x Courses)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT s.name AS student_name, c.course_code, c.course_name, e.subject_name, e.marks, e.grade " +
                        "FROM students s " +
                        "INNER JOIN examinations e ON s.id = e.student_id " +
                        "INNER JOIN courses c ON e.course_id = c.id " +
                        "ORDER BY s.name, e.marks DESC",
                "Relational INNER JOIN across 3 Tables");

        // Operation 6: LEFT OUTER JOIN with COALESCE (All Students with optional Reviews & Admissions)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT s.id, s.name, " +
                        "COALESCE(a.application_no, 0) AS application_no, " +
                        "COALESCE(a.fees_paid, 0.0) AS fees_paid, " +
                        "COALESCE(CAST(r.rating AS VARCHAR), 'No Review') AS rating, " +
                        "COALESCE(r.review_text, 'N/A') AS feedback " +
                        "FROM students s " +
                        "LEFT JOIN admissions a ON s.id = a.student_id " +
                        "LEFT JOIN reviews r ON s.id = r.student_id " +
                        "ORDER BY s.id",
                "LEFT OUTER JOIN with COALESCE Function");

        // Operation 7: RIGHT OUTER JOIN (Courses and Faculty Instructors)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT f.name AS professor_name, f.department, c.course_code, c.course_name " +
                        "FROM courses c " +
                        "RIGHT OUTER JOIN faculties f ON c.faculty_id = f.id " +
                        "ORDER BY f.department, f.name",
                "RIGHT OUTER JOIN (Faculty and Courses)");

        // Operation 8: SELF JOIN (Faculty in the same department)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT f1.department, f1.name AS faculty_member_1, f2.name AS faculty_member_2 " +
                        "FROM faculties f1 " +
                        "JOIN faculties f2 ON f1.department = f2.department AND f1.id < f2.id " +
                        "ORDER BY f1.department",
                "SELF JOIN (Colleagues sharing the same Department)");

        // Operation 9: Subquery in SELECT (Scalar Subquery)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT s.name, s.gpa, " +
                        "(SELECT COUNT(*) FROM examinations WHERE student_id = s.id) AS total_exams, " +
                        "(SELECT ROUND(AVG(marks), 2) FROM examinations WHERE student_id = s.id) AS calculated_avg " +
                        "FROM students s " +
                        "ORDER BY s.gpa DESC",
                "Scalar Subquery in SELECT clause");

        // Operation 10: Subquery in WHERE with IN
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT name, department, designation " +
                        "FROM faculties " +
                        "WHERE id IN (SELECT DISTINCT faculty_id FROM courses WHERE credits = 4)",
                "Subquery in WHERE clause with IN");

        // Operation 11: Correlated Subquery with EXISTS
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT c.course_code, c.course_name " +
                        "FROM courses c " +
                        "WHERE EXISTS (SELECT 1 FROM timetables t WHERE t.course_id = c.id)",
                "Correlated Subquery with EXISTS operator");

        // Operation 12: Set Operations - UNION ALL (Consolidated Campus Directory)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT name, 'Faculty' AS role, department AS affiliation FROM faculties " +
                        "UNION ALL " +
                        "SELECT name, 'Student' AS role, enrolled_courses AS affiliation FROM students " +
                        "ORDER BY name",
                "Set Operation: UNION ALL (Consolidated Campus Directory)");

        // Operation 13: Built-in SQL String & Math Functions
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT UPPER(name) AS uppercase_name, " +
                        "LENGTH(name) AS name_char_count, " +
                        "CONCAT(course_code, ' - ', course_name) AS full_course_title, " +
                        "ROUND(credits * 1.5, 1) AS estimated_lab_hours " +
                        "FROM courses",
                "Built-in SQL Functions (UPPER, LENGTH, CONCAT, ROUND)");

        // Operation 14: Window / Analytic Functions (ROW_NUMBER and DENSE_RANK)
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT s.name AS student_name, e.subject_name, e.marks, " +
                        "ROW_NUMBER() OVER (PARTITION BY e.subject_name ORDER BY e.marks DESC) AS rank_in_subject, " +
                        "DENSE_RANK() OVER (ORDER BY e.marks DESC) AS overall_college_rank " +
                        "FROM examinations e " +
                        "JOIN students s ON e.student_id = s.id " +
                        "ORDER BY e.subject_name, rank_in_subject",
                "Analytic Window Functions (ROW_NUMBER, DENSE_RANK with PARTITION BY)");

        // Operation 15: Querying Pre-Compiled SQL View v_student_academic_summary
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT * FROM v_student_academic_summary ORDER BY avg_marks DESC",
                "Querying Database View: v_student_academic_summary");

        // Operation 16: Querying Pre-Compiled SQL View v_department_stats
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT * FROM v_department_stats ORDER BY faculty_count DESC",
                "Querying Database View: v_department_stats");

        // Operation 17: Querying Table Populated by Subquery: honors_students
        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT * FROM honors_students ORDER BY avg_marks DESC",
                "Querying Table populated by INSERT ... SELECT (honors_students)");

        // Operation 18: Calling User-Defined SQL Stored Functions
        try {
            DatabaseManager.executeAndPrintQuery(connection,
                    "SELECT s.name AS student_name, e.marks, " +
                            "CALCULATE_GRADE(e.marks) AS calculated_letter_grade, " +
                            "CALCULATE_ATTENDANCE_STATUS(a.attendance_percentage) AS eligibility " +
                            "FROM students s " +
                            "JOIN examinations e ON s.id = e.student_id " +
                            "LEFT JOIN attendances a ON s.id = a.student_id " +
                            "LIMIT 5",
                    "Invoking User-Defined Stored Functions in SQL (CALCULATE_GRADE & ATTENDANCE)");
        } catch (Exception e) {
            System.out.println("Note on Stored Functions: " + e.getMessage());
        }
    }

    private void executeTCLOperations() throws SQLException {
        System.out.println("\n--- [TCL OPERATION] Multi-Operation Atomic Transaction (Commit) ---");
        boolean initialAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false); // Begin Transaction

            // 1. Insert new enrollment record
            String auditSql = "INSERT INTO audit_logs (action_type, details) VALUES (?, ?)";
            try (PreparedStatement psAudit = connection.prepareStatement(auditSql)) {
                psAudit.setString(1, "ENROLLMENT_TRANSACTION");
                psAudit.setString(2, "Student 1 enrolled into Course 3 with tuition verification.");
                psAudit.executeUpdate();
            }

            // 2. Insert audit log
            String updateStudentGpa = "UPDATE students SET gpa = gpa + 0.05 WHERE id = 1";
            statement.executeUpdate(updateStudentGpa);

            connection.commit(); // Commit Transaction
            System.out.println(" Transaction committed successfully! All operations persisted atomically.");

        } catch (SQLException e) {
            connection.rollback();
            System.err.println(" Transaction failed and was rolled back: " + e.getMessage());
        } finally {
            connection.setAutoCommit(initialAutoCommit);
        }

        System.out.println("\n--- [TCL OPERATION] Savepoint Creation & Partial Rollback ---");
        try {
            connection.setAutoCommit(false);

            statement.executeUpdate("INSERT INTO audit_logs (action_type, details) VALUES ('STEP_1', 'Permanent audit step 1')");
            Savepoint savepoint1 = connection.setSavepoint("Savepoint_Step1");
            System.out.println(" Savepoint 'Savepoint_Step1' established.");

            // Do step 2
            statement.executeUpdate("INSERT INTO audit_logs (action_type, details) VALUES ('STEP_2_TEMP', 'Temporary trial entry')");

            // Rollback to savepoint 1
            connection.rollback(savepoint1);
            System.out.println(" Rolled back temporary change to Savepoint_Step1.");

            connection.commit();
            System.out.println(" Committed verified operations after Savepoint rollback.");

        } catch (SQLException e) {
            connection.rollback();
            System.err.println(" Savepoint operation failed: " + e.getMessage());
        } finally {
            connection.setAutoCommit(initialAutoCommit);
        }

        DatabaseManager.executeAndPrintQuery(connection,
                "SELECT * FROM audit_logs ORDER BY id DESC",
                "Verified Audit Logs Persisted via TCL Transactions");
    }

    private void executeAdvancedJDBCOperations() throws SQLException {
        // 1. Scrollable ResultSet Demonstration
        System.out.println("\n--- [JDBC ADVANCED] Scrollable ResultSet (TYPE_SCROLL_INSENSITIVE) ---");
        String scrollQuery = "SELECT id, name, email FROM students ORDER BY id";
        try (Statement scrollStmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = scrollStmt.executeQuery(scrollQuery)) {

            if (rs.last()) {
                int count = rs.getRow();
                System.out.println(" Scrollable ResultSet: Total rows discovered via rs.last() = " + count);
                System.out.println(" Last Student: " + rs.getString("name") + " (" + rs.getString("email") + ")");

                if (rs.first()) {
                    System.out.println(" First Student via rs.first(): " + rs.getString("name"));
                }

                if (rs.absolute(2)) {
                    System.out.println(" Second Student via rs.absolute(2): " + rs.getString("name"));
                }

                if (rs.previous()) {
                    System.out.println(" Traversed backwards via rs.previous(): " + rs.getString("name"));
                }
            }
        }

        // 2. Callable Statement / Parameterized Function
        System.out.println("\n--- [JDBC ADVANCED] Dynamic Calculation via Stored Logic ---");
        double sampleScholarship = DatabaseStoredFunctions.calculateScholarship(3.9, 15000.0);
        System.out.println(" Scholarship computation for GPA 3.9 on $15,000 fees: $" + sampleScholarship);
    }

    // =========================================================================
    // LIFECYCLE & STATUS METHODS (Retained for full backward compatibility)
    // =========================================================================

    public void closeResultSet() {
        if (resultSet != null) {
            DatabaseManager.closeResultSet(resultSet);
            resultSet = null;
        }
        isResultSetOpen = false;
        System.out.println("Closing result set...");
    }

    public void closeStatement() {
        if (statement != null) {
            DatabaseManager.closeStatement(statement);
            statement = null;
        }
        isStatementOpen = false;
        System.out.println("Closing statement...");
    }

    public void closeConnection() {
        if (connection != null) {
            DatabaseManager.closeConnection(connection);
            connection = null;
        }
        isConnectionOpen = false;
        System.out.println("Closing connection...");
    }

    public void closeDatabase() {
        closeResultSet();
        closeStatement();
        closeConnection();
        DatabaseManager.shutdown();
        isDatabaseOpen = false;
        System.out.println("Closing database...");
    }

    public void printDatabaseStatus() {
        System.out.println("Database status: " + (isDatabaseOpen ? "Open" : "Closed"));
    }

    public void printConnectionStatus() {
        System.out.println("Connection status: " + (isConnectionOpen ? "Open" : "Closed"));
    }

    public void printStatementStatus() {
        System.out.println("Statement status: " + (isStatementOpen ? "Open" : "Closed"));
    }

    public void printResultSetStatus() {
        System.out.println("Result set status: " + (isResultSetOpen ? "Open" : "Closed"));
    }
}
