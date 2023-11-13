package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentInfo {
    @TableId(type = IdType.AUTO)
    Integer recruitmentId;

    Integer userId;

    Integer enterpriseId;

    String jobTitle;

    String salaryRange;

    String jobDescription;

    String department;

    String companyName;

    String city;

    Character status;

    Timestamp submissionTime;

    Timestamp approvalTime;

    String rejectionReason;

    Integer recruitNum;

    Integer recruitedNum;

    String byword;
}
