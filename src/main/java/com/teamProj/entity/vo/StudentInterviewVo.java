package com.teamProj.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentInterviewVo {
    String dateTime;

    String position;

    char mark;

    char state;

    String companyName;
    String jobTitle;
}
