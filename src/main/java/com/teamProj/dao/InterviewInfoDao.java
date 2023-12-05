package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.InterviewInfo;
import com.teamProj.entity.vo.StudentInterviewVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterviewInfoDao extends BaseMapper<InterviewInfo> {
    List<StudentInterviewVo> queryInterviewInfo(String queryInfo,Integer userId);
}
