package com.teamProj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUser {
    Integer userId;

    Integer enterpriseId;

    String name;

    String age;

    String birthday;

    String gender;

    String graduation_school;

    String tel;

    String userStatus;
}
