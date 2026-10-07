package com.college;
import jakarta.persistence.*;

@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    String name;
    int age;
    String enrolledCourses;
    
    public Student() {
    }

    public Student(String name, int age, String enrolledCourses) {
        this.name = name;
        this.age = age;
        this.enrolledCourses = enrolledCourses;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(String enrolledCourses) { this.enrolledCourses = enrolledCourses; }
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
