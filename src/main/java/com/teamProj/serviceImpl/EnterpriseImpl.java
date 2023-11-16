package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.DepartmentDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.entity.Department;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EnterpriseImpl implements EnterpriseService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;
    @Resource
    private EnterpriseDao enterpriseDao;

    @Resource
    private DepartmentDao departmentDao;

    @Resource
    private RecruitmentInfoDao recruitmentInfoDao;

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
        int enterpriseId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(enterpriseId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult createNewDepartment(String enterpriseName, String departmentName) {
        QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
        enterpriseQueryWrapper.eq("enterprise_name", enterpriseName);
        Enterprise enterprise = enterpriseDao.selectOne(enterpriseQueryWrapper);
        if (Objects.isNull(enterprise)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        Department department = new Department(null, departmentName, enterprise.getEnterpriseId());
        try {
            departmentDao.insert(department);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(departmentName, "创建成功");
    }

    @Override
    public HttpResult createNewRecruitmentInfo(String departmentName, RecruitmentInfo recruitmentInfo) {
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("name", departmentName);
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

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int userId = loginUser.getUser().getUserId();

        recruitmentInfo.setUserId(userId);
        recruitmentInfo.setEnterpriseId(department.getEnterpriseId());
        recruitmentInfo.setDepartmentId(department.getDepartmentId());
        recruitmentInfo.setCompanyName(enterprise.getEnterpriseName());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        recruitmentInfo.setSubmissionTime(Timestamp.valueOf(formatter.format(date)));

        try {
            recruitmentInfoDao.insert(recruitmentInfo);
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
        return HttpResult.success(null, "添加成功");
    }
}
