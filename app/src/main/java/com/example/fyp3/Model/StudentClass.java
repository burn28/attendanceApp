package com.example.fyp3.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentClass {

    private String courseId;
    private String title;
    private String id;
    private String name;
    private String startTime;
    private String endTime;
    private String day;
    private String location;
    private List<String> weeks = new ArrayList<>();
    private Integer totalWeek = 0;
    private Integer percentage = 100;
    private HashMap<String,String> status = new HashMap<>();

    public StudentClass(String courseId, String title, Integer totalWeek, Integer percentage, String day, String location) {
        this.courseId = courseId;
        this.title = title;
        this.totalWeek = totalWeek;
        this.day = day;
        this.percentage = percentage;
        this.location = location;
    }

    public StudentClass() {

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus(String key) {
        return this.status.get(key);
    }

    public void setStatus(String week, String status) {
        this.status.put(week,status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public List<String> getWeeks() {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
