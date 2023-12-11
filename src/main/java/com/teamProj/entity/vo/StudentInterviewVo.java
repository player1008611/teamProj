package com.teamProj.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentInterviewVo {
    String dateTime;

    String position;

    char mark;

    char status;

    String companyName;
    String jobTitle;
}
