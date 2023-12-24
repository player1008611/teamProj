package com.teamProj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.vo.FairVo;
import com.teamProj.entity.vo.SchoolFairVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Interface for managing career fairs.
 * Extends the BaseMapper interface for CareerFair entities.
 */
@Mapper
public interface CareerFairDao extends BaseMapper<CareerFair> {
    /**
     * Retrieves a list of career fairs.
     *
     * @return List of FairVo representing career fairs
     */
    List<FairVo> queryCareerFair();

    /**
     * Queries a specific school fair based on criteria.
     *
     * @param page      Page object for pagination
     * @param title     Title of the fair
     * @param schoolId  ID of the school
     * @return          Page containing queried SchoolFairVo results
     */
    IPage<SchoolFairVo> queryFair(Page<SchoolFairVo> page, String title, Integer schoolId);
}
