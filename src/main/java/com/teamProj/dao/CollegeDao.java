package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.College;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing College fairs.
 * Extends the BaseMapper interface for College entities.
 */
@Mapper
public interface CollegeDao extends BaseMapper<College> {
}
