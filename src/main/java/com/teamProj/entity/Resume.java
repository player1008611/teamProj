package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    @TableId(type = IdType.AUTO)
    int resumeId;

    int studentId;

    String selfDescription;

    String careerObjective;

    String educationHistory;

    String InternshipExperience;

    String projectExperience;

    String trainingExperience;

    String schoolActivities;

    String certificates;

    String professionalSkills;

    String skillsTags;

    String attachmentLink;
}
