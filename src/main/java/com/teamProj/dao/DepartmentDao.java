package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * Interface for managing Departments.
 * Extends the BaseMapper interface for Department entities.
 */
@Mapper
public interface DepartmentDao extends BaseMapper<Department> {
}
