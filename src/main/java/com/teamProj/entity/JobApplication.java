package com.teamProj.entity;

import java.sql.Timestamp;

public class JobApplication {
    int applicationId;
    int resumeId;
    int studentId;
    int opportunityId;
    Timestamp applicationTime;
    Timestamp approvalTime;
    Character status;
    String rejectionReason;

    @Override
    public String toString() {
        return "JobApplication{" +
                "applicationId=" + applicationId +
                ", resumeId=" + resumeId +
                ", studentId=" + studentId +
                ", opportunityId=" + opportunityId +
                ", applicationTime=" + applicationTime +
                ", approvalTime=" + approvalTime +
                ", status=" + status +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getResumeId() {
        return resumeId;
    }

    public void setResumeId(int resumeId) {
        this.resumeId = resumeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(int opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Timestamp getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Timestamp applicationTime) {
        this.applicationTime = applicationTime;
    }

    public Timestamp getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Timestamp approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
