package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class RecruitmentInfo {
    @TableId(type = IdType.AUTO)
    int recruitmentId;
    int userId;
    int enterpriseId;
    String jobTitle;
    String salaryRange;
    String jobDescription;
    String department;
    String companyName;
    String city;
    Character status;
    Timestamp submissionTime;
    Timestamp approvalTime;
    String rejectionReason;
    int recruitNum;
    int recruitedNum;

    @Override
    public String toString() {
        return "RecruitmentInfo{" +
                "recruitmentId=" + recruitmentId +
                ", userId=" + userId +
                ", enterpriseId=" + enterpriseId +
                ", jobTitle='" + jobTitle + '\'' +
                ", salaryRange='" + salaryRange + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", department='" + department + '\'' +
                ", companyName='" + companyName + '\'' +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", submissionTime=" + submissionTime +
                ", approvalTime=" + approvalTime +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", recruitNum=" + recruitNum +
                ", recruitedNum=" + recruitedNum +
                '}';
    }

    public int getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(int recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Timestamp getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Timestamp submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Timestamp getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Timestamp approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public int getRecruitNum() {
        return recruitNum;
    }

    public void setRecruitNum(int recruitNum) {
        this.recruitNum = recruitNum;
    }

    public int getRecruitedNum() {
        return recruitedNum;
    }

    public void setRecruitedNum(int recruitedNum) {
        this.recruitedNum = recruitedNum;
    }
}
