package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.entity.vo.AdminStudentVo;
import com.teamProj.service.SchoolService;
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
import java.util.concurrent.TimeUnit;

@Service
public class SchoolImpl implements SchoolService {

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;

    @Resource
    private UserDao userDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private CollegeDao collegeDao;
    @Resource
    private MajorDao majorDao;

    public HttpResult schoolLogin(String account, String password) {
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
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        return HttpResult.success(map, "登录成功");
    }

    public HttpResult schoolLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult queryStudent(String name, Character status, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_id", schoolId);
        String schoolName = schoolDao.selectOne(schoolQueryWrapper).getSchoolName();
        Page<AdminStudentVo> page = new Page<>(current, size);
        return HttpResult.success(studentDao.queryStudent(page, name, schoolName, status), "查询成功");
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
    public HttpResult queryCollege(String name, Integer current, Integer size) {
        Page<College> page = new Page<>(current, size);
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("college_name", name);
        return HttpResult.success(collegeDao.selectPage(page, queryWrapper), "查询成功");
    }

    @Override
    public HttpResult createCollege(String name) {
        College college = new College();
        college.setCollegeName(name);
        if (collegeDao.insert(college) > 0) {
            return HttpResult.success(college, "创建成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult deleteCollege(Integer collegeId) {
        if (collegeDao.deleteById(collegeId) > 0) {
            return HttpResult.success(collegeId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult editCollege(Integer collegeId, String name) {
        UpdateWrapper<College> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("college_id", collegeId).set("college_name", name);
        if (collegeDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(collegeId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult queryMajor(String name, Integer current, Integer size) {
        Page<Major> page = new Page<>(current, size);
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("major_name", name);
        return HttpResult.success(majorDao.selectPage(page, queryWrapper), "查询成功");
    }

    @Override
    public HttpResult createMajor(String name, Integer collegeId) {
        Major major = new Major();
        major.setMajorName(name);
        major.setCollegeId(collegeId);
        if (majorDao.insert(major) > 0) {
            return HttpResult.success(major, "创建成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult deleteMajor(Integer majorId) {
        if (majorDao.deleteById(majorId) > 0) {
            return HttpResult.success(majorId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult editMajor(Integer majorId, String name) {
        UpdateWrapper<Major> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("major_id", majorId).set("major_name", name);
        if (majorDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(majorId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }


}
