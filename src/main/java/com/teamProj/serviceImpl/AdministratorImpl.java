/**
 * This class implements the functionalities related to administrator operations.
 */
package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.dao.AnnouncementDao;
import com.teamProj.dao.CollegeDao;
import com.teamProj.dao.DepartmentDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.EnterpriseUserDao;
import com.teamProj.dao.MajorDao;
import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.dao.SchoolDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.dao.UserDao;
import com.teamProj.entity.Announcement;
import com.teamProj.entity.College;
import com.teamProj.entity.Department;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.LoginUser;
import com.teamProj.entity.Major;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.entity.User;
import com.teamProj.entity.vo.AdminAnnouncementVo;
import com.teamProj.entity.vo.AdminEnterpriseUserVo;
import com.teamProj.entity.vo.AdminRecruitmentVo;
import com.teamProj.entity.vo.AdminSchoolUserVo;
import com.teamProj.entity.vo.AdminStudentVo;
import com.teamProj.service.AdministratorService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;

    @Resource
    private UserDao userDao;

    @Resource
    private AdministratorDao administratorDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private EnterpriseDao enterpriseDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private DepartmentDao departmentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private RecruitmentInfoDao recruitmentInfoDao;

    @Resource
    private AnnouncementDao announcementDao;

    @Resource
    private CollegeDao collegeDao;

    @Resource
    private MajorDao majorDao;
    // 管理员登录

    /**
     * Administrator login functionality.
     *
     * @param account  The administrator's account.
     * @param password The administrator's password.
     * @return HTTP result indicating success or failure of login.
     */
    public HttpResult administratorLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authentication)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
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

    // 管理员登出

    /**
     * Administrator logout functionality.
     *
     * @return HTTP result indicating success or failure of logout.
     */
    public HttpResult administratorLogout() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    // 管理员主页信息

    /**
     * Retrieves information for the administrator's home page.
     *
     * @return HTTP result containing the queried information.
     */
    @Override
    public HttpResult queryHome() {
        QueryWrapper<RecruitmentInfo> recruitmentInfoQueryWrapper = new QueryWrapper<>();
        recruitmentInfoQueryWrapper.eq("status", "1");
        Map<String, Integer> count = new HashMap<>();
        count.put("student", studentDao.selectCount(null));
        count.put("school", schoolDao.selectCount(null));
        count.put("enterpriseUser", enterpriseUserDao.selectCount(null));
        count.put(
                "uncheckedRecruitmentInfo", recruitmentInfoDao.selectCount(recruitmentInfoQueryWrapper));
        return HttpResult.success(count, "查询成功");
    }

    // 模糊搜索学生

    /**
     * Retrieves a paginated list of students based on provided parameters.
     *
     * @param name       The name to be used for fuzzy searching students.
     * @param schoolName The school name to filter students by.
     * @param status     The status character to filter students by.
     * @param current    The current page for pagination.
     * @param size       The number of records per page for pagination.
     * @return HTTP result containing the paginated list of students matching the criteria.
     */
    @Override
    public HttpResult queryStudent(
            String name, String schoolName, Character status, Integer current, Integer size) {
        Page<AdminStudentVo> page = new Page<>(current, size);
        return HttpResult.success(
                administratorDao.queryStudent(page, name, schoolName, status), "查询成功");
    }

    // 根据account指定重置学生密码

    /**
     * Resets the password for a student based on their account.
     *
     * @param account The account identifier of the student whose password needs to be reset.
     * @return HTTP result indicating success or failure of the password reset.
     */
    @Override
    public HttpResult resetStudentPassword(String account) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("account", account)
                .eq("permission", "student")
                .set("password", bCryptPasswordEncoder.encode("123456"));
        if (userDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(account, "重置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 启用学生账号

    /**
     * Enables the student account based on the provided account identifier.
     *
     * @param account The account identifier of the student whose account needs to be enabled.
     * @return HTTP result indicating success or failure of enabling the student account.
     */
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

    // 封禁学生账号

    /**
     * Disables the student account based on the provided account identifier.
     *
     * @param account The account identifier of the student whose account needs to be disabled.
     * @return HTTP result indicating success or failure of disabling the student account.
     */
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

    // 删除学生账号

    /**
     * Deletes the student account based on the provided account identifier.
     *
     * @param account The account identifier of the student whose account needs to be deleted.
     * @return HTTP result indicating success or failure of deleting the student account.
     */
    @Override
    public HttpResult deleteStudentAccount(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "student");
        User user = userDao.selectOne(userQueryWrapper);

        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(studentQueryWrapper);

        QueryWrapper<College> collegeQueryWrapper = new QueryWrapper<>();
        collegeQueryWrapper.eq("college_id", student.getCollegeId());
        College college = collegeDao.selectOne(collegeQueryWrapper);

        UpdateWrapper<College> collegeUpdateWrapper = new UpdateWrapper<>();
        collegeUpdateWrapper.eq("college_id", student.getCollegeId()).set("student_num", college.getStudentNum() - 1);
        collegeDao.update(null, collegeUpdateWrapper);

        QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
        majorQueryWrapper.eq("major_id", student.getMajorId());
        Major major = majorDao.selectOne(majorQueryWrapper);

        UpdateWrapper<Major> majorUpdateWrapper = new UpdateWrapper<>();
        majorUpdateWrapper.eq("major_id", student.getMajorId()).set("student_num", major.getStudentNum() - 1);
        majorDao.update(null, majorUpdateWrapper);

        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 根据企业名模糊查询企业

    /**
     * Retrieves a paginated list of enterprises based on provided parameters.
     *
     * @param name    The name to be used for fuzzy searching enterprises.
     * @param current The current page for pagination.
     * @param size    The number of records per page for pagination.
     * @return HTTP result containing the paginated list of enterprises matching the criteria.
     */
    @Override
    public HttpResult queryEnterprise(String name, Integer current, Integer size) {
        Page<Enterprise> page = new Page<>(current, size);
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("enterprise_name", "website");
        if (!Objects.isNull(name) && !name.isEmpty()) {
            queryWrapper.like("enterprise_name", name);
        }
        return HttpResult.success(enterpriseDao.selectPage(page, queryWrapper), "查询成功");
    }

    // 根据企业名、用户名模糊查询企业用户

    /**
     * Retrieves a paginated list of enterprise users based on provided parameters.
     *
     * @param enterpriseName The name of the enterprise to filter users by.
     * @param userName       The username to be used for fuzzy searching enterprise users.
     * @param current        The current page for pagination.
     * @param size           The number of records per page for pagination.
     * @return HTTP result containing the paginated list of enterprise users matching the criteria.
     */
    @Override
    public HttpResult queryEnterpriseUser(
            String enterpriseName, String userName, Integer current, Integer size) {
        Page<AdminEnterpriseUserVo> page = new Page<>(current, size);
        return HttpResult.success(
                administratorDao.queryEnterpriseUser(page, enterpriseName, userName), "查询成功");
    }

    // 创建企业

    /**
     * Creates a new enterprise with the given name and URL.
     *
     * @param name The name of the enterprise.
     * @param url  The URL associated with the enterprise.
     * @return HTTP result indicating success or failure of enterprise creation.
     */
    @Override
    public HttpResult createNewEnterprise(String name, String url) {
        Enterprise enterprise = new Enterprise(null, name, url);
        if (enterpriseDao.insert(enterprise) > 0) {
            return HttpResult.success(name, "创建成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
    }

    // 创建企业用户

    /**
     * Creates a new user associated with an enterprise and adds the user to the enterprise.
     *
     * @param account        The account of the new enterprise user.
     * @param enterpriseName The name of the enterprise to which the user will be associated.
     * @param name           The name of the new enterprise user.
     * @param tel            The telephone number of the new enterprise user.
     * @return HTTP result indicating success or failure of enterprise user creation.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult createNewEnterpriseUser(
            String account, String enterpriseName, String name, String tel) {
        QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
        enterpriseQueryWrapper.eq("enterprise_name", enterpriseName);
        Enterprise enterprise = enterpriseDao.selectOne(enterpriseQueryWrapper);
        if (Objects.isNull(enterprise)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        User user = new User(null, account, bCryptPasswordEncoder.encode("123456"), "enterprise");
        try {
            userDao.insert(user);
        } catch (Exception e) {
            return HttpResult.success(null, "用户名重复");
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        user = userDao.selectOne(userQueryWrapper);
        EnterpriseUser enterpriseUser =
                new EnterpriseUser(
                        user.getUserId(),
                        enterprise.getEnterpriseId(),
                        name,
                        null,
                        null,
                        null,
                        null,
                        tel,
                        "1",
                        null);
        if (enterpriseUserDao.insert(enterpriseUser) > 0) {
            return HttpResult.success(name, "添加成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
    }

    // 重置企业用户密码

    /**
     * Resets the password of an enterprise user based on the provided account identifier.
     *
     * @param account The account identifier of the enterprise user whose password needs to be reset.
     * @return HTTP result indicating success or failure of password reset.
     */
    @Override
    public HttpResult resetEnterpriseUserPassword(String account) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("account", account)
                .eq("permission", "enterprise")
                .set("password", bCryptPasswordEncoder.encode("123456"));
        if (userDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(account, "重置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 启用企业用户

    /**
     * Enables the enterprise user account based on the provided account identifier.
     *
     * @param account The account identifier of the enterprise user to be enabled.
     * @return HTTP result indicating success or failure of enabling the enterprise user account.
     */
    @Override
    public HttpResult enableEnterpriseUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "enterprise");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<EnterpriseUser> enterpriseUserUpdateWrapper = new UpdateWrapper<>();
        enterpriseUserUpdateWrapper.eq("user_id", user.getUserId()).set("user_status", '1');
        if (enterpriseUserDao.update(null, enterpriseUserUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 封禁企业用户

    /**
     * Disables the enterprise user account based on the provided account identifier.
     *
     * @param account The account identifier of the enterprise user to be disabled.
     * @return HTTP result indicating success or failure of disabling the enterprise user account.
     */
    @Override
    public HttpResult disableEnterpriseUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "enterprise");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<EnterpriseUser> enterpriseUserUpdateWrapper = new UpdateWrapper<>();
        enterpriseUserUpdateWrapper.eq("user_id", user.getUserId()).set("user_status", '2');
        if (enterpriseUserDao.update(null, enterpriseUserUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 删除企业用户

    /**
     * Deletes the enterprise user account based on the provided account identifier.
     *
     * @param account The account identifier of the enterprise user to be deleted.
     * @return HTTP result indicating success or failure of deleting the enterprise user account.
     */
    @Override
    public HttpResult deleteEnterpriseUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "enterprise");
        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 查询学校用户

    /**
     * Retrieves a paginated list of school users based on provided parameters.
     *
     * @param principal The principal's name to filter school users by.
     * @param status    The status character to filter school users by.
     * @param current   The current page for pagination.
     * @param size      The number of records per page for pagination.
     * @return HTTP result containing the paginated list of school users matching the criteria.
     */
    @Override
    public HttpResult querySchoolUser(
            String principal, Character status, Integer current, Integer size) {
        Page<AdminSchoolUserVo> page = new Page<>(current, size);
        return HttpResult.success(administratorDao.querySchoolUser(page, principal, status), "查询成功");
    }

    // 创建学校用户

    /**
     * Creates a new user associated with a school and adds the user to the school.
     *
     * @param account    The account of the new school user.
     * @param schoolName The name of the school to which the user will be associated.
     * @param principal  The principal's name of the school user.
     * @param tel        The telephone number of the new school user.
     * @return HTTP result indicating success or failure of school user creation.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpResult createNewSchoolUser(
            String account, String schoolName, String principal, String tel) {
        User user = new User(null, account, bCryptPasswordEncoder.encode("123456"), "school");
        try {
            userDao.insert(user);
        } catch (Exception e) {
            return HttpResult.success(null, "用户名重复");
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        user = userDao.selectOne(userQueryWrapper);
        School schoolUser = new School(user.getUserId(), tel, principal, schoolName, "1");
        if (schoolDao.insert(schoolUser) > 0) {
            return HttpResult.success(schoolName, "添加成功");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
    }

    // 重置学校用户密码

    /**
     * Resets the password of a school user identified by the provided account.
     *
     * @param account The account identifier of the school user.
     * @return HTTP result indicating the success or failure of the password reset operation.
     */
    @Override
    public HttpResult resetSchoolUserPassword(String account) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("account", account)
                .eq("permission", "school")
                .set("password", bCryptPasswordEncoder.encode("123456"));
        if (userDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(account, "重置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 启用学校用户

    /**
     * Enables the school user account based on the provided account identifier.
     *
     * @param account The account identifier of the school user to be enabled.
     * @return HTTP result indicating success or failure of enabling the school user account.
     */
    @Override
    public HttpResult enableSchoolUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "school");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<School> schoolUpdateWrapper = new UpdateWrapper<>();
        schoolUpdateWrapper.eq("school_id", user.getUserId()).set("status", '1');
        if (schoolDao.update(null, schoolUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 封禁学校用户

    /**
     * Disables the school user account based on the provided account identifier.
     *
     * @param account The account identifier of the school user to be disabled.
     * @return HTTP result indicating success or failure of disabling the school user account.
     */
    @Override
    public HttpResult disableSchoolUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "school");
        User user = userDao.selectOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        UpdateWrapper<School> schoolUpdateWrapper = new UpdateWrapper<>();
        schoolUpdateWrapper.eq("school_id", user.getUserId()).set("status", '0');
        if (schoolDao.update(null, schoolUpdateWrapper) > 0) {
            return HttpResult.success(account, "设置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 删除学校用户

    /**
     * Deletes the school user account based on the provided account identifier.
     *
     * @param account The account identifier of the school user to be deleted.
     * @return HTTP result indicating success or failure of deleting the school user account.
     */
    @Override
    public HttpResult deleteSchoolUser(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "school");
        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    // 根据企业名称、部门名称、工作职位模糊查询招聘信息

    /**
     * Retrieves a paginated list of recruitment information based on provided parameters.
     *
     * @param companyName    The name of the company associated with the recruitment information.
     * @param departmentName The name of the department associated with the recruitment information.
     * @param jobTitle       The job title associated with the recruitment information.
     * @param current        The current page for pagination.
     * @param size           The number of records per page for pagination.
     * @return HTTP result containing the paginated list of recruitment information matching the
     * criteria.
     */
    @Override
    public HttpResult queryRecruitmentInfo(
            String companyName, String departmentName, String jobTitle, Integer current, Integer size) {
        Page<AdminRecruitmentVo> page = new Page<>(current, size);
        return HttpResult.success(
                administratorDao.queryRecruitment(page, companyName, departmentName, jobTitle), "查询成功");
    }

    // 审核招聘信息

    /**
     * Audits recruitment information based on various criteria and updates the status accordingly.
     *
     * @param enterpriseName The name of the enterprise associated with the recruitment information.
     * @param departmentName The name of the department associated with the recruitment information.
     * @param jobTitle       The job title of the recruitment information to be audited.
     * @param status         The new status to be set for the recruitment information.
     * @param rejectReason   The reason for rejection if the status is set to '3' (rejected).
     * @return HTTP result indicating the success or failure of the audit process.
     */
    @Override
    public HttpResult auditRecruitmentInfo(
            String enterpriseName,
            String departmentName,
            String jobTitle,
            String status,
            String rejectReason) {
        QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
        enterpriseQueryWrapper.eq("enterprise_name", enterpriseName);
        Enterprise enterprise = enterpriseDao.selectOne(enterpriseQueryWrapper);
        if (Objects.isNull(enterprise)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper
                .eq("name", departmentName)
                .eq("enterprise_id", enterprise.getEnterpriseId());
        Department department = departmentDao.selectOne(departmentQueryWrapper);
        if (Objects.isNull(department)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }

        UpdateWrapper<RecruitmentInfo> recruitmentInfoUpdateWrapper = new UpdateWrapper<>();
        recruitmentInfoUpdateWrapper
                .eq("enterprise_id", enterprise.getEnterpriseId())
                .eq("department_id", department.getDepartmentId())
                .eq("job_title", jobTitle)
                .set("status", status);
        if (status.equals("3")) {
            recruitmentInfoUpdateWrapper.set("rejection_reason", rejectReason);
        }
        if (recruitmentInfoDao.update(null, recruitmentInfoUpdateWrapper) > 0) {
            return HttpResult.success(jobTitle, "审批成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "审批失败");
    }

    // 创建公告

    /**
     * Creates a new announcement with the provided details.
     *
     * @param title    The title of the announcement.
     * @param cover    The cover image file associated with the announcement.
     * @param category The category of the announcement.
     * @param content  The content/body of the announcement.
     * @param data     Additional data/file attached to the announcement.
     * @return HTTP result indicating the success or failure of the announcement creation.
     */
    @Override
    public HttpResult createAnnouncement(
            String title, MultipartFile cover, String category, String content, MultipartFile data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            announcementDao.insert(
                    new Announcement(
                            null,
                            adminId,
                            title,
                            cover == null ? null : cover.getBytes(),
                            category,
                            content,
                            data == null ? null : data.getBytes(),
                            Timestamp.valueOf(format.format(date)),
                            "0",
                            null
                    ));
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "发布失败");
        }
        return HttpResult.success(title, "发布成功");
    }

    // 删除公告

    /**
     * Deletes the announcement identified by the given ID.
     *
     * @param id The unique identifier of the announcement to be deleted.
     * @return HTTP result indicating the success or failure of the deletion process.
     */
    @Override
    public HttpResult deleteAnnouncement(Integer id) {
        QueryWrapper<Announcement> announcementQueryWrapper = new QueryWrapper<>();
        announcementQueryWrapper.eq("announcement_id", id);
        if (announcementDao.delete(announcementQueryWrapper) > 0) {
            return HttpResult.success(id, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "对象已删除或不存在");
    }

    // 查询公告

    /**
     * Queries announcements based on title, category, and pagination parameters.
     *
     * @param title    The title of the announcement for filtering.
     * @param category The category of the announcement for filtering.
     * @param current  The current page number for pagination.
     * @return HTTP result containing the paginated list of announcements matching the criteria.
     */
    @Override
    public HttpResult queryAnnouncement(String title, String category, Integer current) {
        Page<AdminAnnouncementVo> page = new Page<>(current, 10);
        return HttpResult.success(administratorDao.queryAnnouncement(page, title, category), "查询成功");
    }

    // 获取公告封面
    // 分步获取可以减少加载延迟

    /**
     * Retrieves the cover image of a specific announcement.
     *
     * @param id The unique identifier of the announcement.
     * @return HTTP result containing the cover image of the specified announcement.
     */
    @Override
    public HttpResult queryAnnouncementCover(Integer id) {
        QueryWrapper<Announcement> announcementQueryWrapper = new QueryWrapper<>();
        announcementQueryWrapper.eq("announcement_id", id).select("cover");
        return HttpResult.success(announcementDao.selectOne(announcementQueryWrapper), "查询成功");
    }

    // 获取公告附件

    /**
     * Retrieves the additional data/file attached to a specific announcement.
     *
     * @param id The unique identifier of the announcement.
     * @return HTTP result containing the additional data/file of the specified announcement.
     */
    @Override
    public HttpResult queryAnnouncementData(Integer id) {
        QueryWrapper<Announcement> announcementQueryWrapper = new QueryWrapper<>();
        announcementQueryWrapper.eq("announcement_id", id).select("data");
        return HttpResult.success(announcementDao.selectOne(announcementQueryWrapper), "查询成功");
    }

    @Override
    public HttpResult setAnnouncementTop(Integer id) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UpdateWrapper<Announcement> announcementUpdateWrapper = new UpdateWrapper<>();
        announcementUpdateWrapper.eq("announcement_id", id).set("top", "1").set("top_time", Timestamp.valueOf(format.format(date)));
        if (announcementDao.update(null, announcementUpdateWrapper) > 0) {
            return HttpResult.success(id, "已置顶");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "置顶失败");
    }

    @Override
    public HttpResult setAnnouncementDown(Integer id) {
        UpdateWrapper<Announcement> announcementUpdateWrapper = new UpdateWrapper<>();
        announcementUpdateWrapper.eq("announcement_id", id).set("top", "0").set("top_time", null);
        if (announcementDao.update(null, announcementUpdateWrapper) > 0) {
            return HttpResult.success(id, "已取消置顶");
        }
        return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "取消置顶失败");
    }
}
