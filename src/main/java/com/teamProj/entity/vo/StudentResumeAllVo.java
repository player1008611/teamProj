package com.teamProj.entity.vo;

import com.teamProj.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class StudentResumeAllVo {
    Resume resume;
    String name;
    String gender;
    String phoneNumber;
}
