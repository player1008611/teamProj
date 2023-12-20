package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.vo.FairVo;
import com.teamProj.entity.vo.SchoolFairVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CareerFairDao extends BaseMapper<CareerFair> {
    List<FairVo> queryCareerFair();

    IPage<SchoolFairVo> queryFair(Page<SchoolFairVo> page, String title, Integer schoolId);
}
