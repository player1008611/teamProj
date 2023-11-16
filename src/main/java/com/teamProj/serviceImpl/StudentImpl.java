package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itextpdf.layout.element.Image;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.service.StudentService;
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.teamProj.utils.MakeResume.makeResume;

@Service
public class StudentImpl implements StudentService {
    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private StudentDao studentDao;
    @Resource
    private UserDao userDao;
    @Resource
    private AuthenticationManager authenticationManager;
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
    public HttpResult createResume(String account, Image image, String selfDescription, String careerObjective,
                                   String educationExperience, String InternshipExperience, String projectExperience,
                                   String certificates, String skills, String resumeName) {
        QueryWrapper<User> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("account", account);
        User user = userDao.selectOne(queryWrapper0);
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("name", student.getName());
        map.put("image", image);
        map.put("gender", student.getGender());
        map.put("phone_number", student.getPhoneNumber());
        map.put("self_description", selfDescription);
        map.put("career_objective", careerObjective);
        map.put("education_experience", educationExperience);
        map.put("internship_experience", InternshipExperience);
        map.put("project_experience", projectExperience);
        map.put("certificates", certificates);
        map.put("skills", skills);
        byte[] resumePdf;
        try {
            resumePdf = makeResume(map);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        Resume resume = new Resume(null, student.getStudentId(), resumePdf, timestamp ,student.getName(),resumeName);
        if (resumeDao.insert(resume) > 0) {
            return HttpResult.success(resume, "创建成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public HttpResult queryResume(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        QueryWrapper<Resume> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId()).select("resume_id", "create_time","student_name","resume_name");
        return HttpResult.success(resumeDao.selectList(queryWrapper1), "查询成功");
    }

    @Override
    public HttpResult deleteResume(String account, Integer resumeId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        QueryWrapper<Resume> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId()).eq("resume_id", resumeId);
        if (resumeDao.delete(queryWrapper1) > 0) {
            return HttpResult.success(null, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
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
    public HttpResult markRecruitmentInfo(String account, Integer RecruitmentInfoId) {
        MarkedRecruitmentInfo mri = new MarkedRecruitmentInfo();
        mri.setRecruitmentId(RecruitmentInfoId);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        mri.setStudentId(userDao.selectOne(queryWrapper).getUserId());
        if (markedRecruitmentInfoDao.insert(mri) > 0) {
            return HttpResult.success(mri, "收藏成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }
}
