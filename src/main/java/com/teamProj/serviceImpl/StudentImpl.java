package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import net.bytebuddy.asm.Advice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class StudentImpl implements StudentService {
    @Resource
    private StudentDao studentDao;
    @Resource
    private UserDao userDao;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ResumeDao resumeDao;
    @Resource
    private RecruitmentInfoDao recruitmentInfoDao;
    @Resource
    private EnterpriseDao enterpriseDao;
    @Resource
    private MarkedRecruitmentInfoDao markedRecruitmentInfoDao;

    @Override
    public HttpResult studentLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginUser.getPermissions().get(0).equals("student")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
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
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        if (user != null) {
            if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
                userDao.update(user, queryWrapper);
                queryWrapper.select("account");
                return HttpResult.success(userDao.selectOne(queryWrapper), "修改成功");
            }
            return HttpResult.success(userDao.selectOne(queryWrapper), "密码错误");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult createResume(String studentAccount, Map<String, Object> map) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", studentAccount);
        User user = userDao.selectOne(queryWrapper);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(map.get("attachment_link")!=null){
            Resume resume= new Resume();
            resume.setAttachmentLink(((String) map.get("attachment_link")).getBytes());
            resume.setStudentId(user.getUserId());
            resume.setCreationTime(timestamp);
            if(resumeDao.insert(resume)>0){
                QueryWrapper<Resume> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("student_id", user.getUserId()).eq("creation_time", timestamp);
                resume = resumeDao.selectOne(queryWrapper1);
                return HttpResult.success(resume, "创建成功");
            }else{
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
            }
        } else{
//            byte[] resumeLink = makeResume(map);
//            Resume resume= new Resume();
//            resume.setAttachmentLink(resumeLink);

        }

        return null;
    }

    @Override
    public HttpResult setStudentInfo(String account, Map<String, Object> map) {
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("account", account);
        User user = userDao.selectOne(queryWrapper1);
        int studentId = user.getUserId();
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_id", studentId);
        Student student = new Student();
        student.setName((String) map.get("name"));
        student.setPhoneNumber((String) map.get("phoneNumber"));
        student.setGender((Character) map.get("gender"));
        student.setWechat((String) map.get("wechat"));
        student.setQq((String) map.get("qq"));
        student.setCollegeId((Integer) map.get("college_id"));
        student.setMajorId((Integer) map.get("major_id"));
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name").eq("student_id", studentId);
        if (studentDao.update(student, updateWrapper) > 0) {
            return HttpResult.success(studentDao.selectOne(queryWrapper), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult queryRecruitmentInfo(String enterpriseName) {
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enterprise_name", enterpriseName);
        Enterprise enterprise = enterpriseDao.selectOne(queryWrapper);
        if (enterprise != null) {
            QueryWrapper<RecruitmentInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("enterprise_id", enterprise.getEnterpriseId());
            return HttpResult.success(recruitmentInfoDao.selectList(queryWrapper1), "查询成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult markRecruitmentInfo(String account,Integer RecruitmentInfoId){
        MarkedRecruitmentInfo mri = new MarkedRecruitmentInfo();
        mri.setRecruitmentId(RecruitmentInfoId);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        mri.setStudentId(userDao.selectOne(queryWrapper).getUserId());
        if(markedRecruitmentInfoDao.insert(mri)>0){
            return HttpResult.success(mri,"收藏成功");
        }
        else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }
}
