package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.InterviewInfo;
import com.teamProj.entity.vo.StudentInterviewVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Interface for managing InterviewInfo.
 * Extends the BaseMapper interface for InterviewInfo entities.
 */
@Mapper
public interface InterviewInfoDao extends BaseMapper<InterviewInfo> {

    /**
     * Query interview info list.
     *
     * @param queryInfo the query info
     * @param userId    the user id
     * @return the list
     */
    List<StudentInterviewVo> queryInterviewInfo(String queryInfo, Integer userId);
}
