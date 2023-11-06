package com.teamProj.entity;

public class FairParticipant {
    int fairId;
    int studentId;

    @Override
    public String toString() {
        return "FairParticipant{" +
                "fairId=" + fairId +
                ", studentId=" + studentId +
                '}';
    }

    public int getFairId() {
        return fairId;
    }

    public void setFairId(int fairId) {
        this.fairId = fairId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
