package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    @TableId(type = IdType.AUTO)
    Integer resumeId;

    Integer studentId;

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
