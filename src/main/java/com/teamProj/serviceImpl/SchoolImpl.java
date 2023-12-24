package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.entity.data.SchoolApplicationData;
import com.teamProj.entity.vo.SchoolApplicationDataVo;
import com.teamProj.entity.vo.SchoolFairVo;
import com.teamProj.entity.vo.SchoolStudentVo;
import com.teamProj.service.SchoolService;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SchoolImpl implements SchoolService {

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;

    @Resource
    private UserDao userDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private CollegeDao collegeDao;
    @Resource
    private MajorDao majorDao;

    @Resource
    private CareerFairDao careerFairDao;
    @Resource
    private JobApplicationDao jobApplicationDao;

    /**
     * Authenticates a school user's login based on the provided credentials.
     *
     * @param account  String representing the user's account or username.
     * @param password String representing the user's password.
     * @return HttpResult Result indicating the success or failure of the login attempt.
     */
    @Override
    public HttpResult schoolLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginUser.getPermissions().get(0).equals("school")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_id", userId);
        String schoolName = schoolDao.selectOne(schoolQueryWrapper).getSchoolName();
        map.put("token", jwt);
        map.put("schoolName", schoolName);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        return HttpResult.success(map, "登录成功");
    }

    /**
     * Logs out a school user from the system.
     *
     * @return HttpResult Result indicating the success or failure of the logout attempt.
     */
    @Override
    public HttpResult schoolLogout() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    /**
     * Allows a school user to change their password after verifying the old password.
     *
     * @param oldPassword String representing the old password for verification.
     * @param password    String representing the new password to be set.
     * @return HttpResult Result indicating the success or failure of the password change attempt.
     */
    @Override
    public HttpResult setSchoolPassword(String oldPassword, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        if (user != null) {
            if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
                userDao.updateById(user);
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("account").eq("user_id", user.getUserId());
                return HttpResult.success(userDao.selectOne(queryWrapper), "修改成功");
            }
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("account").eq("user_id", user.getUserId());
            return HttpResult.success(userDao.selectOne(queryWrapper), "密码错误");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Queries students based on specified criteria.
     *
     * @param name    The name of the student to search for.
     * @param majorId The ID of the major the student belongs to.
     * @param status  The status of the student.
     * @param current The current page number.
     * @param size    The number of items per page.
     * @return HttpResult Result containing the queried student information.
     */
    @Override
    public HttpResult queryStudent(
            String name, Integer majorId, Character status, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_id", schoolId);
        String schoolName = schoolDao.selectOne(schoolQueryWrapper).getSchoolName();
        Page<SchoolStudentVo> page = new Page<>(current, size);
        return HttpResult.success(
                studentDao.queryStudentWithMajor(page, name, schoolName, majorId, status), "查询成功");
    }

    /**
     * Resets a student's password to a default value.
     *
     * @param account The account/username of the student.
     * @return HttpResult Result indicating the success or failure of the password reset attempt.
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

    /**
     * Enables a student's account.
     *
     * @param account The account/username of the student.
     * @return HttpResult Result indicating the success or failure of enabling the student account.
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

    /**
     * Disables a student's account.
     *
     * @param account The account/username of the student.
     * @return HttpResult Result indicating the success or failure of disabling the student account.
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

    /**
     * Deletes a student's account.
     *
     * @param account The account/username of the student.
     * @return HttpResult Result indicating the success or failure of deleting the student account.
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

        QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
        majorQueryWrapper.eq("major_id", student.getMajorId());
        Major major = majorDao.selectOne(majorQueryWrapper);

        UpdateWrapper<College> collegeUpdateWrapper = new UpdateWrapper<>();
        collegeUpdateWrapper.eq("college_id", college.getCollegeId()).set("student_num", college.getStudentNum() - 1);
        collegeDao.update(null, collegeUpdateWrapper);

        UpdateWrapper<Major> majorUpdateWrapper = new UpdateWrapper<>();
        majorUpdateWrapper.eq("major_id", major.getMajorId()).set("student_num", major.getStudentNum() - 1);
        majorDao.update(null, majorUpdateWrapper);

        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Queries colleges based on specified criteria.
     *
     * @param name    The name of the college to search for.
     * @param current The current page number.
     * @param size    The number of items per page.
     * @return HttpResult Result containing the queried college information.
     */
    @Override
    public HttpResult queryCollege(String name, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<College> page = new Page<>(current, size);
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("college_name", name).eq("school_id", schoolId);
        return HttpResult.success(collegeDao.selectPage(page, queryWrapper), "查询成功");
    }

    /**
     * Creates a new college.
     *
     * @param name The name of the college to be created.
     * @return HttpResult Result indicating the success or failure of college creation.
     */
    @Override
    public HttpResult createCollege(String name) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        College college = new College();
        college.setCollegeName(name);
        college.setSchoolId(schoolId);
        college.setStudentNum(0);
        if (collegeDao.insert(college) > 0) {
            return HttpResult.success(college, "创建成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Deletes a college based on its ID.
     *
     * @param collegeId The ID of the college to be deleted.
     * @return HttpResult Result indicating the success or failure of deleting the college.
     */
    @Override
    public HttpResult deleteCollege(Integer collegeId) {
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", collegeId);
        if (collegeDao.delete(queryWrapper) > 0) {
            return HttpResult.success(collegeId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Edits a college's information.
     *
     * @param collegeId The ID of the college to be edited.
     * @param name      The updated name of the college.
     * @return HttpResult Result indicating the success or failure of editing the college.
     */
    @Override
    public HttpResult editCollege(Integer collegeId, String name) {
        UpdateWrapper<College> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("college_id", collegeId).set("college_name", name);
        if (collegeDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(collegeId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Queries majors within a college based on specified criteria.
     *
     * @param name      The name of the major to search for.
     * @param current   The current page number.
     * @param size      The number of items per page.
     * @param collegeId The ID of the college associated with the majors.
     * @return HttpResult Result containing the queried major information.
     */
    @Override
    public HttpResult queryMajor(String name, Integer current, Integer size, Integer collegeId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<Major> page = new Page<>(current, size);
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("major_name", name).eq("college_id", collegeId).eq("school_id", schoolId);
        return HttpResult.success(majorDao.selectPage(page, queryWrapper), "查询成功");
    }

    /**
     * Creates a new major within a college.
     *
     * @param name      The name of the major to be created.
     * @param collegeId The ID of the college to which the major belongs.
     * @return HttpResult Result indicating the success or failure of major creation.
     */
    @Override
    public HttpResult createMajor(String name, Integer collegeId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Major major = new Major();
        major.setMajorName(name);
        major.setCollegeId(collegeId);
        major.setSchoolId(schoolId);
        major.setStudentNum(0);
        if (majorDao.insert(major) > 0) {
            return HttpResult.success(major, "创建成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Deletes a major based on its ID.
     *
     * @param majorId The ID of the major to be deleted.
     * @return HttpResult Result indicating the success or failure of deleting the major.
     */
    @Override
    public HttpResult deleteMajor(Integer majorId) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major_id", majorId);
        if (majorDao.delete(queryWrapper) > 0) {
            return HttpResult.success(majorId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Edits a major's information.
     *
     * @param majorId The ID of the major to be edited.
     * @param name    The updated name of the major.
     * @return HttpResult Result indicating the success or failure of editing the major.
     */
    @Override
    public HttpResult editMajor(Integer majorId, String name) {
        UpdateWrapper<Major> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("major_id", majorId).set("major_name", name);
        if (majorDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(majorId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Audits a career fair by updating its status and reason.
     *
     * @param careerFairId The ID of the career fair to be audited.
     * @param status       The status to set for the career fair.
     * @param reason       The reason for the audit outcome.
     * @return HttpResult Result indicating the success or failure of the audit process.
     */
    @Override
    public HttpResult auditCareerFair(Integer careerFairId, String status, String reason) {
        UpdateWrapper<CareerFair> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("fair_id", careerFairId).set("status", status).set("reason", reason);
        if (careerFairDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(careerFairId, "审核成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    /**
     * Queries career fairs based on specified criteria.
     *
     * @param name    The name of the career fair to search for.
     * @param current The current page number.
     * @param size    The number of items per page.
     * @return HttpResult Result containing the queried career fair information.
     */
    @Override
    public HttpResult queryCareerFair(String name, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<SchoolFairVo> page = new Page<>(current, size);
        return HttpResult.success(careerFairDao.queryFair(page, name, schoolId), "查询成功");
    }

    /**
     * Queries the number of career fairs.
     *
     * @return HttpResult Result containing the number of career fairs.
     */
    @Override
    public HttpResult queryCareerFairNum() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<CareerFair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "0").eq("school_id", user.getUserId());
        return HttpResult.success(careerFairDao.selectCount(queryWrapper), "查询成功");
    }

    /**
     * Queries the number of career fairs today.
     *
     * @return HttpResult Result containing the number of career fairs today.
     */
    @Override
    public HttpResult queryCareerFairToday(){
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<CareerFair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "1").eq("school_id", user.getUserId()).le("start_time", new Timestamp(System.currentTimeMillis())).ge("end_time", new Timestamp(System.currentTimeMillis()));
        System.out.println(LocalDate.now());
        return HttpResult.success(careerFairDao.selectList(queryWrapper), "查询成功");
    }

    @Override
    public HttpResult applicationData() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        QueryWrapper<Student> queryStudent = new QueryWrapper<>();
        queryStudent.eq("school_id", schoolId);
        List<Student> students = studentDao.selectList(queryStudent);
        List<SchoolApplicationDataVo> schoolApplicationDataVos =
                jobApplicationDao.querySchoolApplicationData(schoolId);
        SchoolApplicationData schoolApplicationData = new SchoolApplicationData();
        schoolApplicationData.setTotal(students.size());


        QueryWrapper<College> collegeQueryWrapper = new QueryWrapper<>();
        collegeQueryWrapper.eq("school_id", schoolId);
        List<College> colleges = collegeDao.selectList(collegeQueryWrapper);

        QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
        majorQueryWrapper.eq("school_id", schoolId);
        List<Major> majors = majorDao.selectList(majorQueryWrapper);

        for(College college : colleges){
            SchoolApplicationData.CollegeApplicationData collegeApplicationData = new SchoolApplicationData.CollegeApplicationData();
            collegeApplicationData.setTotal(0);
            collegeApplicationData.setStudentNum(0);
            collegeApplicationData.setCity(new HashMap<>());
            collegeApplicationData.setEnterprise(new HashMap<>());
            Map<String, SchoolApplicationData.MajorApplicationData> major = new HashMap<>();
            for(Major major1 : majors){
                if(major1.getCollegeId().equals(college.getCollegeId())){
                    SchoolApplicationData.MajorApplicationData majorApplicationData = new SchoolApplicationData.MajorApplicationData();
                    majorApplicationData.setTotal(major1.getStudentNum());
                    majorApplicationData.setStudentNum(0);
                    majorApplicationData.setCity(new HashMap<>());
                    majorApplicationData.setEnterprise(new HashMap<>());
                    major.put(major1.getMajorName(), majorApplicationData);
                }
            }
            collegeApplicationData.setMajor(major);
            schoolApplicationData.getCollege().put(college.getCollegeName(), collegeApplicationData);
        }

        for (Student student : students) {
            for (SchoolApplicationDataVo dataVo : schoolApplicationDataVos) {
                if (student.getStudentId().equals(dataVo.getStudentId())) {
                    // 仅统计已通过的
                    if (!dataVo.getStatus().equals("2")) continue;

                    // 修改school-num
                    schoolApplicationData.setStudentNum(schoolApplicationData.getStudentNum() + 1);
                    // 修改school-enterprise
                    Map<String, Integer> schoolEnterprise = schoolApplicationData.getEnterprise();
                    if (schoolEnterprise.containsKey(dataVo.getEnterpriseName())) {
                        schoolEnterprise.put(
                                dataVo.getEnterpriseName(), schoolEnterprise.get(dataVo.getEnterpriseName()) + 1);
                    } else {
                        schoolEnterprise.put(dataVo.getEnterpriseName(), 1);
                    }
                    schoolApplicationData.setEnterprise(schoolEnterprise);

                    // 修改school-city
                    Map<String, Integer> schoolCity = schoolApplicationData.getCity();
                    if (schoolCity.containsKey(dataVo.getCity())) {
                        schoolCity.put(dataVo.getCity(), schoolCity.get(dataVo.getCity()) + 1);
                    } else {
                        schoolCity.put(dataVo.getCity(), 1);
                    }
                    schoolApplicationData.setCity(schoolCity);

                    // 修改college
                    if (schoolApplicationData.getCollege().containsKey(dataVo.getCollegeName())) {

                        SchoolApplicationData.CollegeApplicationData collegeApplicationData =
                                schoolApplicationData.getCollege().get(dataVo.getCollegeName());
                        // 修改college-num
                        collegeApplicationData.setStudentNum(collegeApplicationData.getStudentNum() + 1);

                        // 修改college-city
                        Map<String, Integer> collegeCity = collegeApplicationData.getCity();
                        if (collegeCity.containsKey(dataVo.getCity())) {
                            collegeCity.put(dataVo.getCity(), collegeCity.get(dataVo.getCity()) + 1);
                            collegeApplicationData.setCity(collegeCity);
                        } else {
                            collegeCity.put(dataVo.getCity(), 1);
                            collegeApplicationData.setCity(collegeCity);
                        }

                        // 修改college-enterprise
                        Map<String, Integer> collegeEnterprise = collegeApplicationData.getEnterprise();
                        if (collegeEnterprise.containsKey(dataVo.getEnterpriseName())) {
                            collegeEnterprise.put(
                                    dataVo.getEnterpriseName(),
                                    collegeEnterprise.get(dataVo.getEnterpriseName()) + 1);
                            collegeApplicationData.setEnterprise(collegeEnterprise);
                        } else {
                            collegeEnterprise.put(dataVo.getEnterpriseName(), 1);
                            collegeApplicationData.setEnterprise(collegeEnterprise);
                        }

                        // 修改major
                        if (collegeApplicationData.getMajor().containsKey(dataVo.getMajorName())) {
                            SchoolApplicationData.MajorApplicationData majorApplicationData =
                                    collegeApplicationData.getMajor().get(dataVo.getMajorName());
                            // 修改major-num
                            majorApplicationData.setStudentNum(majorApplicationData.getStudentNum() + 1);

                            // 修改major-city
                            Map<String, Integer> majorCity = majorApplicationData.getCity();
                            if (majorCity.containsKey(dataVo.getCity())) {
                                majorCity.put(dataVo.getCity(), majorCity.get(dataVo.getCity()) + 1);
                                majorApplicationData.setCity(majorCity);
                            } else {
                                majorCity.put(dataVo.getCity(), 1);
                                majorApplicationData.setCity(majorCity);
                            }

                            // 修改major-enterprise

                            Map<String, Integer> majorEnterprise = majorApplicationData.getEnterprise();
                            if (majorEnterprise.containsKey(dataVo.getEnterpriseName())) {
                                majorEnterprise.put(
                                        dataVo.getEnterpriseName(),
                                        majorEnterprise.get(dataVo.getEnterpriseName()) + 1);
                                majorApplicationData.setEnterprise(majorEnterprise);
                            } else {
                                majorEnterprise.put(dataVo.getEnterpriseName(), 1);
                                majorApplicationData.setEnterprise(majorEnterprise);
                            }

                            Map<String, SchoolApplicationData.MajorApplicationData> map =
                                    collegeApplicationData.getMajor();
                            map.put(dataVo.getMajorName(), majorApplicationData);
                            collegeApplicationData.setMajor(map);
                        } else {
                            Map<String, SchoolApplicationData.MajorApplicationData> map =
                                    collegeApplicationData.getMajor();
                            SchoolApplicationData.MajorApplicationData majorApplicationData =
                                    new SchoolApplicationData.MajorApplicationData();
                            Map<String, Integer> city = new HashMap<>();
                            city.put(dataVo.getCity(), 1);
                            majorApplicationData.setCity(city);
                            Map<String, Integer> enterprise = new HashMap<>();
                            enterprise.put(dataVo.getEnterpriseName(), 1);
                            majorApplicationData.setEnterprise(enterprise);
                            majorApplicationData.setStudentNum(1);
                            QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
                            queryWrapper
                                    .eq("major_name", dataVo.getMajorName())
                                    .eq(
                                            "college_id",
                                            collegeDao
                                                    .selectOne(
                                                            new QueryWrapper<College>()
                                                                    .eq("college_name", dataVo.getCollegeName())
                                                                    .eq("school_id", schoolId))
                                                    .getCollegeId())
                                    .eq("school_id", schoolId);
                            majorApplicationData.setTotal(majorDao.selectOne(queryWrapper).getStudentNum());
                            map.put(dataVo.getMajorName(), majorApplicationData);

                            collegeApplicationData.setMajor(map);
                        }

                    } else {
                        Map<String, SchoolApplicationData.CollegeApplicationData> map =
                                schoolApplicationData.getCollege();
                        // 新建一个college
                        SchoolApplicationData.CollegeApplicationData collegeApplicationData =
                                new SchoolApplicationData.CollegeApplicationData();
                        // 设置num
                        collegeApplicationData.setStudentNum(1);
                        // 设置total
                        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("college_name", dataVo.getCollegeName()).eq("school_id", schoolId);
                        College college = collegeDao.selectOne(queryWrapper);
                        collegeApplicationData.setTotal(college.getStudentNum());
                        // 设置college-city
                        Map<String, Integer> city = new HashMap<>();
                        city.put(dataVo.getCity(), 1);
                        collegeApplicationData.setCity(city);
                        // 设置college-enterprise
                        Map<String, Integer> enterprise = new HashMap<>();
                        enterprise.put(dataVo.getEnterpriseName(), 1);
                        collegeApplicationData.setEnterprise(enterprise);
                        // 设置college-major
                        // 新建major
                        Map<String, SchoolApplicationData.MajorApplicationData> major =
                                collegeApplicationData.getMajor();
                        SchoolApplicationData.MajorApplicationData majorApplicationData =
                                new SchoolApplicationData.MajorApplicationData();
                        // 设置major-num
                        majorApplicationData.setStudentNum(1);
                        // 设置major-total
                        QueryWrapper<Major> queryWrapper1 = new QueryWrapper<>();
                        queryWrapper1
                                .eq("major_name", dataVo.getMajorName())
                                .eq("college_id", college.getCollegeId())
                                .eq("school_id", schoolId);
                        majorApplicationData.setTotal(majorDao.selectOne(queryWrapper1).getStudentNum());
                        // 设置major-city
                        city = new HashMap<>();
                        city.put(dataVo.getCity(), 1);
                        majorApplicationData.setCity(city);
                        // 设置major-enterprise
                        enterprise = new HashMap<>();
                        enterprise.put(dataVo.getEnterpriseName(), 1);
                        majorApplicationData.setEnterprise(enterprise);
                        // 写入college-major
                        major.put(dataVo.getMajorName(), majorApplicationData);
                        collegeApplicationData.setMajor(major);

                        // 写入school-college
                        map.put(dataVo.getCollegeName(), collegeApplicationData);
                        schoolApplicationData.setCollege(map);
                    }
                }
            }
        }

        schoolApplicationData.setCity(
                SchoolApplicationData.sortMapByValue(schoolApplicationData.getCity()));
        schoolApplicationData.setEnterprise(
                SchoolApplicationData.sortMapByValue(schoolApplicationData.getEnterprise()));
        Map<String, SchoolApplicationData.CollegeApplicationData> college =
                schoolApplicationData.getCollege();
        for (Map.Entry<String, SchoolApplicationData.CollegeApplicationData> entry :
                college.entrySet()) {
            entry.getValue().setCity(SchoolApplicationData.sortMapByValue(entry.getValue().getCity()));
            entry
                    .getValue()
                    .setEnterprise(SchoolApplicationData.sortMapByValue(entry.getValue().getEnterprise()));
            Map<String, SchoolApplicationData.MajorApplicationData> major = entry.getValue().getMajor();
            for (Map.Entry<String, SchoolApplicationData.MajorApplicationData> entry1 :
                    major.entrySet()) {
                entry1
                        .getValue()
                        .setCity(SchoolApplicationData.sortMapByValue(entry1.getValue().getCity()));
                entry1
                        .getValue()
                        .setEnterprise(SchoolApplicationData.sortMapByValue(entry1.getValue().getEnterprise()));
            }
        }
        schoolApplicationData.setCollege(college);
        return HttpResult.success(schoolApplicationData, "查询成功");
    }
}
