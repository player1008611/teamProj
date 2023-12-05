package com.teamProj.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInfoVo {
    String account;

    String enterpriseName;

    String name;

    Integer age;

    Date birthday;

    String gender;

    String graduationSchool;

    String tel;

    byte avatar;
}
