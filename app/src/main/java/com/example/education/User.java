package com.example.education;

public class User {
    private String id;
    private String name;
    private String email;
    private  String phone;
    private String school;
    private  String status;
    private  String role;

    public User(){
    }

    public User(String id,String name,String email,String phone,String school,String status,String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.status = status;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSchool() { return school; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSchoolName(String school) { this.school = school; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}
