package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    AdministratorDao administratorDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<Administrator> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_account", username);
        Administrator administrator = administratorDao.selectOne(queryWrapper);
        if (Objects.isNull(administrator)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> list = new ArrayList<>(Arrays.asList("admin", "xxx"));
        return new LoginUser(administrator, list);
    }
}
