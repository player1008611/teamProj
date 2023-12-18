package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @TableId(type = IdType.AUTO)
    Integer messageId;
    @TableField(value = "`from`")
    Integer from;
    @TableField(value = "`to`")
    Integer to;
    String title;
    String content;
    String time;
    String status;
    String type;
    String enterpriseDel;
    String studentDel;
}
