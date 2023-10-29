package com.teamproj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamproj.domain.Administrator;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdministratorDao extends BaseMapper<Administrator> {
    @Insert(value = "insert into administrator(admin_id,name,login_account,password) values('test','test','test','test');")
    public void addAdmin();
}
