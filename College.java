package com.college;

public class College {
    private String name;
    private String location;
    private int establishedYear;
    public College(String name, String location, int establishedYear) {
        this.name = name;
        this.location = location;
        this.establishedYear = establishedYear;
    }
    public String getName() {
        return name;
    }
    public void addStudent(String student) {
        System.out.println("Student " + student + " added to the college.");
    }
    public void addCourse(String course) {
        System.out.println("Course " + course + " added to the college.");
    }
    public void addFaculty(String faculty) {
        System.out.println("Faculty " + faculty + " added to the college.");
    }
    public void addDepartment(String department) {
        System.out.println("Department " + department + " added to the college.");
    }
    void getdata()
    {
        System.out.println("College Name: " + name);
        System.out.println("Location: " + location);
        System.out.println("Established Year: " + establishedYear);
    }
    void showdata()
    {
        System.out.println("Name:"+ name);
        System.out.println("Location:"+ location);
        System.out.println("Established Year:"+ establishedYear);
    }

    public static void main(String[] args) {
        System.out.println("--- College ---");
        College college = new College("Northbridge College", "London", 1987);
        college.showdata();
        
        System.out.println("\n--- Admission ---");
        Admission admission = new Admission();
        admission.getdata("Alice Johnson", 19, 1001, 2);
        
        System.out.println("\n--- Attendance ---");
        Attendance attendance = new Attendance(1001, "Alice Johnson", 2025, 91.8f);
        attendance.getdata();
        
        System.out.println("\n--- Course ---");
        Course course = new Course("Data Structures", "CS-204", 4);
        System.out.println("Course Name: " + course.getCourseName());
        System.out.println("Course Code: " + course.getCourseCode());
        System.out.println("Credits: " + course.getCredits());
        
        System.out.println("\n--- Examination ---");
        Examination exam = new Examination("Alice Johnson", 1001, new String[]{"Mathematics", "Science"}, new int[]{86, 92}, "Midterm");
        exam.showdata();
        
        System.out.println("\n--- Faculty ---");
        Faculty faculty = new Faculty("Dr. John Doe", 45, "Computer Science", "Professor", 12345);
        System.out.println("Name: " + faculty.getName());
        System.out.println("Department: " + faculty.getDepartment());
        System.out.println("Designation: " + faculty.getDesignation());
        
        System.out.println("\n--- Reviews ---");
        Reviews review = new Reviews();
        review.getdata("Jane Smith", 2023, "Great professor!");
        review.getdata();
        
        System.out.println("\n--- Student ---");
        Student student = new Student("John Doe", 20, "Math, Science");
        student.getdata();
        
        System.out.println("\n--- Syllabus ---");
        Syllabus syllabus = new Syllabus(new String[]{"Mathematics", "Science"});
        syllabus.showdata();
        
        System.out.println("\n--- Timetable ---");
        Timetable timetable = new Timetable();
        timetable.getdata("Mathematics", 101, "Dr. Jane Smith", "Monday");
        timetable.showdata();
        
        System.out.println("\n--- College Database ---");
        CollegeDatabaseThread.main(args);
    }
}

// Duplicate class definitions removed. They are located in their respective individual .java files.
