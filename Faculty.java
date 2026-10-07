package com.college;
import jakarta.persistence.*;

@Entity
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    private String name;
    private int age;
    private String department;
    private String designation;
    private int id; 

    public Faculty() {
    }

    public Faculty(String name, int age, String department, String designation, int id) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.designation = designation;
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
