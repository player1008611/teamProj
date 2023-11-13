package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerFair {
    @TableId(type = IdType.AUTO)
    Integer fairId;

    Integer schoolId;

    Integer enterpriseId;

    Date date;

    Time time;

    String location;

    String host;
}
