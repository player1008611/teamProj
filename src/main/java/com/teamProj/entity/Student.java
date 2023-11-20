package com.teamProj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    Integer studentId;

    Integer schoolId;

    String name;

    String phoneNumber;

    String userStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp creationTime;

    String gender;

    String wechat;

    String qq;

    Integer collegeId;

    Integer majorId;

    String byword;
}
