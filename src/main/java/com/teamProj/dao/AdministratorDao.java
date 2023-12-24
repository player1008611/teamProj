package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Interface for managing administrators' data.
 * Extends the BaseMapper interface for Administrator entities.
 */
@Mapper
public interface AdministratorDao extends BaseMapper<Administrator> {

    /**
     * Queries students based on specified criteria.
     *
     * @param page        Page object for pagination
     * @param name        Name of the student
     * @param schoolName  Name of the student's school
     * @param status      Status of the student
     * @return            Page containing queried AdminStudentVo results
     */
    IPage<AdminStudentVo> queryStudent(
            Page<AdminStudentVo> page,
            @Param("name") String name,
            @Param("schoolName") String schoolName,
            @Param("status") Character status);

    /**
     * Queries enterprise users based on specified criteria.
     *
     * @param page            Page object for pagination
     * @param enterpriseName  Name of the enterprise
     * @param name            Name of the enterprise user
     * @return                Page containing queried AdminEnterpriseUserVo results
     */
    IPage<AdminEnterpriseUserVo> queryEnterpriseUser(
            Page<AdminEnterpriseUserVo> page,
            @Param("enterpriseName") String enterpriseName,
            @Param("name") String name);

    /**
     * Queries school users based on specified criteria.
     *
     * @param page        Page object for pagination
     * @param principal   Principal's name
     * @param status      Status of the school user
     * @return            Page containing queried AdminSchoolUserVo results
     */
    IPage<AdminSchoolUserVo> querySchoolUser(
            Page<AdminSchoolUserVo> page,
            @Param("principal") String principal,
            @Param("status") Character status);

    /**
     * Queries recruitment data based on specified criteria.
     *
     * @param page            Page object for pagination
     * @param companyName     Name of the company
     * @param departmentName  Name of the department
     * @param jobTitle        Title of the job position
     * @return                Page containing queried AdminRecruitmentVo results
     */
    IPage<AdminRecruitmentVo> queryRecruitment(
            Page<AdminRecruitmentVo> page,
            @Param("companyName") String companyName,
            @Param("departmentName") String departmentName,
            @Param("jobTitle") String jobTitle);

    /**
     * Queries announcements based on specified criteria.
     *
     * @param page        Page object for pagination
     * @param title       Title of the announcement
     * @param category    Category of the announcement
     * @return            Page containing queried AdminAnnouncementVo results
     */
    IPage<AdminAnnouncementVo> queryAnnouncement(
            Page<AdminAnnouncementVo> page,
            @Param("title") String title,
            @Param("category") String category);
}

