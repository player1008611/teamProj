package com.teamProj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUser {
    Integer userId;

    Integer enterpriseId;

    String name;

    Integer age;

    Date birthday;

    String gender;

    String graduationSchool;

    String tel;

    String userStatus;

    byte[] avatar;
}
