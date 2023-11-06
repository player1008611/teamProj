package com.teamProj.entity;

public class MarkedRecruitmentInfo {
    int studentId;
    int recruitmentId;

    @Override
    public String toString() {
        return "MarkedRecruitmentInfo{" +
                "studentId=" + studentId +
                ", recruitmentId=" + recruitmentId +
                '}';
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(int recruitmentId) {
        this.recruitmentId = recruitmentId;
    }
}
