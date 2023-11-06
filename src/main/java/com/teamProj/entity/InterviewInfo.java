package com.teamProj.entity;

import java.sql.Timestamp;

public class InterviewInfo {
    int interviewId;
    int studentId;
    int applicationId;
    Timestamp dateTime;
    String position;
    Character mark;
    Character state;

    @Override
    public String toString() {
        return "InterviewInfo{" +
                "interviewId=" + interviewId +
                ", studentId=" + studentId +
                ", applicationId=" + applicationId +
                ", dateTime=" + dateTime +
                ", position='" + position + '\'' +
                ", mark=" + mark +
                ", state=" + state +
                '}';
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Character getMark() {
        return mark;
    }

    public void setMark(Character mark) {
        this.mark = mark;
    }

    public Character getState() {
        return state;
    }

    public void setState(Character state) {
        this.state = state;
    }
}
