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
import com.teamProj.dao.MessageDao;
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
import com.teamProj.entity.Message;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.Resume;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.entity.User;
import com.teamProj.entity.vo.EnterpriseFairVo;
import com.teamProj.entity.vo.EnterpriseInterviewVo;
import com.teamProj.entity.vo.EnterpriseJobApplicationVo;
import com.teamProj.entity.vo.EnterpriseRecruitmentVo;
import com.teamProj.entity.vo.EnterpriseSentMessageVo;
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
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
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

    @Resource
    private MessageDao messageDao;

    // 获取学校列表

    /**
     * Retrieves a list of distinct school names.
     *
     * @return HTTP result containing a list of distinct school names retrieved from the database.
     */
    @Override
    public HttpResult schoolList() {
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.select("DISTINCT school_name");
        return HttpResult.success(schoolDao.selectList(schoolQueryWrapper), "查询成功");
    }

    // 获取城市列表

    /**
     * Retrieves a list of distinct cities associated with recruitment information.
     *
     * @return HTTP result containing a list of distinct cities retrieved from the recruitment
     * information.
     */
    @Override
    public HttpResult cityList() {
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.select("DISTINCT city");
        return HttpResult.success(recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper), "查询成功");
    }

    // 获取宣讲会主持人列表

    /**
     * Retrieves a list of distinct hosts associated with career fairs.
     *
     * @return HTTP result containing a list of distinct hosts retrieved from the career fair data.
     */
    @Override
    public HttpResult hostList() {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.select("DISTINCT host");
        return HttpResult.success(careerFairDao.selectList(careerFairQueryWrapper), "查询成功");
    }

    // 获取宣讲会举办地列表

    /**
     * Retrieves a list of distinct locations where career fairs are held.
     *
     * @return HTTP result containing a list of distinct locations retrieved from the career fair
     * data.
     */
    @Override
    public HttpResult locationList() {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.select("DISTINCT location");
        return HttpResult.success(careerFairDao.selectList(careerFairQueryWrapper), "查询成功");
    }

    // 企业用户登录

    /**
     * Authenticates an enterprise user based on provided credentials.
     *
     * @param account  The user account for authentication.
     * @param password The user password for authentication.
     * @return HTTP result with authentication status and token if successful, else failure status.
     */
    @Override
    public HttpResult enterpriseLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account, password);
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

        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        if (enterpriseUser.getUserStatus().equals("2")) {
            return HttpResult.failure(ResultCodeEnum.REDIRECT, "用户被禁用");
        }
        String infoIntegrity = "true";
        if (Objects.isNull(enterpriseUser.getAge())
                || Objects.isNull(enterpriseUser.getBirthday())
                || Objects.isNull(enterpriseUser.getGender())
                || Objects.isNull(enterpriseUser.getGraduationSchool())
                || Objects.isNull(enterpriseUser.getTel())) {
            infoIntegrity = "false";
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        map.put("infoIntegrity", infoIntegrity);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        return HttpResult.success(map, "登录成功");
    }

    // 企业用户登出

    /**
     * Logs out an authenticated enterprise user.
     *
     * @return HTTP result indicating successful user logout.
     */
    @Override
    public HttpResult enterpriseLogout() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(userId));
        return HttpResult.success(null, "用户注销");
    }

    // 企业用户修改密码

    /**
     * Changes the password for an authenticated enterprise user.
     *
     * @param newPassword The new password to be set.
     * @param oldPassword The old password to be validated before changing.
     * @return HTTP result indicating success or failure of password change.
     */
    @Override
    public HttpResult enterpriseChangePassword(String newPassword, String oldPassword) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (!bCryptPasswordEncoder.matches(
                oldPassword, userDao.selectOne(queryWrapper).getPassword())) {
            return HttpResult.failure(ResultCodeEnum.REDIRECT, "密码错误");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("password", bCryptPasswordEncoder.encode(newPassword));
        try {
            userDao.update(null, updateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(null, "修改成功");
    }

    // 创建新部门

    /**
     * Creates a new department under the logged-in enterprise user.
     *
     * @param departmentName The name of the department to be created.
     * @return HTTP result indicating the success or failure of department creation.
     */
    @Override
    public HttpResult createNewDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    // 根据部门名称模糊查询部门

    /**
     * Queries departments based on the provided department name (can be fuzzy) under the logged-in
     * enterprise user.
     *
     * @param departmentName The department name or part of it to search for.
     * @return HTTP result containing department information based on the query.
     */
    @Override
    public HttpResult queryDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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
            recruitmentInfoQueryWrapper
                    .ne("status", "0")
                    .orderByDesc("submission_time")
                    .eq("department_id", department.getDepartmentId());
            List<RecruitmentInfo> recruitmentInfoList =
                    recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper);
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

    // 删除部门

    /**
     * Deletes a department associated with the logged-in enterprise user.
     *
     * @param departmentName The name of the department to be deleted.
     * @return An HttpResult indicating the success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteDepartment(String departmentName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    // 创建新招聘信息
    // 招聘信息必须归属在某个部门下

    /**
     * Creates a new recruitment information entry linked to a specific department.
     *
     * @param draftName       The name of the recruitment draft.
     * @param departmentName  The name of the department to which the recruitment info belongs.
     * @param recruitmentInfo The details of the recruitment information to be created.
     * @return An HttpResult indicating the success or failure of the creation operation.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult createNewRecruitmentInfo(
            String draftName, String departmentName, RecruitmentInfo recruitmentInfo) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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
            recruitmentInfoQueryWrapper
                    .eq("user_id", userId)
                    .eq("job_title", recruitmentInfo.getJobTitle());
            RecruitmentInfo recruitmentInfo1 = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
            Draft draft =
                    new Draft(
                            null,
                            userId,
                            recruitmentInfo1.getRecruitmentId(),
                            Timestamp.valueOf(formatter.format(date)),
                            draftName);
            try {
                draftDao.insert(draft);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "草稿已存在");
            }
        }
        return HttpResult.success(null, "添加成功");
    }

    // 查询招聘信息

    /**
     * Queries recruitment information based on various parameters like city, salary range, department
     * name, and status number.
     *
     * @param city           The city name to filter the recruitment information.
     * @param salaryRange    The range of salary to filter the recruitment information.
     * @param departmentName The department name to filter the recruitment information.
     * @param statusNum      The status number to filter the recruitment information.
     * @param current        The current page number for pagination.
     * @return An HttpResult containing the queried recruitment information.
     */
    @Override
    public HttpResult queryRecruitmentInfo(
            String city, String salaryRange, String departmentName, Integer statusNum, Integer current) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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
        return HttpResult.success(
                enterpriseUserDao.queryRecruitmentInfo(
                        page, userId, city, maxSalary, departmentName, statusNum),
                "查询成功");
    }

    // 查询招聘信息草稿

    /**
     * Queries recruitment information by the specified draft name.
     *
     * @param draftName The name of the draft to filter the recruitment information.
     * @return An HttpResult containing the queried recruitment information.
     */
    @Override
    public HttpResult queryRecruitmentInfoByDraft(String draftName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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
        recruitmentInfoQueryWrapper.select(
                "job_title",
                "job_description",
                "company_name",
                "city",
                "status",
                "submission_time",
                "approval_time",
                "recruit_num",
                "recruited_num",
                "byword",
                "job_duties",
                "min_salary",
                "max_salary");
        recruitmentInfoQueryWrapper.eq("recruitment_id", draft.getRecruitmentId());
        return HttpResult.success(recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper), "查询成功");
    }

    // 删除招聘信息

    /**
     * Deletes a specific recruitment information entry associated with a department.
     *
     * @param departmentName The name of the department related to the recruitment information.
     * @param jobTitle       The job title for which recruitment information needs to be deleted.
     * @return An HttpResult indicating the success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteRecruitmentInfo(String departmentName, String jobTitle) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    // 更新招聘信息草稿

    /**
     * Updates a recruitment information draft by replacing the old draft name with a new one and
     * updating the associated recruitment information.
     *
     * @param oldDraftName    The old name of the draft to be updated.
     * @param newDraftName    The new name to replace the old draft name.
     * @param recruitmentInfo The updated recruitment information.
     * @return An HttpResult indicating the success or failure of the update operation.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult updateDraft(
            String oldDraftName, String newDraftName, RecruitmentInfo recruitmentInfo) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    // 查询草稿

    /**
     * Queries the drafts associated with the current user.
     *
     * @return An HttpResult containing the queried drafts.
     */
    @Override
    public HttpResult queryDraft() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryDraft(userId), "查询成功");
    }

    // 删除草稿

    /**
     * Deletes a specific draft related to the current user.
     *
     * @param draftName The name of the draft to be deleted.
     * @return An HttpResult indicating the success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteDraft(String draftName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    // 根据学校名称、部门名称模糊查询求职申请

    /**
     * Queries job applications based on school name, department name, and code, allowing fuzzy
     * search.
     *
     * @param schoolName     The school name for the query.
     * @param departmentName The department name for the query.
     * @param code           The code for the query.
     * @param current        The current page for paginated results.
     * @return An HttpResult containing the queried job applications.
     */
    @Override
    public HttpResult queryJobApplication(
            String schoolName, String departmentName, Integer code, Integer current) {
        Page<EnterpriseJobApplicationVo> page = new Page<>(current, 7);
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(
                enterpriseUserDao.queryJobApplication(page, schoolName, departmentName, code, userId),
                "查询成功");
    }

    // 获取求职申请对应简历

    /**
     * Queries the resume associated with a specific job application.
     *
     * @param jobApplicationId The ID of the job application to retrieve the resume for.
     * @return An HttpResult containing the queried resume information.
     */
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
        StudentResumeAllVo data =
                new StudentResumeAllVo(
                        resume, student.getName(), student.getGender(), student.getPhoneNumber());
        return HttpResult.success(data, "查询成功");
    }

    // 删除求职申请
    // 实际是更改标志位使该申请在企业端不显示

    /**
     * Deletes a job application by changing the flag to hide it on the enterprise end.
     *
     * @param studentAccount The account of the student associated with the job application.
     * @param departmentName The department name related to the job application.
     * @param jobTitle       The title of the job related to the application.
     * @return An HttpResult indicating the success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteJobApplication(
            String studentAccount, String departmentName, String jobTitle) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();

        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper
                .eq("name", departmentName)
                .eq(
                        "enterprise_id",
                        enterpriseUserDao.selectOne(enterpriseUserQueryWrapper).getEnterpriseId());
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
        recruitmentInfoQueryWrapper
                .eq("department_id", department.getDepartmentId())
                .eq("job_title", jobTitle)
                .eq("user_id", userId);
        RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
        if (Objects.isNull(recruitmentInfo)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>();
        jobApplicationUpdateWrapper
                .eq("recruitment_id", recruitmentInfo.getRecruitmentId())
                .eq("student_id", user.getUserId())
                .set("enterprise_visible", "0");
        try {
            jobApplicationDao.update(null, jobApplicationUpdateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(jobTitle, "删除成功");
    }

    // 否决求职申请

    /**
     * Disagrees with a job application by updating its status and providing a rejection reason.
     *
     * @param id           Integer ID of the job application to reject.
     * @param rejectReason String Reason for rejecting the job application.
     * @return HttpResult Result of the operation.
     */
    @Override
    public HttpResult disagreeJobApplication(Integer id, String rejectReason) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>();
        jobApplicationUpdateWrapper
                .set("status", "1")
                .set("rejection_reason", rejectReason)
                .set("approval_time", Timestamp.valueOf(format.format(date)))
                .eq("application_id", id);
        if (jobApplicationDao.update(null, jobApplicationUpdateWrapper) > 0) {
            return HttpResult.success();
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "操作失败");
    }

    // 通过求职申请
    // 会发送邮件提醒学生参加面试相关信息

    /**
     * Approves a job application, schedules an interview, and sends an email notification to the
     * student.
     *
     * @param id       Integer ID of the job application to approve.
     * @param date     String Date of the scheduled interview.
     * @param position String Position for the interview.
     * @return HttpResult Result of the operation.
     */
    @Override
    public HttpResult agreeJobApplication(Integer id, String date, String position) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
        jobApplicationQueryWrapper.eq("application_id", id).eq("status", "0");
        JobApplication jobApplication = jobApplicationDao.selectOne(jobApplicationQueryWrapper);
        if (Objects.isNull(jobApplication)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "信息不存在或已审核");
        }
        Date time = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        InterviewInfo interviewInfo =
                new InterviewInfo(
                        null,
                        jobApplication.getStudentId(),
                        jobApplication.getApplicationId(),
                        userId,
                        Timestamp.valueOf(date + ":00"),
                        position,
                        '0',
                        Timestamp.valueOf(simpleDateFormat.format(time)),
                        '0');
        try {
            interviewInfoDao.insert(interviewInfo);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "新建面试失败");
        }
        UpdateWrapper<JobApplication> jobApplicationUpdateWrapper = new UpdateWrapper<>(jobApplication);
        jobApplicationUpdateWrapper
                .set("status", '2')
                .set("approval_time", Timestamp.valueOf(simpleDateFormat.format(time)));
        jobApplicationDao.update(null, jobApplicationUpdateWrapper);
        // 给学生发邮箱提醒
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
        String content =
                "您于"
                        + jobApplication.getApplicationTime()
                        + "向"
                        + recruitmentInfo.getCompanyName()
                        + "的"
                        + department.getName()
                        + "申请"
                        + recruitmentInfo.getJobTitle()
                        + "的简历已经通过，面试将在"
                        + date
                        + "于"
                        + interviewInfo.getPosition()
                        + "进行,请按时参加。";
        if (!emailVerification.interviewReminderService(user.getAccount(), content)) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "邮件发送失败");
        }
        return HttpResult.success();
    }

    // 创建招聘宣讲会

    /**
     * Creates a career fair event with specific details.
     *
     * @param title      String Title of the career fair.
     * @param content    String Content or description of the event.
     * @param startTime  Timestamp Start time of the event.
     * @param endTime    Timestamp End time of the event.
     * @param location   String Location where the event will be held.
     * @param host       String Host or organizer of the event.
     * @param schoolName String Name of the school associated with the event.
     * @return HttpResult Result of the operation.
     */
    @Override
    public HttpResult createFair(
            String title,
            String content,
            Timestamp startTime,
            Timestamp endTime,
            String location,
            String host,
            String schoolName) {
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_name", schoolName);
        School school = schoolDao.selectOne(schoolQueryWrapper);
        if (Objects.isNull(school)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "学校不存在");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);
        CareerFair careerFair =
                new CareerFair(
                        null,
                        userId,
                        school.getSchoolId(),
                        enterpriseUser.getEnterpriseId(),
                        startTime,
                        endTime,
                        location,
                        host,
                        "0",
                        title,
                        content,
                        null);
        try {
            careerFairDao.insert(careerFair);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "创建失败");
        }
        return HttpResult.success(title, "创建成功");
    }

    // 查询宣讲会

    /**
     * Queries career fair events based on specific parameters like host, location, school name, date,
     * and code.
     *
     * @param host       String Name of the host or organizer.
     * @param location   String Location where the career fair will be held.
     * @param schoolName String Name of the school associated with the career fair.
     * @param date       Timestamp Date of the career fair.
     * @param code       Integer Code related to the career fair.
     * @param current    Integer Current page number for pagination.
     * @return HttpResult Result of the query operation.
     */
    @Override
    public HttpResult queryFair(
            String host,
            String location,
            String schoolName,
            Timestamp date,
            Integer code,
            Integer current) {
        Page<EnterpriseFairVo> page = new Page<>(current, 7);
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(
                enterpriseUserDao.queryFair(page, host, location, schoolName, date, code, userId), "查询成功");
    }

    // 修改宣讲会

    /**
     * Modifies details of a career fair event.
     *
     * @param id         Integer ID of the career fair to be updated.
     * @param title      String Title of the career fair.
     * @param content    String Content or description of the career fair.
     * @param startTime  Timestamp Start time of the career fair.
     * @param endTime    Timestamp End time of the career fair.
     * @param location   String Location where the career fair will be held.
     * @param host       String Host or organizer of the career fair.
     * @param schoolName String Name of the school associated with the career fair.
     * @return HttpResult Result of the update operation.
     */
    @Override
    public HttpResult updateFair(
            Integer id,
            String title,
            String content,
            Timestamp startTime,
            Timestamp endTime,
            String location,
            String host,
            String schoolName) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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
            careerFairDao.update(
                    new CareerFair(
                            null,
                            null,
                            school.getSchoolId(),
                            null,
                            startTime,
                            endTime,
                            location,
                            host,
                            null,
                            title,
                            content,
                            null),
                    careerFairUpdateWrapper);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
        }
        return HttpResult.success(null, "修改成功");
    }

    // 删除宣讲会

    /**
     * Deletes a career fair event by its ID.
     *
     * @param id Integer ID of the career fair to be deleted.
     * @return HttpResult Result of the deletion operation.
     */
    @Override
    public HttpResult deleteFair(Integer id) {
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.eq("fair_id", id);
        if (careerFairDao.delete(careerFairQueryWrapper) > 0) {
            return HttpResult.success(id, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "对象不存在");
    }

    // 获取本账号当前信息

    /**
     * Retrieves current account information associated with the logged-in user.
     *
     * @return HttpResult Result containing the queried account information.
     */
    @Override
    public HttpResult queryInfo() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        return HttpResult.success(enterpriseUserDao.queryInfo(userId), "查询成功");
    }

    // 更新账号信息

    /**
     * Updates account information for the logged-in user.
     *
     * @param avatar           MultipartFile Avatar or profile picture of the user.
     * @param name             String Name of the user.
     * @param birthday         java.sql.Date Birthday of the user.
     * @param age              Integer Age of the user.
     * @param gender           String Gender of the user.
     * @param graduationSchool String Name of the user's graduation school.
     * @return HttpResult Result of the information update operation.
     */
    @Override
    public HttpResult updateInfo(
            MultipartFile avatar,
            String name,
            java.sql.Date birthday,
            Integer age,
            String gender,
            String graduationSchool,
            String tel) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        UpdateWrapper<EnterpriseUser> enterpriseUserUpdateWrapper = new UpdateWrapper<>();
        enterpriseUserUpdateWrapper.eq("user_id", userId);
        try {
            if (enterpriseUserDao.update(
                    new EnterpriseUser(
                            null,
                            null,
                            name,
                            age,
                            birthday,
                            gender,
                            graduationSchool,
                            tel,
                            null,
                            avatar.getBytes()),
                    enterpriseUserUpdateWrapper)
                    > 0) {
                return HttpResult.success();
            }
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "修改失败");
    }

    // 查询面试信息
    // 可以通过日期、学校、

    /**
     * Queries interview information based on provided parameters such as date, school, and status
     * code.
     *
     * @param date    String Date to filter interviews (format: "YYYY-MM-DD").
     * @param school  String School name to filter interviews.
     * @param code    Integer Status code to filter interviews.
     * @param current Integer Current page number for pagination.
     * @return HttpResult Result containing the queried interview information.
     */
    @Override
    public HttpResult queryInterview(String date, String school, Integer code, Integer current) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        Page<EnterpriseInterviewVo> page = new Page<>(current, 7);
        return HttpResult.success(
                enterpriseUserDao.queryInterview(
                        page,
                        userId,
                        date.isEmpty() ? null : Timestamp.valueOf(date + " 00:00:00"),
                        school,
                        code),
                "查询成功");
    }

    /**
     * Deletes an interview record by its ID.
     *
     * @param id Integer ID of the interview to be deleted.
     * @return HttpResult Result of the deletion operation.
     */
    @Override
    public HttpResult deleteInterview(Integer id) {
        QueryWrapper<InterviewInfo> interviewInfoQueryWrapper = new QueryWrapper<>();
        interviewInfoQueryWrapper.eq("interview_id", id);
        if (interviewInfoDao.delete(interviewInfoQueryWrapper) > 0) {
            return HttpResult.success(id, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "删除失败");
    }

    /**
     * Approves an interview by changing its status to '1' (approved).
     *
     * @param id Integer ID of the interview to be approved.
     * @return HttpResult Result of the approval operation.
     */
    @Override
    public HttpResult agreeInterview(Integer id) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        UpdateWrapper<InterviewInfo> interviewInfoUpdateWrapper = new UpdateWrapper<>();
        interviewInfoUpdateWrapper
                .eq("interview_id", id)
                .set("status", "1")
                .set("principal_id", userId)
                .set("time", Timestamp.valueOf(format.format(date)));
        if (interviewInfoDao.update(null, interviewInfoUpdateWrapper) > 0) {
            return HttpResult.success(id, "审核成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "审核失败");
    }

    /**
     * Disapproves an interview by changing its status to '2' (disapproved).
     *
     * @param id Integer ID of the interview to be disapproved.
     * @return HttpResult Result of the disapproval operation.
     */
    @Override
    public HttpResult disagreeInterview(Integer id) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        if (Objects.isNull(loginUser)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        int userId = loginUser.getUser().getUserId();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        UpdateWrapper<InterviewInfo> interviewInfoUpdateWrapper = new UpdateWrapper<>();
        interviewInfoUpdateWrapper
                .eq("interview_id", id)
                .set("status", "2")
                .set("principal_id", userId)
                .set("time", Timestamp.valueOf(format.format(date)));
        if (interviewInfoDao.update(null, interviewInfoUpdateWrapper) > 0) {
            return HttpResult.success(id, "审核成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "审核失败");
    }

    /**
     * Analyzes job applications by department, counting the number of applications per department.
     *
     * @return HttpResult Result containing the analysis data grouped by department.
     */
    @Override
    public HttpResult applicationAnalysisByDepartment() {
        Map<String, Integer> map = new HashMap<>();
        List<JobApplication> jobApplicationList = jobApplicationDao.selectList(null);
        for (JobApplication jobApplication : jobApplicationList) {
            QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
            recruitmentInfoQueryWrapper.eq("recruitment_id", jobApplication.getRecruitmentId());
            RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
            QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
            departmentQueryWrapper.eq("department_id", recruitmentInfo.getDepartmentId());
            Department department = departmentDao.selectOne(departmentQueryWrapper);
            if (map.containsKey(department.getName())) {
                map.put(department.getName(), map.get(department.getName()) + 1);
            } else {
                map.put(department.getName(), 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes job applications by school, counting the number of applications per school.
     *
     * @return HttpResult Result containing the analysis data grouped by school.
     */
    @Override
    public HttpResult applicationAnalysisBySchool() {
        Map<String, Integer> map = new HashMap<>();
        List<JobApplication> jobApplicationList = jobApplicationDao.selectList(null);
        for (JobApplication jobApplication : jobApplicationList) {
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("student_id", jobApplication.getStudentId());
            Student student = studentDao.selectOne(studentQueryWrapper);
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.eq("school_id", student.getSchoolId());
            School school = schoolDao.selectOne(schoolQueryWrapper);
            if (map.containsKey(school.getSchoolName())) {
                map.put(school.getSchoolName(), map.get(school.getSchoolName()) + 1);
            } else {
                map.put(school.getSchoolName(), 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes job applications by pass status, counting the number of applications that are passed
     * or not passed.
     *
     * @return HttpResult Result containing the analysis data grouped by pass status.
     */
    @Override
    public HttpResult applicationAnalysisByPass() {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<JobApplication> passedApp = new QueryWrapper<>();
        QueryWrapper<JobApplication> notPassApp = new QueryWrapper<>();
        passedApp.eq("status", "2");
        notPassApp.eq("status", "1");
        map.put("已通过", jobApplicationDao.selectCount(passedApp));
        map.put("未通过", jobApplicationDao.selectCount(notPassApp));
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes career fairs by month for a given year, counting the number of fairs held each month.
     *
     * @param year String representing the year for which analysis is performed.
     * @return HttpResult Result containing the analysis data grouped by month for the given year.
     */
    @Override
    public HttpResult fairAnalysisByMon(String year) {
        Map<String, Integer> map = new HashMap<>();
        map.put("01月", 0);
        map.put("02月", 0);
        map.put("03月", 0);
        map.put("04月", 0);
        map.put("05月", 0);
        map.put("06月", 0);
        map.put("07月", 0);
        map.put("08月", 0);
        map.put("09月", 0);
        map.put("10月", 0);
        map.put("11月", 0);
        map.put("12月", 0);
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.eq("status", "1");
        List<CareerFair> careerFairList = careerFairDao.selectList(careerFairQueryWrapper);
        for (CareerFair careerFair : careerFairList) {
            String startYear = careerFair.getStartTime().toString().substring(0, 4);
            if (startYear.equals(year)) {
                String startMon = careerFair.getStartTime().toString().substring(5, 7);
                map.put(startMon + "月", map.get(startMon + "月") + 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes career fairs by school and month, counting the number of fairs held per school for a
     * specific month. If no specific month is provided, it analyzes all months.
     *
     * @param mon String representing the month for which analysis is performed.
     * @return HttpResult Result containing the analysis data grouped by school for the given month.
     */
    @Override
    public HttpResult fairAnalysisBySchool(String mon) {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.eq("status", "1");
        List<CareerFair> careerFairList = careerFairDao.selectList(careerFairQueryWrapper);
        for (CareerFair careerFair : careerFairList) {
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.eq("school_id", careerFair.getSchoolId());
            School school = schoolDao.selectOne(schoolQueryWrapper);
            if (!map.containsKey(school.getSchoolName())) {
                map.put(school.getSchoolName(), 0);
            }
            String startMon = careerFair.getStartTime().toString().substring(5, 7);
            if (Objects.isNull(mon) || mon.isEmpty() || startMon.equals(mon)) {
                map.put(school.getSchoolName(), map.get(school.getSchoolName()) + 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes career fairs by their pass status, counting the number of fairs that are passed or not
     * passed.
     *
     * @return HttpResult Result containing the analysis data grouped by pass status of career fairs.
     */
    @Override
    public HttpResult fairAnalysisByPass() {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<CareerFair> passedApp = new QueryWrapper<>();
        QueryWrapper<CareerFair> notPassApp = new QueryWrapper<>();
        passedApp.eq("status", "1");
        notPassApp.eq("status", "2");
        map.put("已通过", careerFairDao.selectCount(passedApp));
        map.put("未通过", careerFairDao.selectCount(notPassApp));
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes interviews by month for a given year, counting the number of interviews held each
     * month.
     *
     * @param year String representing the year for which analysis is performed.
     * @return HttpResult Result containing the analysis data grouped by month for interviews in the
     * given year.
     */
    @Override
    public HttpResult interviewAnalysisByMon(String year) {
        Map<String, Integer> map = new HashMap<>();
        map.put("01月", 0);
        map.put("02月", 0);
        map.put("03月", 0);
        map.put("04月", 0);
        map.put("05月", 0);
        map.put("06月", 0);
        map.put("07月", 0);
        map.put("08月", 0);
        map.put("09月", 0);
        map.put("10月", 0);
        map.put("11月", 0);
        map.put("12月", 0);
        List<InterviewInfo> interviewInfoList = interviewInfoDao.selectList(null);
        for (InterviewInfo interviewInfo : interviewInfoList) {
            String startYear = interviewInfo.getTime().toString().substring(0, 4);
            if (startYear.equals(year)) {
                String startMon = interviewInfo.getTime().toString().substring(5, 7);
                map.put(startMon + "月", map.get(startMon + "月") + 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes interviews by their pass status, counting the number of interviews that are passed or
     * not passed.
     *
     * @return HttpResult Result containing the analysis data grouped by pass status of interviews.
     */
    @Override
    public HttpResult interviewAnalysisByPass() {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<InterviewInfo> passedApp = new QueryWrapper<>();
        QueryWrapper<InterviewInfo> notPassApp = new QueryWrapper<>();
        passedApp.eq("status", "1");
        notPassApp.eq("status", "2");
        map.put("已通过", interviewInfoDao.selectCount(passedApp));
        map.put("未通过", interviewInfoDao.selectCount(notPassApp));
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes interviews by department within the enterprise, counting the number of interviews
     * held, the number of interviews passed, and the number of interviews not passed in each
     * department.
     *
     * @return HttpResult Result containing the analysis data grouped by department in the enterprise.
     */
    @Override
    public HttpResult interviewAnalysisByDepartment() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<EnterpriseUser> enterpriseUserQueryWrapper = new QueryWrapper<>();
        enterpriseUserQueryWrapper.eq("user_id", userId);
        EnterpriseUser enterpriseUser = enterpriseUserDao.selectOne(enterpriseUserQueryWrapper);

        Map<String, Map<String, Integer>> map = new HashMap<>();
        List<InterviewInfo> interviewInfoList = interviewInfoDao.selectList(null);
        for (InterviewInfo interviewInfo : interviewInfoList) {
            QueryWrapper<JobApplication> jobApplicationQueryWrapper = new QueryWrapper<>();
            jobApplicationQueryWrapper.eq("application_id", interviewInfo.getApplicationId());
            QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
            recruitmentInfoQueryWrapper.eq(
                    "recruitment_id",
                    jobApplicationDao.selectOne(jobApplicationQueryWrapper).getRecruitmentId());
            RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(recruitmentInfoQueryWrapper);
            if (recruitmentInfo.getEnterpriseId().equals(enterpriseUser.getEnterpriseId())) {
                QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
                departmentQueryWrapper.eq("department_id", recruitmentInfo.getDepartmentId());
                Department department = departmentDao.selectOne(departmentQueryWrapper);
                if (!map.containsKey(department.getName())) {
                    map.put(
                            department.getName(),
                            new HashMap<>() {
                                {
                                    put("面试数量", 0);
                                    put("通过数", 0);
                                    put("不通过数", 0);
                                }
                            });
                }
                Map<String, Integer> inner = map.get(department.getName());
                inner.put("面试数量", inner.get("面试数量") + 1);
                if (interviewInfo.getStatus().equals('1')) {
                    inner.put("通过数", inner.get("通过数") + 1);
                }
                if (interviewInfo.getStatus().equals('2')) {
                    inner.put("不通过数", inner.get("不通过数") + 1);
                }
                map.put(department.getName(), inner);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes recruitment information by its pass status, counting the number of recruitments that
     * are passed or not passed.
     *
     * @return HttpResult Result containing the analysis data grouped by pass status of recruitment
     * information.
     */
    @Override
    public HttpResult recruitmentAnalysisByPass() {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<RecruitmentInfo> passedApp = new QueryWrapper<>();
        QueryWrapper<RecruitmentInfo> notPassApp = new QueryWrapper<>();
        passedApp.eq("status", "2");
        notPassApp.eq("status", "3");
        map.put("已通过", recruitmentInfoDao.selectCount(passedApp));
        map.put("未通过", recruitmentInfoDao.selectCount(notPassApp));
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes recruitment information by city, counting the number of recruitments in each city that
     * have been marked as passed.
     *
     * @return HttpResult Result containing the analysis data grouped by city for passed recruitments.
     */
    @Override
    public HttpResult recruitmentAnalysisByCity() {
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("status", "2");
        List<RecruitmentInfo> recruitmentInfoList =
                recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper);
        for (RecruitmentInfo recruitmentInfo : recruitmentInfoList) {
            if (map.containsKey(recruitmentInfo.getCity())) {
                map.put(recruitmentInfo.getCity(), map.get(recruitmentInfo.getCity()) + 1);
            } else {
                map.put(recruitmentInfo.getCity(), 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Analyzes recruitment information by maximum salary range, categorizing recruitments based on
     * their maximum salary values.
     *
     * @return HttpResult Result containing the analysis data grouped by maximum salary range for
     * passed recruitments.
     */
    @Override
    public HttpResult recruitmentAnalysisByMaxSalary() {
        Map<String, Integer> map = new HashMap<>();
        map.put("10k以下", 0);
        map.put("10k-20k", 0);
        map.put("20k-30k", 0);
        map.put("30k以上", 0);
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("status", "2");
        List<RecruitmentInfo> recruitmentInfoList =
                recruitmentInfoDao.selectList(recruitmentInfoQueryWrapper);
        for (RecruitmentInfo recruitmentInfo : recruitmentInfoList) {
            if (recruitmentInfo.getMaxSalary() < 10) {
                map.put("10k以下", map.get("10k以下") + 1);
            } else if (recruitmentInfo.getMaxSalary() < 20) {
                map.put("10k-20k", map.get("10k-20k") + 1);
            } else if (recruitmentInfo.getMaxSalary() < 30) {
                map.put("20k-30k", map.get("20k-30k") + 1);
            } else {
                map.put("30k以上", map.get("30k以上") + 1);
            }
        }
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Queries the list of sent messages for the currently logged-in enterprise user.
     *
     * @param current Integer value indicating the current page for pagination.
     * @return HttpResult Result containing the list of sent messages for the logged-in enterprise
     * user.
     */
    @Override
    public HttpResult querySentMessageList(Integer current) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        Page<EnterpriseSentMessageVo> page = new Page<>(current, 10);
        return HttpResult.success(enterpriseUserDao.querySentMessage(page, userId), "查询成功");
    }

    /**
     * Queries a specific sent message for the currently logged-in enterprise user.
     *
     * @param id Integer value indicating the ID of the message to be queried.
     * @return HttpResult Result containing the details of the queried sent message for the logged-in
     * enterprise user.
     */
    @Override
    public HttpResult querySentMessage(Integer id) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("message_id", id).eq("enterprise_del", "0").eq("`from`", userId);
        messageQueryWrapper.select("title", "content");
        return HttpResult.success(messageDao.selectOne(messageQueryWrapper), "查询成功");
    }

    /**
     * Deletes a sent message by setting the 'enterprise_del' flag to 1, indicating deletion by the
     * enterprise user.
     *
     * @param id Integer value indicating the ID of the message to be deleted.
     * @return HttpResult Result indicating the success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteSentMessage(Integer id) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        UpdateWrapper<Message> messageQueryWrapper = new UpdateWrapper<>();
        messageQueryWrapper.eq("message_id", id).eq("enterprise_del", "0").eq("`from`", userId);
        messageQueryWrapper.set("enterprise_del", "1");
        if (messageDao.update(null, messageQueryWrapper) > 0) {
            return HttpResult.success(id, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "删除失败");
    }

    /**
     * Queries the list of recent contacts for the currently logged-in enterprise user.
     *
     * @return HttpResult Result containing a list of recent contacts based on the latest messages
     * sent by the user.
     */
    @Override
    public HttpResult queryRecentContacts() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
        messageQueryWrapper.eq("`from`", userId).orderByAsc("time").last("limit 5");
        List<Message> messageList = messageDao.selectList(messageQueryWrapper);
        List<String> accountList = new ArrayList<>();
        for (Message message : messageList) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("user_id", message.getTo());
            accountList.add(userDao.selectOne(userQueryWrapper).getAccount());
        }
        return HttpResult.success(accountList, "查询成功");
    }

    /**
     * Sends a message from the currently logged-in enterprise user to another user identified by
     * their account.
     *
     * @param account String value indicating the account of the recipient user.
     * @param type    String value indicating the type of message being sent.
     * @param title   String value indicating the title of the message.
     * @param content String value indicating the content of the message.
     * @return HttpResult Result indicating the success or failure of the sending operation.
     */
    @Override
    public HttpResult sendMessage(String account, String type, String title, String content) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "收件人不存在");
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Message message =
                new Message(
                        null,
                        userId,
                        user.getUserId(),
                        title,
                        content,
                        format.format(date),
                        "0",
                        type,
                        "0",
                        "0");
        try {
            messageDao.insert(message);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "发送失败");
        }
        return HttpResult.success(null, "发送成功");
    }
}
