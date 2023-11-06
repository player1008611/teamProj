package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.School;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchoolDao extends BaseMapper<School> {
}
