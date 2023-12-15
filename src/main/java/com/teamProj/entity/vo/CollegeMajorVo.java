package com.teamProj.entity.vo;

import com.teamProj.entity.Major;
import lombok.Data;

import java.util.List;

@Data
public class CollegeMajorVo {
    Integer collegeId;
    String collegeName;
    List<Major> majorList;
}
