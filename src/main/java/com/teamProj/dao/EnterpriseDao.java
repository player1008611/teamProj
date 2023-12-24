package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Enterprises.
 * Extends the BaseMapper interface for Enterprise entities.
 */
@Mapper
public interface EnterpriseDao extends BaseMapper<Enterprise> {
}
