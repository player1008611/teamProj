package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.JobApplication;
import com.teamProj.entity.vo.JobApplicationRecruitmentVo;
import com.teamProj.entity.vo.SchoolApplicationDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Interface for managing Job applications.
 * Extends the BaseMapper interface for JobApplication entities.
 */
@Mapper
public interface JobApplicationDao extends BaseMapper<JobApplication> {
    /**
     * Queries job applications related to a specific user.
     *
     * @param userId    ID of the user
     * @return          List of JobApplicationRecruitmentVo representing job applications
     */
    List<JobApplicationRecruitmentVo> queryJobApplication(@Param("userId") Integer userId);

    /**
     * Queries application data related to a specific school.
     *
     * @param schoolId  ID of the school
     * @return          List of SchoolApplicationDataVo representing school application data
     */
    List<SchoolApplicationDataVo> querySchoolApplicationData(@Param("schoolId") Integer schoolId);
}
