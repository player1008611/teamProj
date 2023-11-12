package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
