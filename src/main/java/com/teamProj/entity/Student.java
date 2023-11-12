package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @TableId(type = IdType.AUTO)
    int studentId;

    //String studentAccount;

    int schoolId;

    String name;

    String phoneNumber;

    Character userStatus;

    Timestamp creationTime;

    //String studentPassword;

    Character gender;

    String wechat;

    String qq;

    int collegeId;

    int majorId;

    String byword;
}
