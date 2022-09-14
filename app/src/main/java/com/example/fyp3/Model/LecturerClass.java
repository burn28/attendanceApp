package com.example.fyp3.Model;

public class LecturerClass {

    private String courseId;
    private String title;
    private String day;
    private String startTime;
    private String endTime;
    private String location;
    private String lecturerId;
    private String type;

    public LecturerClass(String id, String title, String day, String startTime, String endTime, String location, String lecturerId, String type) {
        this.courseId = id;
        this.title = title;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.lecturerId = lecturerId;
        this.type = type;
    }

    public LecturerClass(){

    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }
}
