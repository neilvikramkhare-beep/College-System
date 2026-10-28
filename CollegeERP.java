package com.college;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * CollegeERP Swing desktop application powered by real JDBC database backend.
 */
public class CollegeERP extends JFrame {

    private final StudentDAO studentDAO = new StudentDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final CollegeAnalyticsDAO analyticsDAO = new CollegeAnalyticsDAO();

    private DefaultTableModel studentModel;
    private DefaultTableModel facultyModel;
    private DefaultTableModel courseModel;
    private DefaultTableModel analyticsModel;

    private JLabel totalStudentsCard;
    private JLabel totalFacultyCard;
    private JLabel totalCoursesCard;

    public CollegeERP() {
        setTitle("College Administration ERP - JDBC SQL Backend");
        setSize(1050, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ensure database schema and seed data are ready
        initDatabase();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Faculty", createFacultyPanel());
        tabbedPane.addTab("Courses", createCoursePanel());
        tabbedPane.addTab("SQL Analytics", createAnalyticsPanel());
        tabbedPane.addTab("Admissions & Exams", createAdmissionsExamsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadDataFromDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseManager.initializeSchema(conn);
        } catch (Exception e) {
            System.err.println("Database initialization in GUI: " + e.getMessage());
        }
    }

    private void loadDataFromDatabase() {
        // Refresh Students
        if (studentModel != null) {
            studentModel.setRowCount(0);
            List<Student> students = studentDAO.getAllStudents();
            for (Student s : students) {
                studentModel.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getEnrolledCourses()});
            }
        }

        // Refresh Faculty
        if (facultyModel != null) {
            facultyModel.setRowCount(0);
            List<Faculty> faculties = facultyDAO.getAllFaculty();
            for (Faculty f : faculties) {
                facultyModel.addRow(new Object[]{f.getDbId(), f.getName(), f.getAge(), f.getDepartment(), f.getDesignation(), f.getId()});
            }
        }

        // Refresh Courses
        if (courseModel != null) {
            courseModel.setRowCount(0);
            List<Course> courses = courseDAO.getAllCourses();
            for (Course c : courses) {
                courseModel.addRow(new Object[]{c.getId(), c.getCourseName(), c.getCourseCode(), c.getCredits()});
            }
        }

        // Refresh Analytics
        if (analyticsModel != null) {
            analyticsModel.setRowCount(0);
            List<Map<String, Object>> summaries = analyticsDAO.getStudentAcademicSummaries();
            for (Map<String, Object> row : summaries) {
                analyticsModel.addRow(new Object[]{
                        row.get("STUDENT_ID"),
                        row.get("STUDENT_NAME"),
                        row.get("AVG_MARKS"),
                        row.get("MAX_MARKS"),
                        row.get("AVG_ATTENDANCE"),
                        row.get("ACADEMIC_STATUS")
                });
            }
        }

