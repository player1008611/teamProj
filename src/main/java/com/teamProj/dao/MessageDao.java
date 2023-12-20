package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<Message> {
    List<Message> queryMessageList(Integer userId, Integer messageId, String queryInfo);
}
