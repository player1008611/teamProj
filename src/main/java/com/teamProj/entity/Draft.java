package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Draft {

    @TableId(type = IdType.AUTO)
    Integer draftId;

    Integer recruitmentId;

    Timestamp editTime;

    String draftName;
}
