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
public class InterviewInfo {
    @TableId(type = IdType.AUTO)
    int interviewId;

    int studentId;

    int applicationId;

    Timestamp dateTime;

    String position;

    Character mark;

    Character state;
}
