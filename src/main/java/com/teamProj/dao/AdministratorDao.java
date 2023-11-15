package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.vo.AdminEnterpriseUserVo;
import com.teamProj.entity.vo.AdminSchoolUserVo;
import com.teamProj.entity.vo.AdminStudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface AdministratorDao extends BaseMapper<Administrator> {
    IPage<AdminStudentVo> queryStudent(Page<AdminStudentVo> page
            , @Param("name") String name
            , @Param("schoolName") String schoolName
            , @Param("status") Character status);

    IPage<AdminEnterpriseUserVo> queryEnterpriseUser(Page<AdminEnterpriseUserVo> page
            , @Param("enterpriseName") String enterpriseName
            , @Param("name") String name);

    IPage<AdminSchoolUserVo> querySchoolUser(Page<AdminSchoolUserVo> page
            , @Param("principal") String principal
            , @Param("status") Character status);
}
