package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Draft;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Drafts.
 * Extends the BaseMapper interface for Draft entities.
 */
@Mapper
public interface DraftDao extends BaseMapper<Draft> {
}
