package com.teamProj.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class JobApplicationRecruitmentVo {
    Integer applicationId;
    String jobTitle;
    String time;
    String companyName;

}
