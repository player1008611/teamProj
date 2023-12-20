package com.teamProj.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class StudentMessageVo {
    @TableId(type = IdType.AUTO)
    Integer messageId;

    String from;
    String to;
    String title;
    String content;
    String time;
    Integer status;
}
