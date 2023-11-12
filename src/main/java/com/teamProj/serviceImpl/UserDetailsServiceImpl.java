package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.UserDao;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", username);
        User user = userDao.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> list = new ArrayList<>();
        list.add(user.getPermission());
        return new LoginUser(user, list);
    }
}
