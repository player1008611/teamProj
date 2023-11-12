package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.SchoolDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    private StudentDao studentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    public HttpResult administratorLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String adminId = String.valueOf(loginUser.getAdministrator().getAdminId());
        String jwt = JwtUtil.createJWT(adminId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(adminId, loginUser);
        return HttpResult.success(map, "登录成功");
    }

    public HttpResult administratorLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getAdministrator().getAdminId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult resetStudentPassword(String account) {
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_account", account).set("student_password", "123456");
        if (studentDao.update(null, updateWrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "重置成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult disableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 0);
        if (studentDao.update(null, wrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult enableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 1);
        if (studentDao.update(null, wrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult deleteStudentAccount(String account) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("student_account", account);
        Student deletedStudent = studentDao.selectOne(wrapper);
        if (studentDao.delete(wrapper) > 0) {
            Student result = new Student();
            result.setName(deletedStudent.getName());
            result.setStudentAccount(deletedStudent.getStudentAccount());
            return HttpResult.success(result, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult queryStudent(String name, String account, String status) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.eq("name", name);
        }
        if (account != null && !account.isEmpty()) {
            queryWrapper.eq("student_account", account);
        }
        queryWrapper.select("student_account", "name", "phone_number", "user_status", "creation_time").eq("user_status", status);
        return HttpResult.success(studentDao.selectList(queryWrapper), "查询成功");
    }

    @Override
    public HttpResult resetSchoolPassword(String account) {
        UpdateWrapper<School> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("school_account", account).set("school_password", "123456");
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("school_account", "school_name").eq("school_account", account);
        if (schoolDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(schoolDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult disableSchoolAccount(String account) {
        return null;
    }

    @Override
    public HttpResult enableSchoolAccount(String account) {
        return null;
    }

    @Override
    public HttpResult deleteSchoolAccount(String account) {
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        wrapper.eq("school_account", account);
        School deletedSchool = schoolDao.selectOne(wrapper);
        if (schoolDao.delete(wrapper) > 0) {
            School result = new School();
            result.setSchoolName(deletedSchool.getSchoolName());
            result.setSchoolAccount(deletedSchool.getSchoolAccount());
            return HttpResult.success(result, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal) {
        School newSchool = new School();
        newSchool.setSchoolAccount(account);
        newSchool.setSchoolPassword(password);
        newSchool.setSchoolName(schoolName);
        newSchool.setTel(telephone);
        newSchool.setPrincipal(principal);
        QueryWrapper<School> queryWrapper = new QueryWrapper<School>();
        queryWrapper.select("school_account", "school_name").eq("school_account", account);
        if (schoolDao.insert(newSchool) > 0) {
            return HttpResult.success(schoolDao.selectOne(queryWrapper), "添加成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public HttpResult querySchool(String schoolName, String account, String status) {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        if (schoolName != null && !schoolName.isEmpty()) {
            queryWrapper.eq("school_name", schoolName);
        }
        if (account != null && !account.isEmpty()) {
            queryWrapper.eq("school_account", account);
        }
        queryWrapper.select("school_name", "principal", "tel", "state", "school_account").eq("user_status", status);
        return HttpResult.success(schoolDao.selectList(queryWrapper), "查询成功");
    }
}
