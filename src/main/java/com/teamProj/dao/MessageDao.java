package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Message;
import com.teamProj.entity.vo.StudentMessageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Interface for managing Messages.
 * Extends the BaseMapper interface for Message entities.
 */
@Mapper
public interface MessageDao extends BaseMapper<Message> {
    List<StudentMessageVo> queryMessageList(Integer userId, Integer messageId, String queryInfo);
}
