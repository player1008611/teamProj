package com.teamProj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    Integer schoolId;

    String tel;

    String principal;

    String schoolName;

    String status;
}
