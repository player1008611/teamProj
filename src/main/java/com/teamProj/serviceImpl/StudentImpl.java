package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.service.StudentService;
import com.teamProj.utils.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialBlob;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    private SchoolDao schoolDao;
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
    public HttpResult studentRegister(String account, String password, String schoolName, String name, String phoneNumber) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = new User();
        if (userDao.selectOne(queryWrapper) != null) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "账号已存在");
        } else {
            user.setAccount(account);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setPermission("student");
            userDao.insert(user);
        }
        QueryWrapper<School> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("school_name", schoolName);
        School school = schoolDao.selectOne(queryWrapper1);
        Student student = new Student(userDao.selectOne(queryWrapper).getUserId(), school.getSchoolId(), name, phoneNumber, "1", new Timestamp(System.currentTimeMillis()), null, null, null, null, null, null);
        if (studentDao.insert(student) > 0) {
            return HttpResult.success(student, "注册成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }

    }

    @Override
    public HttpResult verification(String email){
        EmailVerification emailVerification = new EmailVerification();
        String captcha = emailVerification.verificationService(email);
        return HttpResult.success(captcha,"发送成功");
    }

    @Override
    public HttpResult createResume(String account, MultipartFile imageFile, String selfDescription, String careerObjective,
                                   String educationExperience, String InternshipExperience, String projectExperience,
                                   String certificates, String skills, String resumeName) {

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        Image image = null;
//
//        if (imageFile != null && !imageFile.isEmpty()) {
//            byte[] imageByte;
//            try {
//                imageByte = imageFile.getBytes();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            //image = new Image(ImageDataFactory.create(imageByte));
//        }
//
//        byte[] resumePdf;
//        if (attachPDF != null && !attachPDF.isEmpty()) {
//            try {
//                resumePdf = attachPDF.getBytes();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", student.getName());
//            map.put("image", image);
//            map.put("gender", student.getGender());
//            map.put("phone_number", student.getPhoneNumber());
//            map.put("self_description", selfDescription);
//            map.put("career_objective", careerObjective);
//            map.put("education_experience", educationExperience);
//            map.put("internship_experience", InternshipExperience);
//            map.put("project_experience", projectExperience);
//            map.put("certificates", certificates);
//            map.put("skills", skills);
//
//            try {
//                resumePdf = makeResume(map);
//            } catch (Exception e) {
//                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
//            }
//        }


        Resume resume;
        try {
            resume = new Resume(null, student.getStudentId(), timestamp, resumeName,selfDescription,careerObjective,educationExperience,InternshipExperience,projectExperience,certificates,skills,imageFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resumeDao.insert(resume) > 0) {
            return HttpResult.success(resume, "创建成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public HttpResult queryResume() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        return HttpResult.success(resumeDao.queryResume(user.getUserId()), "查询成功");
    }

    @Override
    public HttpResult deleteResume(Integer resumeId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<Resume> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId()).eq("resume_id", resumeId);
        if (resumeDao.delete(queryWrapper1) > 0) {
            return HttpResult.success(null, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult queryResumeDetail(Integer resumeId)  {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resume_id", resumeId);
        return HttpResult.success(resumeDao.selectOne(queryWrapper), "查询成功");
//        File file = new File("./temp/resume.pdf");
//        OutputStream output = null;
//        try {
//            output = new FileOutputStream(file);
//            BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
//            bufferedOutput.write(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        FileOutputStream stream = new FileOutputStream(File);

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
        student.setGender((String) map.get("gender"));
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
    public HttpResult queryRecruitmentInfo(String account, String queryInfo, String minSalary, String maxSalary, boolean mark) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        if (mark) {
            return HttpResult.success(recruitmentInfoDao.queryMarkedRecruitment(queryInfo, maxSalary, minSalary, user.getUserId()), "查询成功");
        } else {
//            QueryWrapper<RecruitmentInfo> queryWrapper = new QueryWrapper<>();
//            if (queryInfo != null && !queryInfo.isEmpty()) {
//                queryWrapper.like("company_name", queryInfo).or().like("job_title", queryInfo);
//            }
//            if (minSalary != null && maxSalary != null && !minSalary.isEmpty() && !maxSalary.isEmpty()) {
//                queryWrapper.ge("min_salary", minSalary).le("max_salary", maxSalary);
//            }
//            queryWrapper.select("recruitment_id", "job_title", "company_name", "city", "min_salary", "max_salary", "byword");
//            return HttpResult.success(recruitmentInfoDao.selectList(queryWrapper), "查询成功");
            return HttpResult.success(recruitmentInfoDao.queryRecruitmentInfo(queryInfo,maxSalary,minSalary, user.getUserId()), "查询成功");
        }
    }




    @Override
    public HttpResult markRecruitmentInfo(String account, Integer RecruitmentInfoId) {
        QueryWrapper<MarkedRecruitmentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("recruitment_id", RecruitmentInfoId).eq("student_id",
                userDao.selectOne(new QueryWrapper<User>().eq("account", account)).getUserId());
        if(markedRecruitmentInfoDao.selectOne(queryWrapper)!=null){
            markedRecruitmentInfoDao.delete(queryWrapper);
            return HttpResult.success(null, "取消收藏成功");
        }
        else {
        MarkedRecruitmentInfo mri = new MarkedRecruitmentInfo();
        mri.setRecruitmentId(RecruitmentInfoId);
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("account", account);
        mri.setStudentId(userDao.selectOne(queryWrapper1).getUserId());
        if (markedRecruitmentInfoDao.insert(mri) > 0) {
            return HttpResult.success(mri, "收藏成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
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
        status = switch (status) {
            case "1" -> "未审核";
            case "2" -> "已安排面试";
            case "3" -> "未通过";
            default -> status;
        };
        map.put("简历情况", status);
        map.put("薪资情况", recruitmentInfo.getMinSalary() + "-" + recruitmentInfo.getMaxSalary());
        map.put("投递简历名", resumeDao.selectOne(queryWrapper2).getResumeName());
        return HttpResult.success(map, "查询成功");
    }

    @Override
    public HttpResult queryJobApplication(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        return HttpResult.success(jobApplicationDao.queryJobApplication(user.getUserId()), "查询成功");
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
