package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itextpdf.io.image.ImageDataFactory;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private MarkedRecruitmentInfoDao markedRecruitmentInfoDao;
    @Resource
    private JobApplicationDao jobApplicationDao;

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
    public HttpResult createResume(String account, MultipartFile imageFile, String selfDescription, String careerObjective,
                                   String educationExperience, String InternshipExperience, String projectExperience,
                                   String certificates, String skills, String resumeName, byte[] attachPDF) {

        QueryWrapper<User> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("account", account);
        User user = userDao.selectOne(queryWrapper0);
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        FileInputStream fis;
        Image image = null;
        if(imageFile!=null&&!imageFile.equals(new File(""))) {
            byte[] imageByte = new byte[0];

            try {
                imageByte = imageFile.getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            image = new Image(ImageDataFactory.create(imageByte));
        }

        byte[] resumePdf;
        if (attachPDF != null&&!Arrays.equals(attachPDF, new byte[0])) {
            resumePdf = attachPDF;
        } else {
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

            try {
                resumePdf = makeResume(map);
            } catch (Exception e) {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
            }
        }
        Resume resume = new Resume(null, student.getStudentId(), resumePdf, timestamp, student.getName(), resumeName);
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
        queryWrapper1.eq("student_id", user.getUserId()).select("resume_id", "creation_time", "student_name", "resume_name");
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
    public HttpResult queryRecruitmentInfo(String queryInfo, String minSalary,String maxSalary, boolean mark) {
        if (mark) {
            QueryWrapper<MarkedRecruitmentInfo> queryWrapper = new QueryWrapper<>();
            if (!queryInfo.isEmpty()) {
                queryWrapper.like("company_name", queryInfo).or().like("job_title", queryInfo);
            }
            if (!minSalary.isEmpty()&&!maxSalary.isEmpty()) {
                queryWrapper.ge("min_salary", minSalary).le("max_salary", maxSalary);
            }
            queryWrapper.select("recruitment_id", "job_title", "company_name", "city", "min_salary","max_salary", "byword");
            return HttpResult.success(markedRecruitmentInfoDao.selectList(queryWrapper), "查询成功");
        } else {
            QueryWrapper<RecruitmentInfo> queryWrapper = new QueryWrapper<>();
            if (!queryInfo.isEmpty()) {
                queryWrapper.like("company_name", queryInfo).or().like("job_title", queryInfo);
            }
            if (!minSalary.isEmpty()&&!maxSalary.isEmpty()) {
                queryWrapper.ge("min_salary", minSalary).le("max_salary", maxSalary);
            }
            queryWrapper.select("recruitment_id", "job_title", "company_name", "city", "min_salary","max_salary", "byword");
            return HttpResult.success(recruitmentInfoDao.selectList(queryWrapper), "查询成功");
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

    @Override
    public HttpResult createJobApplication(String account, Integer recruitmentInfoId, Integer resumeId) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setRecruitmentId(recruitmentInfoId);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        jobApplication.setStudentId(userDao.selectOne(queryWrapper).getUserId());
        jobApplication.setResumeId(resumeId);
        jobApplication.setApplicationTime(new Timestamp(System.currentTimeMillis()));
        if (jobApplicationDao.insert(jobApplication) > 0) {
            return HttpResult.success(jobApplication, "创建成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public HttpResult queryJobApplicationDetail(Integer applicationId) {
        QueryWrapper<JobApplication> queryWrapper0 = new QueryWrapper<>();
        JobApplication jobApplication = jobApplicationDao.selectOne(queryWrapper0.eq("application_id", applicationId));
        QueryWrapper<RecruitmentInfo> queryWrapper1 = new QueryWrapper<>();
        RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(queryWrapper1.eq("recruitment_id", jobApplication.getRecruitmentId()));
        QueryWrapper<Resume> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("resume_id", jobApplication.getResumeId());
        Map<String, String> map = new HashMap<>();
        map.put("投报职位", recruitmentInfo.getJobTitle());
        map.put("投报日期", new SimpleDateFormat("yyyy-MM-dd").format(jobApplication.getApplicationTime()));
        map.put("投报公司名", recruitmentInfo.getCompanyName());
        String status = "" + jobApplication.getStatus();
        if (status.equals("1")) {
            status = "未审核";
        } else if (status.equals("2")) {
            status = "已安排面试";
        } else if (status.equals("3")) {
            status = "未通过";
        }
        map.put("简历情况", status);
        map.put("薪资情况", recruitmentInfo.getMinSalary() + "-" + recruitmentInfo.getMaxSalary());
        map.put("投递简历名", resumeDao.selectOne(queryWrapper2).getResumeName());
        return HttpResult.success(map, "查询成功");
    }

    @Override
    public HttpResult queryJobApplication(String account) {
        QueryWrapper<User> queryWrapper0 = new QueryWrapper<>();
        queryWrapper0.eq("account", account);
        User user = userDao.selectOne(queryWrapper0);
        QueryWrapper<JobApplication> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        return HttpResult.success(jobApplicationDao.selectList(queryWrapper1), "查询成功");
    }

    @Override
    public HttpResult deleteJobApplication(Integer applicationId) {
        QueryWrapper<JobApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        if (jobApplicationDao.delete(queryWrapper) > 0) {
            return HttpResult.success(null, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }
}
