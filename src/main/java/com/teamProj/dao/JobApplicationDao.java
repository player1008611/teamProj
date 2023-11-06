package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobApplicationDao extends BaseMapper<JobApplication> {
}
