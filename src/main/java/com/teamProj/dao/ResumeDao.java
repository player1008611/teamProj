package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Resume;
import com.teamProj.entity.vo.StudentResumeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Interface for managing Resumes.
 * Extends the BaseMapper interface for Resume entities.
 */
@Mapper
public interface ResumeDao extends BaseMapper<Resume> {

    /**
     * Queries a student's resume based on their user ID.
     *
     * @param userId    ID of the user
     * @return          List of StudentResumeVo representing the student's resume
     */
    List<StudentResumeVo> queryResume(@Param("userId") Integer userId);
}
