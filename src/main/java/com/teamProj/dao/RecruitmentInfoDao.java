package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.RecruitmentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecruitmentInfoDao extends BaseMapper<RecruitmentInfo> {
    List<RecruitmentInfo> queryMarkedRecruitment(@Param("queryInfo") String queryInfo,
                                                 @Param("maxSalary") String maxSalary,
                                                 @Param("minSalary") String minSalary
    );
}
