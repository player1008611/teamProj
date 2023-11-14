package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.EnterpriseUserDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.dao.UserDao;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.Student;
import com.teamProj.entity.User;
import com.teamProj.entity.vo.AdminEnterpriseUserVo;
import com.teamProj.entity.vo.AdminStudentVo;
import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserDao userDao;

    @Resource
    private AdministratorDao administratorDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private EnterpriseDao enterpriseDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    public HttpResult administratorLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginUser.getPermissions().get(0).equals("admin")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser);
        return HttpResult.success(map, "登录成功");
    }

    public HttpResult administratorLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult queryStudent(String name, String schoolName, Character status, Integer current, Integer size) {
        Page<AdminStudentVo> page = new Page<>(current, size);
        return HttpResult.success(administratorDao.queryStudent(page, name, schoolName, status), "查询成功");
    }

    @Override
    public HttpResult resetStudentPassword(String account) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).eq("permission", "student").set("password", bCryptPasswordEncoder.encode("123456"));
        if (userDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(account, "重置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult enableStudentAccount(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "student");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
        studentUpdateWrapper.eq("student_id", user.getUserId()).set("user_status", '1');
        if (studentDao.update(null, studentUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult disableStudentAccount(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "student");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
        studentUpdateWrapper.eq("student_id", user.getUserId()).set("user_status", '0');
        if (studentDao.update(null, studentUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult deleteStudentAccount(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "student");
        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult queryEnterprise(String name, Integer current, Integer size) {
        Page<Enterprise> page = new Page<>(current, size);
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("enterprise_name", "website");
        if (!Objects.isNull(name) && !name.isEmpty()) {
            queryWrapper.eq("enterprise_name", name);
        }
        return HttpResult.success(enterpriseDao.selectPage(page, queryWrapper), "查询成功");
    }

    @Override
    public HttpResult queryEnterpriseUser(String enterpriseName, String userName, Integer current, Integer size) {
        Page<AdminEnterpriseUserVo> page = new Page<>(current, size);
        return HttpResult.success(administratorDao.queryEnterpriseUser(page, enterpriseName, userName), "查询成功");
    }
}

