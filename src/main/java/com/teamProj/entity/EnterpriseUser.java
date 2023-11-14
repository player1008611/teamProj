package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUser {
    @TableId(type = IdType.AUTO)
    String userId;

    String enterpriseId;

    String name;

    String age;

    String birthday;

    String gender;

    String graduation_school;

    String tel;
}
