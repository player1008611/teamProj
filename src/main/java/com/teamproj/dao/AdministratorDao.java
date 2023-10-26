package com.teamproj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamproj.domain.Administrator;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdministratorDao extends BaseMapper<Administrator> {
}
