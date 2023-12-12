package com.teamProj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class College {
    Integer collegeId;
    String collegeName;
    Integer schoolId;
    Integer studentNum;
}
