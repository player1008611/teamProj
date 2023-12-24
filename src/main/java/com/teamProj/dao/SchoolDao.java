package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Schools.
 * Extends the BaseMapper interface for School entities.
 */
@Mapper
public interface SchoolDao extends BaseMapper<School> {
}
