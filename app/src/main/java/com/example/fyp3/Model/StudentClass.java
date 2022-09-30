package com.example.fyp3.Model;

import java.util.ArrayList;

public class StudentClass {

    private String courseId;
    private String title;
    //    private String studentId;
    private String week;
    private String day;
    //    private String status;
    private ArrayList<String> weeks = new ArrayList<>();
    private Integer totalWeek = 0;
    private Integer percentage = 100;

    public StudentClass(String courseId, String week, String title, Integer totalWeek, Integer percentage, String day) {
        this.courseId = courseId;
        this.week = week;
        this.title = title;
        this.totalWeek = totalWeek;
        this.day = day;
        this.percentage = percentage;
    }

    public StudentClass() {

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

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public ArrayList<String> getWeeks() {
        return weeks;
    }

    public void setWeeks(String week) {
        this.weeks.add(week);
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
