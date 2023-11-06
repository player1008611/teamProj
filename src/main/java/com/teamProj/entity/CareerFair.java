package com.teamProj.entity;

import java.sql.Date;
import java.sql.Time;

public class CareerFair {
    int fairId;
    int schoolId;
    int enterpriseId;
    Date date;
    Time time;
    String location;
    String host;

    @Override
    public String toString() {
        return "Careerfair{" +
                "fairId=" + fairId +
                ", schoolId=" + schoolId +
                ", enterpriseId=" + enterpriseId +
                ", date=" + date +
                ", time=" + time +
                ", location='" + location + '\'' +
                ", host='" + host + '\'' +
                '}';
    }

    public int getFairId() {
        return fairId;
    }

    public void setFairId(int fairId) {
        this.fairId = fairId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
