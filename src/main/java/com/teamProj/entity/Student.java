package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class Student {
    @TableId(type = IdType.AUTO)
    int studentId;
    String studentAccount;
    int schoolId;
    String name;
    String phoneNumber;
    Character userStatus;
    Timestamp creationTime;
    String studentPassword;
    Character gender;
    String wechat;
    String qq;
    int collegeId;
    int majorId;
    String byword;

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentAccount='" + studentAccount + '\'' +
                ", schoolId=" + schoolId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userStatus=" + userStatus +
                ", creationTime=" + creationTime +
                ", studentPassword='" + studentPassword + '\'' +
                ", gender=" + gender +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", collegeId=" + collegeId +
                ", majorId=" + majorId +
                ", byword='" + byword + '\'' +
                '}';
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Character getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Character userStatus) {
        this.userStatus = userStatus;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getByword() {
        return byword;
    }

    public void setByword(String byword) {
        this.byword = byword;
    }
}
