package com.teamProj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Major {
    Integer majorId;
    String majorName;
    Integer collegeId;
    Integer schoolId;
    Integer studentNum;
}
