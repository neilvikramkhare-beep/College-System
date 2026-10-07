package com.college;

public class Timetable {
    public String courseName;
    int courseId;
    String facultyName;
    String day;
    void getdata(String cName, int cId, String fName, String d)
    {
        this.courseName = cName;
        this.courseId = cId;
        this.facultyName = fName;
        this.day = d;
    }
    void showdata()
    {
        System.out.println("Course Name:"+courseName);
        System.out.println("Course Id:"+courseId);
        System.out.println("Faculty Name:"+facultyName);
        System.out.println("Day:"+day);
    }
}
