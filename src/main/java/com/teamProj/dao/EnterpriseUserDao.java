package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.vo.EnterpriseDraftVo;
import com.teamProj.entity.vo.EnterpriseJobApplicationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EnterpriseUserDao extends BaseMapper<EnterpriseUser> {
    EnterpriseDraftVo queryDraft(@Param("userId") Integer userId);

    IPage<EnterpriseJobApplicationVo> queryJobApplication(Page<EnterpriseJobApplicationVo> page
            , @Param("schoolName") String schoolName
            , @Param("departmentName") String departmentName
            , @Param("userId") Integer userId);
}
