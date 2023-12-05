package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.vo.FairVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CareerFairDao extends BaseMapper<CareerFair> {
    List<FairVo> queryCareerFair();
}
