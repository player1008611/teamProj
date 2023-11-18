package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.DepartmentDao;
import com.teamProj.dao.DraftDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.EnterpriseUserDao;
import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.entity.Department;
import com.teamProj.entity.Draft;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.vo.EnterpriseJobApplicationVo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
public class EnterpriseImpl implements EnterpriseService {

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
        departmentQueryWrapper.select("name").eq("enterprise_id", enterpriseUser.getEnterpriseId());
        if (!Objects.isNull(departmentName) && !departmentName.equals("")) {
            departmentQueryWrapper.eq("name", departmentName);
        }
        return HttpResult.success(departmentDao.selectList(departmentQueryWrapper), "查询成功");
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

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        recruitmentInfo.setSubmissionTime(Timestamp.valueOf(formatter.format(date)));

        try {
            recruitmentInfoDao.insert(recruitmentInfo);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
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
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
            }
        }
        return HttpResult.success(null, "添加成功");
    }

    @Override
    public HttpResult queryRecruitmentInfo(String city, String salaryRange, String departmentName, Integer current) {
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("name", departmentName);
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        if (Objects.isNull(department)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.select("job_title"
                        , "job_description"
                        , "company_name"
                        , "city"
                        , "status"
                        , "submission_time"
                        , "approval_time"
                        , "rejection_reason"
                        , "recruit_num"
                        , "recruited_num"
                        , "byword"
                        , "job_duties"
                        , "min_salary"
                        , "max_salary")
                .eq("enterprise_id", department.getEnterpriseId())
                .eq("department_id", department.getDepartmentId());
        if (!Objects.isNull(city) && !city.isEmpty()) {
            recruitmentInfoQueryWrapper.eq("city", city);
        }
        if (!Objects.isNull(salaryRange) && !salaryRange.isEmpty()) {
            Integer maxSalary = Integer.parseInt(salaryRange.substring(0, salaryRange.length() - 4));
            recruitmentInfoQueryWrapper.ge("max_salary", maxSalary);
        }
        Page<RecruitmentInfo> page = new Page<>(current, 6);
        return HttpResult.success(recruitmentInfoDao.selectPage(page, recruitmentInfoQueryWrapper), "查询成功");
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
}
