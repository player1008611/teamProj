package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.vo.RecruitmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecruitmentInfoDao extends BaseMapper<RecruitmentInfo> {
    List<RecruitmentInfo> queryMarkedRecruitment(@Param("queryInfo") String queryInfo,
                                                 @Param("maxSalary") String maxSalary,
                                                 @Param("minSalary") String minSalary,
                                                 @Param("userId") Integer userId
    );

    List<RecruitmentVo> queryRecruitmentInfo(@Param("queryInfo") String queryInfo,
                                             @Param("maxSalary") String maxSalary,
                                             @Param("minSalary") String minSalary,
                                             @Param("userId") Integer userId
    );
}
