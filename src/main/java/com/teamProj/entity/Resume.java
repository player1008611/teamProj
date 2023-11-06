package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class Resume {
    @TableId(type = IdType.AUTO)
    int resumeId;
    int studentId;
    String selfDescription;
    String careerObjective;
    String educationHistory;
    String InternshipExperience;
    String projectExperience;
    String trainingExperience;
    String schoolActivities;
    String certificates;
    String professionalSkills;
    String skillsTags;
    String attachmentLink;

    @Override
    public String toString() {
        return "Resume{" +
                "resumeId=" + resumeId +
                ", studentId=" + studentId +
                ", selfDescription='" + selfDescription + '\'' +
                ", careerObjective='" + careerObjective + '\'' +
                ", educationHistory='" + educationHistory + '\'' +
                ", InternshipExperience='" + InternshipExperience + '\'' +
                ", projectExperience='" + projectExperience + '\'' +
                ", trainingExperience='" + trainingExperience + '\'' +
                ", schoolActivities='" + schoolActivities + '\'' +
                ", certificates='" + certificates + '\'' +
                ", professionalSkills='" + professionalSkills + '\'' +
                ", skillsTags='" + skillsTags + '\'' +
                ", attachmentLink='" + attachmentLink + '\'' +
                '}';
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

    public String getSelfDescription() {
        return selfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        this.selfDescription = selfDescription;
    }

    public String getCareerObjective() {
        return careerObjective;
    }

    public void setCareerObjective(String careerObjective) {
        this.careerObjective = careerObjective;
    }

    public String getEducationHistory() {
        return educationHistory;
    }

    public void setEducationHistory(String educationHistory) {
        this.educationHistory = educationHistory;
    }

    public String getInternshipExperience() {
        return InternshipExperience;
    }

    public void setInternshipExperience(String internshipExperience) {
        InternshipExperience = internshipExperience;
    }

    public String getProjectExperience() {
        return projectExperience;
    }

    public void setProjectExperience(String projectExperience) {
        this.projectExperience = projectExperience;
    }

    public String getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(String trainingExperience) {
        this.trainingExperience = trainingExperience;
    }

    public String getSchoolActivities() {
        return schoolActivities;
    }

    public void setSchoolActivities(String schoolActivities) {
        this.schoolActivities = schoolActivities;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public String getProfessionalSkills() {
        return professionalSkills;
    }

    public void setProfessionalSkills(String professionalSkills) {
        this.professionalSkills = professionalSkills;
    }

    public String getSkillsTags() {
        return skillsTags;
    }

    public void setSkillsTags(String skillsTags) {
        this.skillsTags = skillsTags;
    }

    public String getAttachmentLink() {
        return attachmentLink;
    }

    public void setAttachmentLink(String attachmentLink) {
        this.attachmentLink = attachmentLink;
    }
}
