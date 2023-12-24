package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Announcements' data.
 * Extends the BaseMapper interface for Announcement entities.
 */
@Mapper
public interface AnnouncementDao extends BaseMapper<Announcement> {
}
