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
public class Resume {
    @TableId(type = IdType.AUTO)
    Integer resumeId;

    Integer studentId;

    byte[] attachmentLink;

    Timestamp creationTime;

    String studentName;

    String resumeName;
}
