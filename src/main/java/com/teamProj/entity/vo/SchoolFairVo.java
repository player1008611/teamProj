package com.teamProj.entity.vo;

import lombok.Data;

@Data
public class SchoolFairVo {
    Integer fairId;
    Integer userId;
    Integer schoolId;
    Integer enterpriseId;
    String schoolName;
    String enterpriseName;
    String startTime;
    String endTime;
    String location;
    String host;
    String status;
    String title;
    String content;
}
