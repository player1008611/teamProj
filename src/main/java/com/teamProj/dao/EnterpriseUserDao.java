package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.vo.EnterpriseDraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnterpriseUserDao extends BaseMapper<EnterpriseUser> {
    EnterpriseDraftVo queryDraft(@Param("userId") Integer userId);
}
