package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.Student;
import com.teamProj.entity.vo.AdminStudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface StudentDao extends BaseMapper<Student> {
    IPage<AdminStudentVo> queryStudent(Page<AdminStudentVo> page
            , @Param("name") String name
            , @Param("schoolName") String schoolName
            , @Param("status") Character status);

}
