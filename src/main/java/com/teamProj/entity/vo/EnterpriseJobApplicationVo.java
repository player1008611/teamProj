package com.teamProj.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class EnterpriseJobApplicationVo {
    Integer id;

    Character status;

    String studentName;

    String schoolName;

    String departmentName;

    String jobTitle;

    String studentAccount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    Timestamp applicationTime;
}
