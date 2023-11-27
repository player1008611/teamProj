package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    @TableId(type = IdType.AUTO)
    Integer resumeId;

    Integer studentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp creationTime;

    String resumeName;

    String selfDescription;

    String careerObjective;

    String educationExperience;

    String internshipExperience;

    String projectExperience;

    String certificates;

    String skills;

    byte[] imageByte;
}
