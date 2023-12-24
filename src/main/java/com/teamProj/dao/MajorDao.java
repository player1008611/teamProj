package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Major;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Majors.
 * Extends the BaseMapper interface for Major entities.
 */
@Mapper
public interface MajorDao extends BaseMapper<Major> {
}
