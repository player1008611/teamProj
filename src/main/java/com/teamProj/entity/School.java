package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    @TableId(type = IdType.AUTO)
    int schoolId;

    String schoolAccount;

    String schoolPassword;

    String tel;

    String principal;

    String schoolName;
}
