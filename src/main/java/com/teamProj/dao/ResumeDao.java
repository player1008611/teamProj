package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeDao extends BaseMapper<Resume> {
}
