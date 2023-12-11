package com.teamProj.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInterviewVo {
    String studentName;

    String schoolName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp creationTime;

    String selfDescription;

    String careerObjective;

    String educationExperience;

    String internshipExperience;

    String projectExperience;

    String certificates;

    String skills;

    byte[] imageByte;

    Integer interviewId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp dateTime;

    String position;

    Character status;

    String principal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp time;
}
