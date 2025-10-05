package com.example.education;

public class CourseModel {
    private String id;
    private String name;
    private String status;

    public CourseModel() {} // Firestore needs empty constructor

    public CourseModel(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
}
