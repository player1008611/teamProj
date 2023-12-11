package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.College;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollegeDao extends BaseMapper<College> {
}
