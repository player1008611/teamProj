package com.teamProj.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AdminStudentVo {
    String account;

    String name;

    String phoneNumber;

    Character userStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp creationTime;

    String schoolName;
}
