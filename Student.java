package CollegeAdministrationSystem;
import java.util.List;
public class Student {
    String name;
    int age;
    String enrolledCourses;
    public Student(String name, int age, String enrolledCourses) {
        this.name = name;
        this.age = age;
        this.enrolledCourses = enrolledCourses;
    }
    public void enrollInCourse(Course course) {
        enrolledCourses += ", " + course.getCourseName();
    }
    public String toString() {
        return "Student: " + name + ", Age: " + age + ", Enrolled Courses: " + enrolledCourses;
    }
    public void getdata()
    {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Enrolled Courses: " + enrolledCourses);
    }
}
