package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Resume;
import com.teamProj.entity.vo.StudentResumeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResumeDao extends BaseMapper<Resume> {
    List<StudentResumeVo> queryResume(@Param("userId") Integer userId);
}
