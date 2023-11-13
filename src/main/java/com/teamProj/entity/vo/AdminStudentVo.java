package com.teamProj.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AdminStudentVo {
    String account;

    String name;

    String phoneNumber;

    Character userStatus;

    Timestamp creationTime;

    String schoolName;
}
