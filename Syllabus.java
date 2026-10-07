package com.college;
public class Syllabus {
    String[] subjects;
    Syllabus(String[] subjects) {
        this.subjects = subjects;
    }
    void getdata()
    {
        System.out.println("Enter the subjects:"+subjects);
    }
    void showdata()
    {
        System.out.println("Subjects:"+subjects);
    }
}