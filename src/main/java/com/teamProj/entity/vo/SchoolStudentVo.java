package com.teamProj.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class SchoolStudentVo {
    String account;

    String name;

    String phoneNumber;

    Character userStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp creationTime;

    String schoolName;

    Integer majorId;

    String majorName;
}
