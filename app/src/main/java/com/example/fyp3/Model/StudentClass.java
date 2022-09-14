package com.example.fyp3.Model;

public class StudentClass {

    private String courseId;
    private String title;
    private String studentId;
    private String week;
    private String status;

    public StudentClass(String courseId, String studentId, String week, String status) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.week = week;
        this.status = status;
    }

    public StudentClass(){

    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
