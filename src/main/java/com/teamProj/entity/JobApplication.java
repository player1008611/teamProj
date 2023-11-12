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
public class JobApplication {
    @TableId(type = IdType.AUTO)
    int applicationId;

    int resumeId;

    int studentId;

    int recruitmentId;

    Timestamp applicationTime;

    Timestamp approvalTime;

    Character status;

    String rejectionReason;
}
