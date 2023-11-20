package com.teamProj.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class EnterpriseRecruitmentVo {
    String name;

    String jobTitle;

    String jobDescription;

    String companyName;

    String city;

    Character status;

    Timestamp submissionTime;

    Timestamp approvalTime;

    String rejectionReason;

    Integer recruitNum;

    Integer recruitedNum;

    String byword;

    String jobDuties;

    Integer minSalary;

    Integer maxSalary;
}
