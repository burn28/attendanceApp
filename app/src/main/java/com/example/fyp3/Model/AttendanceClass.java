package com.example.fyp3.Model;

import java.util.ArrayList;
import java.util.List;

public class AttendanceClass {

    private String courseId;
    private String courseTitle;
    private String studentId;
    private String studentName;
    private String status;
    private List<String> weeks = new ArrayList<>();
    private Integer totalWeek = 0;
    private Integer percentage = 100;


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<String> getWeek() {
        return weeks;
    }

    public void setWeeks(List<String> weeks){
        this.weeks = weeks;
    }

    public void setWeek(String week) {
        this.weeks.add(week);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalWeek() {
        return totalWeek;
    }

    public void setTotalWeek(Integer totalWeek) {
        this.totalWeek = totalWeek;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }
}
