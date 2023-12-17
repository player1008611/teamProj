package com.teamProj.entity.vo;

import lombok.Data;

@Data
public class SchoolApplicationDataVo {
    Integer studentId;
    Integer applicationId;
    Integer schoolId;
    String collegeName;
    String majorName;
    String enterpriseName;
    String city;
    String status;
}
