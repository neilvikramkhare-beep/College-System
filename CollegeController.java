package com.college;

import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;

@RestController
@CrossOrigin
public class CollegeController {
    
    private Map<String, List<Map<String, Object>>> dataStore = new HashMap<>();

    public CollegeController() {
        String[] keys = {"colleges", "students", "courses", "faculties", "admissions", "attendances", "examinations", "reviews", "syllabuses", "timetables"};
        for (String key : keys) {
            dataStore.put(key, new ArrayList<>());
        }
        
        // Initial data
        Map<String, Object> student = new HashMap<>();
        student.put("name", "Alice Johnson");
        student.put("age", 19);
        student.put("enrolledCourses", "Data Structures");
        dataStore.get("students").add(student);
        
        Map<String, Object> course = new HashMap<>();
        course.put("courseName", "Data Structures");
        course.put("courseCode", "CS-204");
        course.put("credits", 4);
        dataStore.get("courses").add(course);
        
        Map<String, Object> faculty = new HashMap<>();
        faculty.put("name", "Dr. John Doe");
        faculty.put("age", 45);
        faculty.put("department", "Computer Science");
        faculty.put("designation", "Professor");
        dataStore.get("faculties").add(faculty);

        Map<String, Object> college = new HashMap<>();
        college.put("name", "Northbridge College");
        college.put("location", "London");
        college.put("establishedYear", 1987);
        dataStore.get("colleges").add(college);
    }

    @GetMapping(value = "/", produces = "text/html")
    public String home() throws IOException {
        String path = System.getProperty("user.dir") + "/index.html";
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @GetMapping("/api/{type}")
    public List<Map<String, Object>> getAll(@PathVariable String type) {
        return dataStore.getOrDefault(type, new ArrayList<>());
    }

    @PostMapping("/api/{type}")
    public Map<String, Object> create(@PathVariable String type, @RequestBody Map<String, Object> payload) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent(type, k -> new ArrayList<>());
        list.add(payload);
        return payload;
    }

    @PostMapping(value = "/api/colleges/operation", produces = "text/plain")
    public String operation(@RequestBody Map<String, String> payload) {
        String op = payload.get("operation");
        String val = payload.get("value");
        return "Executed " + op + " with value " + val;
    }
}
