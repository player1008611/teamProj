package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.Student;
import com.teamProj.entity.vo.SchoolStudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Interface for managing Students.
 * Extends the BaseMapper interface for Student entities.
 */
@Mapper
public interface StudentDao extends BaseMapper<Student> {

    /**
     * Queries students with a specific major from a school based on specified criteria.
     *
     * @param page        Page object for pagination
     * @param name        Name of the student
     * @param schoolName  Name of the school
     * @param majorId     ID of the major
     * @param status      Status of the student
     * @return            Page containing queried SchoolStudentVo results with major information
     */
    IPage<SchoolStudentVo> queryStudentWithMajor(
            Page<SchoolStudentVo> page,
            @Param("name") String name,
            @Param("schoolName") String schoolName,
            @Param("majorId") Integer majorId,
            @Param("status") Character status);
}
