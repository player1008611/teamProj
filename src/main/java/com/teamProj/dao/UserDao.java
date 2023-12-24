package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Users.
 * Extends the BaseMapper interface for User entities.
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
}
