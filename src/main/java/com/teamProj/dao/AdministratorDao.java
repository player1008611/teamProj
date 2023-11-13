package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Administrator;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AdministratorDao extends BaseMapper<Administrator> {
}
