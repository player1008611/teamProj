package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.JobApplication;
import com.teamProj.entity.vo.JobApplicationRecruitmentVo;
import com.teamProj.entity.vo.SchoolApplicationDataVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobApplicationDao extends BaseMapper<JobApplication> {
    List<JobApplicationRecruitmentVo> queryJobApplication(@Param("userId") Integer userId);
    List<SchoolApplicationDataVo> querySchoolApplicationData(@Param("schoolId") Integer schoolId);
}
