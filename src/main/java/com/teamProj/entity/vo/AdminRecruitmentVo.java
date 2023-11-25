package com.teamProj.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AdminRecruitmentVo {
    String departmentName;

    String jobTitle;

    String jobDescription;

    String companyName;

    String city;

    Character status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp submissionTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp approvalTime;

    String rejectionReason;

    Integer recruitNum;

    Integer recruitedNum;

    String byword;

    String jobDuties;

    Integer minSalary;

    Integer maxSalary;
}
