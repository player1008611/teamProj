package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Student;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface StudentDao extends BaseMapper<Student> {
}
