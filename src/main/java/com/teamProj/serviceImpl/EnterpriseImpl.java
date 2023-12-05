package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.CareerFairDao;
import com.teamProj.dao.DepartmentDao;
import com.teamProj.dao.DraftDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.EnterpriseUserDao;
import com.teamProj.dao.InterviewInfoDao;
import com.teamProj.dao.JobApplicationDao;
import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.dao.ResumeDao;
import com.teamProj.dao.SchoolDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.dao.UserDao;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.Department;
import com.teamProj.entity.Draft;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.InterviewInfo;
import com.teamProj.entity.JobApplication;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.Resume;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.entity.User;
import com.teamProj.entity.vo.EnterpriseFairVo;
import com.teamProj.entity.vo.EnterpriseJobApplicationVo;
import com.teamProj.entity.vo.EnterpriseRecruitmentVo;
import com.teamProj.entity.vo.StudentResumeAllVo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.EmailVerification;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
public class EnterpriseImpl implements EnterpriseService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private RedisCache redisCache;
    @Resource
    private EnterpriseDao enterpriseDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private DepartmentDao departmentDao;

    @Resource
    private RecruitmentInfoDao recruitmentInfoDao;

    @Resource
    private DraftDao draftDao;

    @Resource
    private JobApplicationDao jobApplicationDao;

    @Resource
    private UserDao userDao;

    @Resource
    private InterviewInfoDao interviewInfoDao;

    @Resource
    private ResumeDao resumeDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private CareerFairDao careerFairDao;

    @Override
    public HttpResult schoolList() {
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.select("DISTINCT school_name");
        return HttpResult.success(schoolDao.selectList(schoolQueryWrapper), "查询成功");
    }

    @Override
    public HttpResult cityList() {
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.select("DISTINCT city");
        return HttpResult.success(recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper), "查询成功");
    }

    @Override
    public HttpResult hostList() {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.select("DISTINCT host");
        return HttpResult.success(careerFairDao.selectList(careerFairQueryWrapper), "查询成功");
    }

    @Override
    public HttpResult locationList() {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.select("DISTINCT location");
        return HttpResult.success(careerFairDao.selectList(careerFairQueryWrapper), "查询成功");
    }

    @Override
    public HttpResult enterpriseLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authentication)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (!loginUser.getPermissions().get(0).equals("enterprise")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        return HttpResult.success(map, "登录成功");
    }

    @Override
    public HttpResult enterpriseLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(userId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult enterpriseChangePassword(String newPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("password", bCryptPasswordEncoder.encode(newPassword));
        try {
            userDao.update(null, updateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(null, "修改成功");
    }

    @Override
    public HttpResult createNewDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (Objects.isNull(enterpriseUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        Department department = new Department(null, departmentName, enterpriseUser.getEnterpriseId());
        try {
            departmentDao.insert(department);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(departmentName, "创建成功");
    }

    @Override
    public HttpResult queryDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();

        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (Objects.isNull(enterpriseUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("enterprise_id", enterpriseUser.getEnterpriseId());
        if (!Objects.isNull(departmentName) && !departmentName.equals("")) {
            departmentQueryWrapper.like("name", departmentName);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        List<Department> departmentList = departmentDao.selectList(departmentQueryWrapper);
        for (Department department : departmentList) {
            QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
            recruitmentInfoQueryWrapper.ne("status", "0").orderByDesc("submission_time").eq("department_id", department.getDepartmentId());
            List<RecruitmentInfo> recruitmentInfoList = recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("departmentName", department.getName());
            map.put("recruitmentInfoNum", recruitmentInfoList.size());
            List<RecruitmentInfo> recruitmentInfos = new ArrayList<>();
            for (RecruitmentInfo recruitmentInfo : recruitmentInfoList) {
                if (recruitmentInfos.size() < 4) {
                    recruitmentInfos.add(recruitmentInfo);
                } else {
                    break;
                }
            }
            map.put("latest", recruitmentInfos);
            list.add(map);
        }
        return HttpResult.success(list, "查询成功");
    }

    @Override
    public HttpResult deleteDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();

        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (Objects.isNull(enterpriseUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        UpdateWrapper<Department> departmentUpdateWrapper = new UpdateWrapper<>();
        departmentUpdateWrapper.eq("enterprise_id", enterpriseUser.getEnterpriseId());
        if (!Objects.isNull(departmentName) && !departmentName.equals("")) {
            departmentUpdateWrapper.like("name", departmentName);
        }
        try {
            departmentDao.delete(departmentUpdateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "删除失败");
        }
        return HttpResult.success(departmentName, "删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult createNewRecruitmentInfo(String draftName, String departmentName, RecruitmentInfo recruitmentInfo) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();


        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (Objects.isNull(enterpriseUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("name", departmentName);
        departmentQueryWrapper.eq("enterprise_id", enterpriseUser.getEnterpriseId());
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        if (Objects.isNull(department)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
        enterpriseQueryWrapper.eq("enterprise_id", department.getEnterpriseId());
        Enterprise enterprise = enterpriseDao.selectOne(enterpriseQueryWrapper);
        if (Objects.isNull(enterprise)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        recruitmentInfo.setUserId(userId);
        recruitmentInfo.setEnterpriseId(department.getEnterpriseId());
        recruitmentInfo.setDepartmentId(department.getDepartmentId());
        recruitmentInfo.setCompanyName(enterprise.getEnterpriseName());
        recruitmentInfo.setRecruitedNum(0);

        Date date = new java.sql.Date(new java.util.Date().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        recruitmentInfo.setSubmissionTime(Timestamp.valueOf(formatter.format(date)));

        try {
            recruitmentInfoDao.insert(recruitmentInfo);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "聘文已存在");
        }

        if (recruitmentInfo.getStatus().equals('0')) {
            QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
            recruitmentInfoQueryWrapper.eq("user_id", userId).eq("job_title", recruitmentInfo.getJobTitle());
            RecruitmentInfo recruitmentInfo1 = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
            Draft draft = new Draft(null, userId, recruitmentInfo1.getRecruitmentId(), Timestamp.valueOf(formatter.format(date)), draftName);
            try {
                draftDao.insert(draft);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "草稿已存在");
            }
        }
        return HttpResult.success(null, "添加成功");
    }

    @Override
    public HttpResult queryRecruitmentInfo(String city, String salaryRange, String departmentName, Integer statusNum, Integer current) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        if (statusNum < 0 || statusNum > 7) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "状态码错误");
        }
        Integer maxSalary = null;
        if (!Objects.isNull(salaryRange) && !salaryRange.isEmpty()) {
            maxSalary = Integer.parseInt(salaryRange.substring(0, salaryRange.length() - 4));
        }
        Page<EnterpriseRecruitmentVo> page = new Page<>(current, 6);
        return HttpResult.success(enterpriseUserDao.queryRecruitmentInfo(page, userId, city, maxSalary, departmentName, statusNum), "查询成功");
    }

    @Override
    public HttpResult queryRecruitmentInfoByDraft(String draftName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<Draft> draftQueryWrapper = new QueryWrapper<>();
        draftQueryWrapper.eq("draft_name", draftName).eq("user_id", userId);
        Draft draft = draftDao.selectOne(draftQueryWrapper);
        if (Objects.isNull(draft)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.select("job_title", "job_description", "company_name", "city", "status", "submission_time", "approval_time", "recruit_num", "recruited_num", "byword", "job_duties", "min_salary", "max_salary");
        recruitmentInfoQueryWrapper.eq("recruitment_id", draft.getRecruitmentId());
        return HttpResult.success(recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper), "查询成功");
    }


    @Override
    public HttpResult deleteRecruitmentInfo(String departmentName, String jobTitle) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();

        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (Objects.isNull(enterpriseUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("enterprise_id", enterpriseUser.getEnterpriseId());
        departmentQueryWrapper.eq("name", departmentName);
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        if (Objects.isNull(department)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("enterprise_id", department.getEnterpriseId());
        recruitmentInfoQueryWrapper.eq("department_id", department.getDepartmentId());
        recruitmentInfoQueryWrapper.eq("job_title", jobTitle);

        try {
            recruitmentInfoDao.delete(recruitmentInfoQueryWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(jobTitle, "删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult updateDraft(String oldDraftName, String newDraftName, RecruitmentInfo recruitmentInfo) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<Draft> draftQueryWrapper = new QueryWrapper<>();
        draftQueryWrapper.eq("user_id", userId).eq("draft_name", oldDraftName);
        Draft draft = draftDao.selectOne(draftQueryWrapper);
        UpdateWrapper<RecruitmentInfo> recruitmentInfoUpdateWrapper = new UpdateWrapper<>();
        recruitmentInfoUpdateWrapper.eq("recruitment_id", draft.getRecruitmentId());

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        draft.setDraftName(newDraftName);
        draft.setEditTime(Timestamp.valueOf(formatter.format(date)));
        try {
            recruitmentInfoDao.update(recruitmentInfo, recruitmentInfoUpdateWrapper);
            if (recruitmentInfo.getStatus().equals('0')) {
                draftDao.update(draft, draftQueryWrapper);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(oldDraftName, "编辑成功");
    }

    @Override
    public HttpResult queryDraft() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryDraft(userId), "查询成功");
    }

    @Override
    public HttpResult deleteDraft(String draftName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<Draft> draftQueryWrapper = new QueryWrapper<>();
        draftQueryWrapper.eq("user_id", userId).eq("draft_name", draftName);
        Draft draft = draftDao.selectOne(draftQueryWrapper);
        if (Objects.isNull(draft)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("recruitment_id", draft.getRecruitmentId());
        try {
            recruitmentInfoDao.delete(recruitmentInfoQueryWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(draftName, "删除成功");
    }

    @Override
    public HttpResult queryJobApplication(String schoolName, String departmentName, Integer current) {
        Page<EnterpriseJobApplicationVo> page = new Page<>(current, 7);
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryJobApplication(page, schoolName, departmentName, userId), "查询成功");
    }

    @Override
    public HttpResult queryResume(Integer jobApplicationId) {
        QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
        jobApplicationQueryWrapper.eq("application_id", jobApplicationId);
        JobApplication jobApplication = jobApplicationDao.selectOne(jobApplicationQueryWrapper);
        if (Objects.isNull(jobApplication)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<Resume> resumeQueryWrapper = new QueryWrapper<>();
        resumeQueryWrapper.eq("resume_id", jobApplication.getResumeId());
        Resume resume = resumeDao.selectOne(resumeQueryWrapper);
        if (Objects.isNull(resume)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.eq("student_id", resume.getStudentId());
        Student student = studentDao.selectOne(studentQueryWrapper);
        StudentResumeAllVo data = new StudentResumeAllVo(resume, student.getName(), student.getGender(), student.getPhoneNumber());
        return HttpResult.success(data, "查询成功");
    }

    @Override
    public HttpResult deleteJobApplication(String studentAccount, String departmentName, String jobTitle) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("name", departmentName);
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        if (Objects.isNull(department)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", studentAccount);
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("department_id", department.getDepartmentId())
                .eq("job_title", jobTitle)
                .eq("user_id", userId);
        RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
        if (Objects.isNull(recruitmentInfo)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>();
        jobApplicationUpdateWrapper.eq("recruitment_id", recruitmentInfo.getRecruitmentId())
                .eq("student_id", user.getUserId());
        try {
            jobApplicationDao.delete(jobApplicationUpdateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(jobTitle, "删除成功");
    }

    @Override
    public HttpResult disagreeJobApplication(Integer id, String rejectReason) {
        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>();
        jobApplicationUpdateWrapper.set("status", "1")
                .set("rejection_reason", rejectReason)
                .eq("application_id", id);
        if (jobApplicationDao.update(null, jobApplicationUpdateWrapper) > 0) {
            return HttpResult.success();
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "操作失败");
    }

    @Override
    public HttpResult agreeJobApplication(Integer id, String date, String position) {
        QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
        jobApplicationQueryWrapper.eq("application_id", id).eq("status", "0");
        JobApplication jobApplication = jobApplicationDao.selectOne(jobApplicationQueryWrapper);
        if (Objects.isNull(jobApplication)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "信息不存在或已审核");
        }
        InterviewInfo interviewInfo = new InterviewInfo(null, jobApplication.getStudentId(), jobApplication.getApplicationId(), date, position, '0', '0');
        try {
            interviewInfoDao.insert(interviewInfo);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "新建面试失败");
        }
        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>(jobApplication);
        jobApplicationUpdateWrapper.set("status", '2');
        jobApplicationDao.update(null, jobApplicationUpdateWrapper);
        //给学生发邮箱提醒
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("recruitment_id", jobApplication.getRecruitmentId());
        RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("department_id", recruitmentInfo.getDepartmentId());
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", jobApplication.getStudentId());
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "学生信息不存在");
        }
        EmailVerification emailVerification = new EmailVerification();
        String content = "您于" + jobApplication.getApplicationTime()
                + "向" + recruitmentInfo.getCompanyName()
                + "的" + department.getName()
                + "申请" + recruitmentInfo.getJobTitle()
                + "的简历已经通过，面试将在" + date
                + "于" + interviewInfo.getPosition()
                + "进行,请按时参加。";
        if (!emailVerification.interviewReminderService(user.getAccount(), content)) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "邮件发送失败");
        }
        return HttpResult.success();
    }

    @Override
    public HttpResult createFair(String title, String content, Timestamp startTime, Timestamp endTime, String location, String host, String schoolName) {
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_name", schoolName);
        School school = schoolDao.selectOne(schoolQueryWrapper);
        if (Objects.isNull(school)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "学校不存在");
        }
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        CareerFair careerFair = new CareerFair(null, userId, school.getSchoolId(), enterpriseUser.getEnterpriseId(), startTime, endTime, location, host, "0", title, content);
        try {
            careerFairDao.insert(careerFair);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "创建失败");
        }
        return HttpResult.success(title, "创建成功");
    }

    @Override
    public HttpResult queryFair(String host, String location, String schoolName, Timestamp date, Integer code, Integer current) {
        Page<EnterpriseFairVo> page = new Page<>(current, 7);
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryFair(page, host, location, schoolName, date, code, userId), "查询成功");
    }

    @Override
    public HttpResult updateFair(Integer id, String title, String content, Timestamp startTime, Timestamp endTime, String location, String host, String schoolName) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        UpdateWrapper<CareerFair> careerFairUpdateWrapper = new UpdateWrapper<>();
        careerFairUpdateWrapper.eq("fair_id", id);
        CareerFair careerFair = careerFairDao.selectOne(careerFairUpdateWrapper);
        if (careerFair.getUserId() != userId) {
            return HttpResult.failure(ResultCodeEnum.REDIRECT, "无权修改");
        }
        if (!careerFair.getStatus().equals("0")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "稿件已通过，请联系管理员修改");
        }
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_name", schoolName);
        School school = schoolDao.selectOne(schoolQueryWrapper);
        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        try {
            careerFairDao.update(new CareerFair(null, null, school.getSchoolId(), null, startTime, endTime, location, host, null, title, content), careerFairUpdateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
        }
        return HttpResult.success(null, "修改成功");
    }

    @Override
    public HttpResult deleteFair(Integer id) {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.eq("fair_id", id);
        if (careerFairDao.delete(careerFairQueryWrapper) > 0) {
            return HttpResult.success(id, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "对象不存在");
    }

    @Override
    public HttpResult queryInfo() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryInfo(userId), "查询成功");
    }

    @Override
    public HttpResult updateInfo(MultipartFile avatar, String name, java.sql.Date birthday, Integer age, String gender, String graduationSchool) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        UpdateWrapper<EnterpriseUser> enterpriseUserUpdateWrapper = new UpdateWrapper<>();
        enterpriseUserUpdateWrapper.eq("user_id", userId);
        try {
            if (enterpriseUserDao.update(new EnterpriseUser(null, null, name, age, birthday, gender, graduationSchool, null, null, avatar.getBytes()), enterpriseUserUpdateWrapper) > 0) {
                return HttpResult.success();
            }
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
    }
}
