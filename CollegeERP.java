package com.college;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CollegeERP extends JFrame {

    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Faculty> faculties = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();

    private DefaultTableModel studentModel;
    private DefaultTableModel facultyModel;
    private DefaultTableModel courseModel;

    public CollegeERP() {
        setTitle("College Administration ERP");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Faculty", createFacultyPanel());
        tabbedPane.addTab("Courses", createCoursePanel());
        tabbedPane.addTab("Admissions & Exams", createAdmissionsExamsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        // Add sample data
        addSampleData();
    }

    private void addSampleData() {
        students.add(new Student("Alice Johnson", 19, "Data Structures"));
        students.add(new Student("Bob Smith", 20, "Mathematics, Physics"));
        
        faculties.add(new Faculty("Dr. John Doe", 45, "Computer Science", "Professor", 12345));
        
        courses.add(new Course("Data Structures", "CS-204", 4));
        courses.add(new Course("Mathematics", "MATH-101", 3));
        
        refreshTables();
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Welcome to College ERP System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        infoPanel.add(createCard("Total Students", "Manage all enrolled students"));
        infoPanel.add(createCard("Total Faculty", "Manage teaching staff"));
        infoPanel.add(createCard("Active Courses", "Manage college curriculum"));
        infoPanel.add(createCard("Admissions", "Process new admissions"));

        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, String desc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(240, 248, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel descLabel = new JLabel(desc, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Name", "Age", "Enrolled Courses"};
        studentModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(studentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(5);
        JTextField courseField = new JTextField(15);
        
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Courses:"));
        inputPanel.add(courseField);
        
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String courses = courseField.getText();
                students.add(new Student(name, age, courses));
                refreshTables();
                nameField.setText("");
                ageField.setText("");
                courseField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for student!");
            }
        });
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFacultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Name", "Age", "Department", "Designation", "ID"};
        facultyModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(facultyModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(10);
        JTextField ageField = new JTextField(5);
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
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        
        JButton addButton = new JButton("Add Faculty");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String dept = deptField.getText();
                String desig = desigField.getText();
                int id = Integer.parseInt(idField.getText());
                faculties.add(new Faculty(name, age, dept, desig, id));
                refreshTables();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for faculty!");
            }
        });
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Course Name", "Course Code", "Credits"};
        courseModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(courseModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameField = new JTextField(15);
        JTextField codeField = new JTextField(10);
        JTextField creditsField = new JTextField(5);
        
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Course Code:"));
        inputPanel.add(codeField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(creditsField);
        
        JButton addButton = new JButton("Add Course");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String code = codeField.getText();
                int credits = Integer.parseInt(creditsField.getText());
                courses.add(new Course(name, code, credits));
                refreshTables();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for course!");
            }
        });
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdmissionsExamsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Admission Section
        JPanel admissionPanel = new JPanel(new BorderLayout());
        admissionPanel.setBorder(BorderFactory.createTitledBorder("Run Admission Process"));
        JTextArea admissionLog = new JTextArea();
        admissionLog.setEditable(false);
        JButton runAdmissionBtn = new JButton("Simulate Admission");
        runAdmissionBtn.addActionListener(e -> {
            Admission admission = new Admission();
            admission.getdata("New Student", 18, 1005, 1);
            admissionLog.append("Admission processed for New Student, Roll: 1005\n");
        });
        admissionPanel.add(new JScrollPane(admissionLog), BorderLayout.CENTER);
        admissionPanel.add(runAdmissionBtn, BorderLayout.SOUTH);
        
        // Exam Section
        JPanel examPanel = new JPanel(new BorderLayout());
        examPanel.setBorder(BorderFactory.createTitledBorder("Run Examination Process"));
        JTextArea examLog = new JTextArea();
        examLog.setEditable(false);
        JButton runExamBtn = new JButton("Simulate Examination");
        runExamBtn.addActionListener(e -> {
            Examination exam = new Examination("Bob Smith", 1002, new String[]{"Math", "Physics"}, new int[]{88, 90}, "Final");
            examLog.append("Exam recorded for Bob Smith (Finals)\n");
            examLog.append("Math: 88, Physics: 90\n");
        });
        examPanel.add(new JScrollPane(examLog), BorderLayout.CENTER);
        examPanel.add(runExamBtn, BorderLayout.SOUTH);
        
        panel.add(admissionPanel);
        panel.add(examPanel);
        return panel;
    }

    private void refreshTables() {
        studentModel.setRowCount(0);
        for (Student s : students) {
            studentModel.addRow(new Object[]{s.getName(), s.getAge(), s.getEnrolledCourses()});
        }
        
        facultyModel.setRowCount(0);
        for (Faculty f : faculties) {
            facultyModel.addRow(new Object[]{f.getName(), f.getAge(), f.getDepartment(), f.getDesignation(), f.getId()});
        }
        
        courseModel.setRowCount(0);
        for (Course c : courses) {
            courseModel.addRow(new Object[]{c.getCourseName(), c.getCourseCode(), c.getCredits()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CollegeERP().setVisible(true);
        });
    }
}
