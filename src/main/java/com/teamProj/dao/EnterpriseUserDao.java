package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.vo.EnterpriseDraftVo;
import com.teamProj.entity.vo.EnterpriseFairVo;
import com.teamProj.entity.vo.EnterpriseInfoVo;
import com.teamProj.entity.vo.EnterpriseInterviewVo;
import com.teamProj.entity.vo.EnterpriseJobApplicationVo;
import com.teamProj.entity.vo.EnterpriseRecruitmentVo;
import com.teamProj.entity.vo.EnterpriseSentMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface EnterpriseUserDao extends BaseMapper<EnterpriseUser> {
    List<EnterpriseDraftVo> queryDraft(@Param("userId") Integer userId);

    IPage<EnterpriseJobApplicationVo> queryJobApplication(Page<EnterpriseJobApplicationVo> page
            , @Param("schoolName") String schoolName
            , @Param("departmentName") String departmentName
            , @Param("code") Integer code
            , @Param("userId") Integer userId);

    IPage<EnterpriseRecruitmentVo> queryRecruitmentInfo(Page<EnterpriseRecruitmentVo> page
            , @Param("userId") Integer userId
            , @Param("city") String city
            , @Param("maxSalary") Integer maxSalary
            , @Param("departmentName") String departmentName
            , @Param("statusNum") Integer statusNum
    );

    IPage<EnterpriseFairVo> queryFair(Page<EnterpriseFairVo> page
            , @Param("host") String host
            , @Param("location") String location
            , @Param("schoolName") String schoolName
            , @Param("date") Timestamp date
            , @Param("code") Integer code
            , @Param("userId") Integer userId);

    EnterpriseInfoVo queryInfo(@Param("userId") Integer userId);

    IPage<EnterpriseInterviewVo> queryInterview(Page<EnterpriseInterviewVo> page
            , @Param("userId") Integer userId
            , @Param("date") Timestamp date
            , @Param("school") String school
            , @Param("code") Integer code);

    IPage<EnterpriseSentMessageVo> querySentMessage(Page<EnterpriseSentMessageVo> page, @Param("userId") Integer userId);
}
