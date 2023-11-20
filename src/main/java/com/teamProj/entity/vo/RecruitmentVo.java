package com.teamProj.entity.vo;

import lombok.Data;

@Data
public class RecruitmentVo {
    Integer recruitmentId;
    String jobTitle;
    String companyName;
    String city;
    String minSalary;
    String maxSalary;
    String byword;
    Integer state;
}