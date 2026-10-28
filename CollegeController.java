package com.college;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@RestController
public class CollegeController {

    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final CollegeAnalyticsDAO analyticsDAO = new CollegeAnalyticsDAO();

    public CollegeController() {
        // Ensure database tables and initial seed data exist
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseManager.initializeSchema(conn);
        } catch (Exception e) {
            System.err.println("Database init in CollegeController: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public String home() {
        int studentCount = studentDAO.getStudentCount();
        int courseCount = courseDAO.getCourseCount();
        int facultyCount = facultyDAO.getFacultyCount();

        return "<html>" +
                "<head><title>College ERP - SQL Backend</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 40px; color: #333; }" +
                ".container { max-width: 950px; margin: auto; background: white; padding: 40px; border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.06); }" +
                "h1 { color: #2c3e50; text-align: center; margin-bottom: 25px; font-size: 2.4em; }" +
                ".subtitle { text-align: center; color: #7f8c8d; font-size: 1.1em; margin-bottom: 30px; }" +
                ".nav { display: flex; justify-content: center; flex-wrap: wrap; gap: 15px; margin-bottom: 35px; }" +
                ".nav a { text-decoration: none; padding: 12px 22px; background: #3498db; color: white; border-radius: 8px; font-weight: bold; transition: 0.3s; }" +
                ".nav a:hover { background: #2980b9; transform: translateY(-2px); }" +
                ".metrics { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }" +
                ".metric-box { background: #ebf5fb; padding: 20px; border-radius: 8px; text-align: center; border: 1px solid #d4e6f1; }" +
                ".metric-number { font-size: 2.2em; font-weight: bold; color: #2980b9; margin-bottom: 5px; }" +
                ".metric-label { font-size: 0.95em; color: #566573; text-transform: uppercase; letter-spacing: 1px; }" +
                ".card { background: #fdfefe; padding: 25px; border-radius: 8px; border-left: 5px solid #2ecc71; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.03); }" +
                ".btn-action { display: inline-block; padding: 14px 28px; background: #27ae60; color: white; border-radius: 8px; font-size: 1.05em; font-weight: bold; text-decoration: none; text-align: center; margin-top: 15px; }" +
                ".btn-action:hover { background: #219150; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>College Administration ERP</h1>" +
                "<p class='subtitle'>Powered by SQL Backend with JDBC (Full DDL, DML, DQL, TCL & Analytics)</p>" +
                "<div class='metrics'>" +
                "<div class='metric-box'><div class='metric-number'>" + studentCount + "</div><div class='metric-label'>Students in DB</div></div>" +
                "<div class='metric-box'><div class='metric-number'>" + courseCount + "</div><div class='metric-label'>Active Courses</div></div>" +
                "<div class='metric-box'><div class='metric-number'>" + facultyCount + "</div><div class='metric-label'>Faculty Members</div></div>" +
                "</div>" +
                "<div class='nav'>" +
                "<a href='/students'>Manage Students</a>" +
                "<a href='/courses'>Manage Courses</a>" +
                "<a href='/faculty'>View Faculty</a>" +
                "<a href='/analytics'>SQL Analytics & Views</a>" +
                "<a href='/h2-console' target='_blank'>SQL Web Console</a>" +
                "</div>" +
                "<div class='card'>" +
                "<h3>Live SQL Database Connection</h3>" +
                "<p><strong>JDBC URL:</strong> <code>" + DatabaseManager.getDbUrl() + "</code></p>" +
                "<p><strong>Driver:</strong> <code>org.h2.Driver</code> (Full ANSI & MySQL Compatibility Mode)</p>" +
                "<p>All data is dynamically persisted into relational SQL tables with foreign key constraints, indexes, views, and stored functions.</p>" +
                "<a href='/run-database-operations' class='btn-action'>Run Maximum Built-in SQL Database Operations Suite</a>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    @GetMapping("/students")
    public String studentsPage() {
        List<Student> students = studentDAO.getAllStudents();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Students - College ERP</title><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: #f8fafc; }")
                .append(".container { max-width: 900px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th, td { border: 1px solid #e2e8f0; padding: 12px; text-align: left; }")
                .append("th { background-color: #3498db; color: white; }")
                .append("tr:nth-child(even) { background-color: #f8fafc; }")
                .append(".form-group { display: flex; gap: 10px; margin-top: 20px; }")
                .append("input[type=text], input[type=number] { padding: 10px; border: 1px solid #ccc; border-radius: 5px; }")
                .append("input[type=submit] { padding: 10px 20px; background: #27ae60; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }")
                .append(".btn { display: inline-block; padding: 8px 16px; background: #333; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }")
                .append("</style></head><body><div class='container'>");

        sb.append("<h2>Enrolled Students (Queried via JDBC)</h2>");
        sb.append("<table><tr><th>ID</th><th>Name</th><th>Age</th><th>Enrolled Courses</th></tr>");
        for (Student s : students) {
            sb.append("<tr><td>").append(s.getId()).append("</td>")
                    .append("<td>").append(s.getName()).append("</td>")
                    .append("<td>").append(s.getAge()).append("</td>")
                    .append("<td>").append(s.getEnrolledCourses()).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h3>Add New Student (JDBC Prepared Insert)</h3>");
        sb.append("<form method='post' action='/students' class='form-group'>")
                .append("<input type='text' name='name' placeholder='Student Name' required />")
                .append("<input type='number' name='age' placeholder='Age' required style='width: 80px;' />")
                .append("<input type='text' name='courses' placeholder='Enrolled Courses' required style='flex-grow: 1;' />")
                .append("<input type='submit' value='Insert via JDBC' />")
                .append("</form>");

        sb.append("<a href='/' class='btn'>Back to Dashboard</a>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    @PostMapping("/students")
    public String addStudent(@RequestParam String name, @RequestParam int age, @RequestParam String courses) {
        studentDAO.insertStudent(new Student(name, age, courses));
        return "<html><head><meta http-equiv='refresh' content='1;url=/students'></head><body><h3>Student inserted into SQL backend! Redirecting...</h3></body></html>";
    }

    @GetMapping("/courses")
    public String coursesPage() {
        List<Course> courses = courseDAO.getAllCourses();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Courses - College ERP</title><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: #f8fafc; }")
                .append(".container { max-width: 900px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th, td { border: 1px solid #e2e8f0; padding: 12px; text-align: left; }")
                .append("th { background-color: #e67e22; color: white; }")
                .append("tr:nth-child(even) { background-color: #f8fafc; }")
                .append(".form-group { display: flex; gap: 10px; margin-top: 20px; }")
                .append("input[type=text], input[type=number] { padding: 10px; border: 1px solid #ccc; border-radius: 5px; }")
                .append("input[type=submit] { padding: 10px 20px; background: #27ae60; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }")
                .append(".btn { display: inline-block; padding: 8px 16px; background: #333; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }")
                .append("</style></head><body><div class='container'>");

        sb.append("<h2>Active Courses (Queried via JDBC)</h2>");
        sb.append("<table><tr><th>ID</th><th>Course Name</th><th>Code</th><th>Credits</th></tr>");
        for (Course c : courses) {
            sb.append("<tr><td>").append(c.getId()).append("</td>")
                    .append("<td>").append(c.getCourseName()).append("</td>")
                    .append("<td>").append(c.getCourseCode()).append("</td>")
                    .append("<td>").append(c.getCredits()).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<h3>Add New Course (JDBC Prepared Insert)</h3>");
        sb.append("<form method='post' action='/courses' class='form-group'>")
                .append("<input type='text' name='name' placeholder='Course Name' required style='flex-grow: 1;' />")
                .append("<input type='text' name='code' placeholder='Code (e.g. CS-501)' required style='width: 150px;' />")
                .append("<input type='number' name='credits' placeholder='Credits' required style='width: 90px;' />")
                .append("<input type='submit' value='Insert via JDBC' />")
                .append("</form>");

        sb.append("<a href='/' class='btn'>Back to Dashboard</a>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    @PostMapping("/courses")
    public String addCourse(@RequestParam String name, @RequestParam String code, @RequestParam int credits) {
        courseDAO.insertCourse(new Course(name, code, credits));
        return "<html><head><meta http-equiv='refresh' content='1;url=/courses'></head><body><h3>Course inserted into SQL backend! Redirecting...</h3></body></html>";
    }

    @GetMapping("/faculty")
    public String facultyPage() {
        List<Faculty> faculties = facultyDAO.getAllFaculty();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Faculty - College ERP</title><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: #f8fafc; }")
                .append(".container { max-width: 900px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th, td { border: 1px solid #e2e8f0; padding: 12px; text-align: left; }")
                .append("th { background-color: #8e44ad; color: white; }")
                .append(".btn { display: inline-block; padding: 8px 16px; background: #333; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }")
                .append("</style></head><body><div class='container'>");

        sb.append("<h2>Faculty Directory (Queried via JDBC)</h2>");
        sb.append("<table><tr><th>DB ID</th><th>Code</th><th>Name</th><th>Age</th><th>Department</th><th>Designation</th></tr>");
        for (Faculty f : faculties) {
            sb.append("<tr><td>").append(f.getDbId()).append("</td>")
                    .append("<td>").append(f.getId()).append("</td>")
                    .append("<td>").append(f.getName()).append("</td>")
                    .append("<td>").append(f.getAge()).append("</td>")
                    .append("<td>").append(f.getDepartment()).append("</td>")
                    .append("<td>").append(f.getDesignation()).append("</td></tr>");
        }
        sb.append("</table>");
        sb.append("<a href='/' class='btn'>Back to Dashboard</a>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    @GetMapping("/analytics")
    public String analyticsPage() {
        List<Map<String, Object>> summaries = analyticsDAO.getStudentAcademicSummaries();
        List<Map<String, Object>> coursePerfs = analyticsDAO.getCoursePerformance();
        List<Map<String, Object>> deptStats = analyticsDAO.getDepartmentStats();
        List<Map<String, Object>> honors = analyticsDAO.getHonorsStudents();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>SQL Views & Analytics</title><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: #f4f6f9; }")
                .append(".container { max-width: 1000px; margin: auto; background: white; padding: 35px; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }")
                .append("h2 { color: #2c3e50; border-bottom: 2px solid #ecf0f1; padding-bottom: 8px; margin-top: 30px; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 25px; }")
                .append("th, td { border: 1px solid #e2e8f0; padding: 10px 14px; text-align: left; }")
                .append("th { background-color: #2c3e50; color: white; }")
                .append("tr:nth-child(even) { background-color: #f8fafc; }")
                .append(".badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-weight: bold; font-size: 0.85em; }")
                .append(".badge-distinction { background: #d4edda; color: #155724; }")
                .append(".badge-first { background: #cce5ff; color: #004085; }")
                .append(".btn { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 6px; font-weight: bold; }")
                .append("</style></head><body><div class='container'>");

        sb.append("<h1>Built-in SQL Views & Relational Analytics</h1>");

        // 1. View: v_student_academic_summary
        sb.append("<h2>SQL View: <code>v_student_academic_summary</code></h2>");
        sb.append("<table><tr><th>ID</th><th>Student</th><th>Age</th><th>Exams</th><th>Avg Marks</th><th>Top Mark</th><th>Avg Attendance</th><th>Status</th></tr>");
        for (Map<String, Object> r : summaries) {
            String status = String.valueOf(r.get("ACADEMIC_STATUS"));
            String badgeClass = "DISTINCTION".equals(status) ? "badge-distinction" : "badge-first";
            sb.append("<tr><td>").append(r.get("STUDENT_ID")).append("</td>")
                    .append("<td>").append(r.get("STUDENT_NAME")).append("</td>")
                    .append("<td>").append(r.get("AGE")).append("</td>")
                    .append("<td>").append(r.get("EXAMS_TAKEN")).append("</td>")
                    .append("<td><strong>").append(r.get("AVG_MARKS")).append("</strong></td>")
                    .append("<td>").append(r.get("MAX_MARKS")).append("</td>")
                    .append("<td>").append(r.get("AVG_ATTENDANCE")).append("%</td>")
                    .append("<td><span class='badge ").append(badgeClass).append("'>").append(status).append("</span></td></tr>");
        }
        sb.append("</table>");

        // 2. View: v_course_performance
        sb.append("<h2>SQL View: <code>v_course_performance</code></h2>");
        sb.append("<table><tr><th>Code</th><th>Course</th><th>Credits</th><th>Instructor</th><th>Submissions</th><th>Avg Score</th><th>Min</th><th>Max</th></tr>");
        for (Map<String, Object> r : coursePerfs) {
            sb.append("<tr><td>").append(r.get("COURSE_CODE")).append("</td>")
                    .append("<td>").append(r.get("COURSE_NAME")).append("</td>")
                    .append("<td>").append(r.get("CREDITS")).append("</td>")
                    .append("<td>").append(r.get("INSTRUCTOR_NAME")).append("</td>")
                    .append("<td>").append(r.get("TOTAL_SUBMISSIONS")).append("</td>")
                    .append("<td><strong>").append(r.get("AVG_SCORE")).append("</strong></td>")
                    .append("<td>").append(r.get("MIN_SCORE")).append("</td>")
                    .append("<td>").append(r.get("MAX_SCORE")).append("</td></tr>");
        }
        sb.append("</table>");

        // 3. View: v_department_stats
        sb.append("<h2>SQL View: <code>v_department_stats</code></h2>");
        sb.append("<table><tr><th>Department</th><th>Faculty Count</th><th>Avg Salary</th><th>Min Salary</th><th>Max Salary</th></tr>");
        for (Map<String, Object> r : deptStats) {
            sb.append("<tr><td>").append(r.get("DEPARTMENT")).append("</td>")
                    .append("<td>").append(r.get("FACULTY_COUNT")).append("</td>")
                    .append("<td>$").append(r.get("AVG_SALARY")).append("</td>")
                    .append("<td>$").append(r.get("MIN_SALARY")).append("</td>")
                    .append("<td>$").append(r.get("MAX_SALARY")).append("</td></tr>");
        }
        sb.append("</table>");

        // 4. Subquery Insert Table: honors_students
        sb.append("<h2>Table: <code>honors_students</code> (Populated via <code>INSERT ... SELECT</code>)</h2>");
        sb.append("<table><tr><th>Student ID</th><th>Name</th><th>Average Marks</th><th>Honors Timestamp</th></tr>");
        for (Map<String, Object> r : honors) {
            sb.append("<tr><td>").append(r.get("STUDENT_ID")).append("</td>")
                    .append("<td><strong>").append(r.get("NAME")).append("</strong></td>")
                    .append("<td>").append(r.get("AVG_MARKS")).append("</td>")
                    .append("<td>").append(r.get("HONORS_DATE")).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<br><a href='/' class='btn'>Back to Dashboard</a>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    @GetMapping("/run-database-operations")
    public String runDatabaseOperationsPage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        try {
            System.setOut(ps);
            System.setErr(ps);

            CollegeDatabase db = new CollegeDatabase();
            db.runOperations();
            db.closeDatabase();
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }

        String output = baos.toString(StandardCharsets.UTF_8);

        return "<html><head><title>SQL Database Operations Suite Log</title><style>" +
                "body { font-family: 'Consolas', 'Courier New', monospace; background-color: #1e1e1e; color: #d4d4d4; padding: 30px; }" +
                ".container { max-width: 1200px; margin: auto; background: #252526; padding: 25px; border-radius: 8px; border: 1px solid #3c3c3c; }" +
                "h1 { color: #4ec9b0; font-family: 'Segoe UI', Arial, sans-serif; }" +
                "pre { white-space: pre-wrap; word-wrap: break-word; font-size: 13px; line-height: 1.45; color: #9cdcfe; }" +
                "a { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #0e639c; color: white; text-decoration: none; border-radius: 5px; font-family: 'Segoe UI', sans-serif; font-weight: bold; }" +
                "</style></head><body><div class='container'>" +
                "<h1>Execution Log: Built-in SQL Database Operations via JDBC</h1>" +
                "<pre>" + output.replace("<", "&lt;").replace(">", "&gt;") + "</pre>" +
                "<a href='/'>Return to ERP Dashboard</a>" +
                "</div></body></html>";
    }

    // JSON REST Endpoints
    @GetMapping("/api/students")
    public List<Student> apiStudents() {
        return studentDAO.getAllStudents();
    }

    @GetMapping("/api/courses")
    public List<Course> apiCourses() {
        return courseDAO.getAllCourses();
    }

    @GetMapping("/api/faculty")
    public List<Faculty> apiFaculty() {
        return facultyDAO.getAllFaculty();
    }

    @GetMapping("/api/analytics")
    public Map<String, Object> apiAnalytics() {
        return Map.of(
                "studentSummaries", analyticsDAO.getStudentAcademicSummaries(),
                "coursePerformance", analyticsDAO.getCoursePerformance(),
                "departmentStats", analyticsDAO.getDepartmentStats(),
                "honorsStudents", analyticsDAO.getHonorsStudents()
        );
    }
}