        // Refresh Dashboard counts
        if (totalStudentsCard != null) totalStudentsCard.setText("Enrolled: " + studentDAO.getStudentCount());
        if (totalFacultyCard != null) totalFacultyCard.setText("Active: " + facultyDAO.getFacultyCount());
        if (totalCoursesCard != null) totalCoursesCard.setText("Active: " + courseDAO.getCourseCount());
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("College Administration System (SQL Backend)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        totalStudentsCard = new JLabel("Enrolled: " + studentDAO.getStudentCount(), SwingConstants.CENTER);
        totalFacultyCard = new JLabel("Active: " + facultyDAO.getFacultyCount(), SwingConstants.CENTER);
        totalCoursesCard = new JLabel("Active: " + courseDAO.getCourseCount(), SwingConstants.CENTER);

        infoPanel.add(createCard("Total Students (SQL)", totalStudentsCard));
        infoPanel.add(createCard("Total Faculty (SQL)", totalFacultyCard));
        infoPanel.add(createCard("Active Courses (SQL)", totalCoursesCard));
        infoPanel.add(createCard("Database Connection", new JLabel("JDBC: " + DatabaseManager.getDbUrl().split(";")[0], SwingConstants.CENTER)));

        panel.add(infoPanel, BorderLayout.CENTER);

        JButton runSqlBtn = new JButton("Execute Full Built-in SQL Operations Suite");
        runSqlBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        runSqlBtn.setBackground(new Color(52, 152, 219));
        runSqlBtn.setForeground(Color.WHITE);
        runSqlBtn.addActionListener(e -> {
            new Thread(() -> {
                CollegeDatabase db = new CollegeDatabase();
                db.runOperations();
                db.closeDatabase();
                SwingUtilities.invokeLater(() -> {
                    loadDataFromDatabase();
                    JOptionPane.showMessageDialog(this, "All built-in SQL database operations executed successfully! Check console for full tabular logs.");
                });
            }).start();
        });
        panel.add(runSqlBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String title, JLabel descLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID (SQL PK)", "Name", "Age", "Enrolled Courses"};
        studentModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(studentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(12);
        JTextField ageField = new JTextField(4);
        JTextField courseField = new JTextField(15);

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Courses:"));
        inputPanel.add(courseField);

        JButton addButton = new JButton("Add Student (JDBC INSERT)");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String courses = courseField.getText().trim();
                if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");

                Student s = new Student(name, age, courses);
                boolean success = studentDAO.insertStudent(s);
                if (success) {
                    loadDataFromDatabase();
                    nameField.setText("");
                    ageField.setText("");
                    courseField.setText("");
                    JOptionPane.showMessageDialog(this, "Student added to SQL database successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error inserting student into SQL database.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid student input: " + ex.getMessage());
            }
        });
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                long id = ((Number) studentModel.getValueAt(row, 0)).longValue();
                studentDAO.deleteStudent(id);
                loadDataFromDatabase();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student row to delete.");
            }
        });
        inputPanel.add(deleteButton);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFacultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"DB ID", "Name", "Age", "Department", "Designation", "Code"};
        facultyModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(facultyModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(10);
        JTextField ageField = new JTextField(4);
        JTextField deptField = new JTextField(10);
        JTextField desigField = new JTextField(10);
        JTextField idField = new JTextField(5);

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Dept:"));
        inputPanel.add(deptField);
        inputPanel.add(new JLabel("Desig:"));
        inputPanel.add(desigField);
        inputPanel.add(new JLabel("Code:"));
        inputPanel.add(idField);

        JButton addButton = new JButton("Add Faculty (JDBC INSERT)");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String dept = deptField.getText().trim();
                String desig = desigField.getText().trim();
                int id = Integer.parseInt(idField.getText().trim());

                Faculty f = new Faculty(name, age, dept, desig, id);
                if (facultyDAO.insertFaculty(f)) {
                    loadDataFromDatabase();
                    nameField.setText("");
                    ageField.setText("");
                    deptField.setText("");
                    desigField.setText("");
                    idField.setText("");
                    JOptionPane.showMessageDialog(this, "Faculty added to SQL database successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert faculty into database.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid faculty input: " + ex.getMessage());
            }
        });
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"DB ID", "Course Name", "Course Code", "Credits"};
        courseModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(courseModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(14);
        JTextField codeField = new JTextField(8);
        JTextField creditsField = new JTextField(4);

        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Code:"));
        inputPanel.add(codeField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(creditsField);

        JButton addButton = new JButton("Add Course (JDBC INSERT)");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String code = codeField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());

                Course c = new Course(name, code, credits);
                if (courseDAO.insertCourse(c)) {
                    loadDataFromDatabase();
                    nameField.setText("");
                    codeField.setText("");
                    creditsField.setText("");
                    JOptionPane.showMessageDialog(this, "Course added to SQL database successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert course into database.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid course input: " + ex.getMessage());
            }
        });
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("SQL View: v_student_academic_summary (Real-time View Aggregations)", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Student Name", "Average Marks", "Highest Mark", "Attendance %", "Academic Status"};
        analyticsModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(analyticsModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh SQL View Data");
        refreshBtn.addActionListener(e -> loadDataFromDatabase());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAdmissionsExamsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Admission Section
        JPanel admissionPanel = new JPanel(new BorderLayout());
        admissionPanel.setBorder(BorderFactory.createTitledBorder("Run Admission Process (SQL Backed)"));
        JTextArea admissionLog = new JTextArea();
        admissionLog.setEditable(false);
        JButton runAdmissionBtn = new JButton("Simulate Admission & SQL Recording");
        runAdmissionBtn.addActionListener(e -> {
            Admission admission = new Admission();
            admission.getdata("New Student", 18, 1005, 1);
            admissionLog.append("Admission processed for New Student, Roll: 1005 recorded in database.\n");
        });
        admissionPanel.add(new JScrollPane(admissionLog), BorderLayout.CENTER);
        admissionPanel.add(runAdmissionBtn, BorderLayout.SOUTH);

        // Exam Section
        JPanel examPanel = new JPanel(new BorderLayout());
        examPanel.setBorder(BorderFactory.createTitledBorder("Run Examination Process (SQL Backed)"));
        JTextArea examLog = new JTextArea();
        examLog.setEditable(false);
        JButton runExamBtn = new JButton("Simulate Examination & Auto Grade Computation");
        runExamBtn.addActionListener(e -> {
            Examination exam = new Examination("Bob Smith", 1002, new String[]{"Math", "Physics"}, new int[]{88, 90}, "Final");
            examLog.append("Exam recorded for Bob Smith (Finals)\n");
            examLog.append("Math: 88, Physics: 90 -> Letter Grades calculated via SQL CASE statement.\n");
        });
        examPanel.add(new JScrollPane(examLog), BorderLayout.CENTER);
        examPanel.add(runExamBtn, BorderLayout.SOUTH);

        panel.add(admissionPanel);
        panel.add(examPanel);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CollegeERP().setVisible(true);
        });
    }
}
