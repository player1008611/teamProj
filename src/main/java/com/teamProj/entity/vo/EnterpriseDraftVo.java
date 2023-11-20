package com.teamProj.entity.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class EnterpriseDraftVo {
    Timestamp editTime;

    String draftName;

    Timestamp submissionTime;

    String departmentName;
}
