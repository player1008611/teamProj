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
    Integer studentId;

    Integer schoolId;

    String name;

    String phoneNumber;

    Character userStatus;

    Timestamp creationTime;

    Character gender;

    String wechat;

    String qq;

    Integer collegeId;

    Integer majorId;

    String byword;
}
