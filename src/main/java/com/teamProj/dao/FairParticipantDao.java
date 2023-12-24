package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.FairParticipant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing FairParticipants.
 * Extends the BaseMapper interface for FairParticipant entities.
 */
@Mapper
public interface FairParticipantDao extends BaseMapper<FairParticipant> {
}
