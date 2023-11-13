package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.StudentDao;
import com.teamProj.dao.UserDao;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.Student;
import com.teamProj.entity.User;
import com.teamProj.service.StudentService;
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
public class StudentImpl implements StudentService {
    @Resource
    private StudentDao studentDao;
    @Resource
    private UserDao userDao;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;

    @Override
    public HttpResult studentLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if(!loginUser.getPermissions().get(0).equals("student")){
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser);
        return HttpResult.success(map, "登录成功");
    }
    public HttpResult studentLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int studentId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(studentId));
        return HttpResult.success(null, "用户注销");
    }


    @Override
    public HttpResult setStudentPassword(String account, String oldPassword, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_account", account);
        User user = userDao.selectOne(queryWrapper);
        if(user !=null){
            if(user.getPassword().equals(oldPassword)){
                user.setPassword(password);
                userDao.update(user,queryWrapper);
                queryWrapper.select("student_account");
                return HttpResult.success(userDao.selectOne(queryWrapper),"修改成功");
            }
            return HttpResult.success(userDao.selectOne(queryWrapper),"密码错误");
        }else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult createResume(String studentAccount, Map<String, Object> map) {
        return null;
    }

    @Override
    public HttpResult setStudentInfo(String account, Map<String, Object> map) {
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_account", account);
        User user = userDao.selectOne(queryWrapper1);
        int studentId=user.getUserId();
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_id", studentId);
        Student student = new Student();
        student.setName((String) map.get("name"));
        student.setPhoneNumber((String) map.get("phoneNumber"));
        student.setGender((Character) map.get("gender"));
        student.setWechat((String) map.get("wechat"));
        student.setQq((String) map.get("qq"));
        student.setCollegeId((Integer) map.get("collegeId"));
        student.setMajorId((Integer) map.get("majorId"));
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name").eq("student_id", studentId);
        if (studentDao.update(student, updateWrapper) > 0) {
            return HttpResult.success(studentDao.selectOne(queryWrapper), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }
}
