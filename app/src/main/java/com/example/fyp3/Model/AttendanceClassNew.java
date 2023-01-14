package com.example.fyp3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttendanceClassNew {
    private String courseId;
    private String courseTitle;
    private String studentId;
    private String studentName;
    private HashMap<String,String> status = new HashMap<>();
    private List<String> weeks = new ArrayList<>();
    private Integer totalWeek = 0;
    private Integer percentage = 100;

    public AttendanceClassNew() {
    }

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

    public String getStatus(String key) {
        return this.status.get(key);
    }

    public void setStatus(String week, String status) {
        this.status.put(week,status);
//        this.status = status;
    }

    public HashMap<String, String> getStatusMap(){
        return status;
    }

    public List<String> getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks.add(weeks);
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
