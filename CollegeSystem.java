package CollegeAdministrationSystem;
import CollegeAdministrationSystem.Attendance;
import CollegeAdministrationSystem.College;
import CollegeAdministrationSystem.Course;
import CollegeAdministrationSystem.Examination;
import CollegeAdministrationSystem.Student;

public class CollegeSystem {
    public static void main(String[] args) {
        Attendance a = new Attendance(0, null, 0, 0);
        College c = new College(null, null, 0);
        Course co = new Course(null, null, 0);
        Examination e = new Examination(null, 0, args, null, null);
        Student s = new Student("John Doe", 20, "Math, Science");
        System.out.println("Student Name: " + s.name);
        System.out.println("Student age: " + s.age);
        System.out.println("Student courses: " + s.enrolledCourses);
        System.out.println("Attendance: " + a.attendance);
        System.out.println("College Name: " + c.getName());
    }
}