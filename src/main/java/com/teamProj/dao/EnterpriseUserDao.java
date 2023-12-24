package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * Interface for managing enterprise users.
 * Extends the BaseMapper interface for EnterpriseUser entities.
 */
@Mapper
public interface EnterpriseUserDao extends BaseMapper<EnterpriseUser> {

    /**
     * Retrieves drafts related to a specific user.
     *
     * @param userId    ID of the user
     * @return          List of EnterpriseDraftVo representing drafts
     */
    List<EnterpriseDraftVo> queryDraft(@Param("userId") Integer userId);

    /**
     * Queries job applications based on specified criteria.
     *
     * @param page              Page object for pagination
     * @param schoolName        Name of the school
     * @param departmentName    Name of the department
     * @param code              Job application code
     * @param userId            ID of the user
     * @return                  Page containing queried EnterpriseJobApplicationVo results
     */
    IPage<EnterpriseJobApplicationVo> queryJobApplication(
            Page<EnterpriseJobApplicationVo> page,
            @Param("schoolName") String schoolName,
            @Param("departmentName") String departmentName,
            @Param("code") Integer code,
            @Param("userId") Integer userId);

    /**
     * Queries recruitment information based on specified criteria.
     *
     * @param page              Page object for pagination
     * @param userId            ID of the user
     * @param city              City of recruitment
     * @param maxSalary         Maximum salary
     * @param departmentName    Name of the department
     * @param statusNum         Status number
     * @return                  Page containing queried EnterpriseRecruitmentVo results
     */
    IPage<EnterpriseRecruitmentVo> queryRecruitmentInfo(
            Page<EnterpriseRecruitmentVo> page,
            @Param("userId") Integer userId,
            @Param("city") String city,
            @Param("maxSalary") Integer maxSalary,
            @Param("departmentName") String departmentName,
            @Param("statusNum") Integer statusNum);

    /**
     * Queries fairs based on specified criteria.
     *
     * @param page          Page object for pagination
     * @param host          Host of the fair
     * @param location      Location of the fair
     * @param schoolName    Name of the school
     * @param date          Date of the fair
     * @param code          Fair code
     * @param userId        ID of the user
     * @return              Page containing queried EnterpriseFairVo results
     */
    IPage<EnterpriseFairVo> queryFair(
            Page<EnterpriseFairVo> page,
            @Param("host") String host,
            @Param("location") String location,
            @Param("schoolName") String schoolName,
            @Param("date") Timestamp date,
            @Param("code") Integer code,
            @Param("userId") Integer userId);

    /**
     * Retrieves information related to a specific enterprise user.
     *
     * @param userId    ID of the user
     * @return          EnterpriseInfoVo representing user information
     */
    EnterpriseInfoVo queryInfo(@Param("userId") Integer userId);

    /**
     * Queries interviews based on specified criteria.
     *
     * @param page      Page object for pagination
     * @param userId    ID of the user
     * @param date      Date of the interview
     * @param school    School involved in the interview
     * @param code      Interview code
     * @return          Page containing queried EnterpriseInterviewVo results
     */
    IPage<EnterpriseInterviewVo> queryInterview(
            Page<EnterpriseInterviewVo> page,
            @Param("userId") Integer userId,
            @Param("date") Timestamp date,
            @Param("school") String school,
            @Param("code") Integer code);

    /**
     * Queries messages sent by an enterprise user.
     *
     * @param page      Page object for pagination
     * @param userId    ID of the user
     * @return          Page containing queried EnterpriseSentMessageVo results
     */
    IPage<EnterpriseSentMessageVo> querySentMessage(
            Page<EnterpriseSentMessageVo> page,
            @Param("userId") Integer userId);
}
