package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.vo.MarkRecruitmentVo;
import com.teamProj.entity.vo.RecruitmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Interface for managing RecruitmentInfo.
 * Extends the BaseMapper interface for RecruitmentInfo entities.
 */
@Mapper
public interface RecruitmentInfoDao extends BaseMapper<RecruitmentInfo> {
    /**
     * Queries marked recruitment based on specified criteria for a user.
     *
     * @param queryInfo     Query information
     * @param maxSalary     Maximum salary
     * @param minSalary     Minimum salary
     * @param userId        ID of the user
     * @return              List of MarkRecruitmentVo representing marked recruitment
     */
    List<MarkRecruitmentVo> queryMarkedRecruitment(
            @Param("queryInfo") String queryInfo,
            @Param("maxSalary") String maxSalary,
            @Param("minSalary") String minSalary,
            @Param("userId") Integer userId);

    /**
     * Queries recruitment information based on specified criteria for a user.
     *
     * @param queryInfo     Query information
     * @param maxSalary     Maximum salary
     * @param minSalary     Minimum salary
     * @param userId        ID of the user
     * @return              List of RecruitmentVo representing recruitment information
     */
    List<RecruitmentVo> queryRecruitmentInfo(
            @Param("queryInfo") String queryInfo,
            @Param("maxSalary") String maxSalary,
            @Param("minSalary") String minSalary,
            @Param("userId") Integer userId);
}
