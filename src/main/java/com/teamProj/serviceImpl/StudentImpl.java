package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.entity.vo.CollegeMajorVo;
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
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.teamProj.utils.EditByword.editByword;
import static com.teamProj.utils.EditByword.getScore;

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
    @Resource
    private InterviewInfoDao interviewInfoDao;
    @Resource
    private CareerFairDao careerFairDao;
    @Resource
    private MessageDao messageDao;
    @Resource
    private CollegeDao collegeDao;
    @Resource
    private MajorDao majorDao;
    @Resource
    private SMS sms;

    /**
     * Authenticates a student based on account credentials.
     *
     * @param account  The student's account.
     * @param password The student's password.
     * @return HttpResult containing the authentication token on successful login.
     */
    @Override
    public HttpResult studentLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginUser.getPermissions().get(0).equals("student")) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", loginUser.getUser().getUserId());
        Student student = studentDao.selectOne(queryWrapper);
        if (!student.getUserStatus().equals("1")){
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "账号已被禁用");
        }
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("student_id", loginUser.getUser().getUserId())
                .set("login_time", new Timestamp(System.currentTimeMillis()));
        studentDao.update(null, updateWrapper);
        return HttpResult.success(map, "登录成功");
    }

    /**
     * Logs out a student.
     *
     * @return HttpResult indicating successful user logout.
     */
    public HttpResult studentLogout() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int studentId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(studentId));
        return HttpResult.success(null, "用户注销");
    }

    /**
     * Sets a new password for a student's account.
     *
     * @param account     The student's account.
     * @param oldPassword The student's old password.
     * @param password    The new password to be set.
     * @return HttpResult indicating the success or failure of the password update.
     */
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
                UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
                updateWrapper
                        .eq("student_id", user.getUserId())
                        .set("change_password_time", new Timestamp(System.currentTimeMillis()));
                studentDao.update(null, updateWrapper);
                return HttpResult.success(userDao.selectOne(queryWrapper), "修改成功");
            }
            return HttpResult.success(userDao.selectOne(queryWrapper), "密码错误");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Resets a student's password in case of forgetfulness.
     *
     * @param account  The student's account.
     * @param password The new password to be set.
     * @return HttpResult indicating the success or failure of the password reset.
     */
    @Override
    public HttpResult setStudentPasswordForget(String account, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userDao.update(user, queryWrapper);
            queryWrapper.select("account");
            UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("student_id", user.getUserId())
                    .set("change_password_time", new Timestamp(System.currentTimeMillis()));
            studentDao.update(null, updateWrapper);
            return HttpResult.success(userDao.selectOne(queryWrapper), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Registers a new student into the system.
     *
     * @param account     The student's account.
     * @param password    The student's password.
     * @param schoolName  The name of the student's school.
     * @param collegeName The name of the student's college.
     * @param majorName   The name of the student's major.
     * @param name        The student's name.
     * @param phoneNumber The student's phone number.
     * @return HttpResult indicating the success or failure of the student registration.
     */
    @Override
    public HttpResult studentRegister(
            String account,
            String password,
            String schoolName,
            String collegeName,
            String majorName,
            String name,
            String phoneNumber) {
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
        QueryWrapper<College> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("college_name", collegeName).eq("school_id", school.getSchoolId());
        College college = collegeDao.selectOne(queryWrapper2);
        QueryWrapper<Major> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("major_name", majorName).eq("college_id", college.getCollegeId());
        Major major = majorDao.selectOne(queryWrapper3);
        // Date date = new java.sql.Date(new java.util.Date().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Student student =
                new Student(
                        userDao.selectOne(queryWrapper).getUserId(),
                        school.getSchoolId(),
                        college.getCollegeId(),
                        major.getMajorId(),
                        name,
                        phoneNumber,
                        "1",
                        new Timestamp(System.currentTimeMillis()),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()));
        if (studentDao.insert(student) > 0) {
            collegeDao.update(
                    null,
                    new UpdateWrapper<College>()
                            .eq("college_id", college.getCollegeId())
                            .set("student_num", college.getStudentNum() + 1));
            majorDao.update(
                    null,
                    new UpdateWrapper<Major>()
                            .eq("major_id", major.getMajorId())
                            .set("student_num", major.getStudentNum() + 1));
            return HttpResult.success(student, "注册成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    /**
     * Queries educational institutions based on depth and query information.
     *
     * @param depth     The depth of the query: 0 for schools, 1 for colleges, 2 for majors.
     * @param queryInfo Additional query information based on the depth provided.
     * @return HttpResult containing the query results or an error message.
     */
    @Override
    public HttpResult querySchool(Integer depth, String queryInfo) {
        if (depth == 0) {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("school_id", "school_name");
            return HttpResult.success(schoolDao.selectList(queryWrapper), "查询成功");
        } else if (depth == 1) {
            QueryWrapper<College> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("school_id", Integer.parseInt(queryInfo))
                    .select("college_name", "college_id");
            return HttpResult.success(collegeDao.selectList(queryWrapper), "查询成功");
        } else if (depth == 2) {
            QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("college_id", Integer.parseInt(queryInfo)).select("major_name", "major_id");
            return HttpResult.success(majorDao.selectList(queryWrapper), "查询成功");
        } else {
            return HttpResult.success(null, "参数非法");
        }
    }

    /**
     * Sends an email for verification purposes.
     *
     * @param email The email address for verification.
     * @return HttpResult containing the verification code or an error message.
     */
    @Override
    public HttpResult verificationEmail(String email) {
        EmailVerification emailVerification = new EmailVerification();
        String captcha = emailVerification.verificationService(email);
        if (captcha != null) {
            return HttpResult.success(captcha, "发送成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "发送失败");
        }
    }

    /**
     * Sends a verification code to a phone number.
     *
     * @param phone The phone number for verification.
     * @return HttpResult containing the message ID or an error message.
     * @throws Exception if an error occurs during the verification process.
     */
    @Override
    public HttpResult verificationPhone(String phone) throws Exception {
        Map<String, Object> param = sms.sendMessage(phone, phone);
        if ((boolean) param.get("success")) {
            return HttpResult.success(param.get("messageId"), "发送成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "发送失败");
        }
    }

    /**
     * Checks the verification code sent to a phone number.
     *
     * @param messageId The message ID associated with the verification.
     * @param code      The verification code to be checked.
     * @return HttpResult indicating success or failure of the verification.
     */
    @Override
    public HttpResult verificationPhoneCheck(String messageId, String code) {
        try {
            if (sms.checkMessage(messageId, code)) {
                return HttpResult.success(null, "验证成功");
            } else {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "验证失败");
            }
        } catch (Exception e) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "验证失败");
        }
    }

    /**
     * Creates a resume for a student.
     *
     * @param account               The account of the student.
     * @param imageByte             The image file representing the student.
     * @param selfDescription       Description provided by the student about themselves.
     * @param careerObjective       The career objective or goal of the student.
     * @param educationExperience   Details of the student's educational background and experiences.
     * @param internshipExperience  Information regarding the student's internship experiences.
     * @param projectExperience     Details of the projects the student has been involved in.
     * @param certificates          Certificates or achievements earned by the student.
     * @param skills                Skills possessed by the student.
     * @param resumeName            The name or title for the created resume.
     * @return                      An HttpResult indicating the success or failure of the resume creation.
     */
    @Override
    public HttpResult createResume(String account, MultipartFile imageByte, String selfDescription,
                                   String careerObjective, String educationExperience, String internshipExperience,
                                   String projectExperience, String certificates, String skills, String resumeName
            /*Resume resume*/) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //                Image image = null;
        //
        //                if (imageFile != null && !imageFile.isEmpty()) {
        //                    byte[] imageByte;
        //                    try {
        //                        imageByte = imageFile.getBytes();
        //                    } catch (IOException e) {
        //                        throw new RuntimeException(e);
        //                    }
        //
        //                    //image = new Image(ImageDataFactory.create(imageByte));
        //                }
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
            resume = new Resume(null, student.getStudentId(), timestamp, resumeName,
                    selfDescription, careerObjective, educationExperience, internshipExperience,
                    projectExperience, certificates, skills, imageByte.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resume.setStudentId(student.getStudentId());
        resume.setCreationTime(timestamp);

        if (resumeDao.insert(resume) > 0) {
            return HttpResult.success(resume, "创建成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    /**
     * Queries resumes associated with the authenticated user.
     *
     * @return HttpResult containing the list of resumes associated with the user.
     */
    @Override
    public HttpResult queryResume() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        return HttpResult.success(resumeDao.queryResume(user.getUserId()), "查询成功");
    }

    /**
     * Deletes a specific resume associated with the authenticated user.
     *
     * @param resumeId The ID of the resume to delete.
     * @return HttpResult indicating success or failure of the deletion operation.
     */
    @Override
    public HttpResult deleteResume(Integer resumeId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
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

    /**
     * Edits details of a specific resume associated with the authenticated user.
     *
     * @param resumeId             The ID of the resume to edit.
     * @param selfDescription      The updated self-description.
     * @param careerObjective      The updated career objective.
     * @param educationExperience  The updated education experience.
     * @param internshipExperience The updated internship experience.
     * @param projectExperience    The updated project experience.
     * @param certificates         The updated certificates information.
     * @param skills               The updated skills information.
     * @param resumeName           The updated name of the resume.
     * @return HttpResult containing the modified resume details if successful, or an error if not
     * found.
     */
    @Override
    public HttpResult editResume(
            String resumeId,
            String selfDescription,
            String careerObjective,
            String educationExperience,
            String internshipExperience,
            String projectExperience,
            String certificates,
            String skills,
            String resumeName) {
        UpdateWrapper<Resume> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("resume_id", resumeId);
        updateWrapper
                .set("self_description", selfDescription)
                .set("career_objective", careerObjective)
                .set("education_experience", educationExperience)
                .set("internship_experience", internshipExperience)
                .set("project_experience", projectExperience)
                .set("certificates", certificates)
                .set("skills", skills)
                .set("resume_name", resumeName);
        if (resumeDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(resumeDao.selectById(resumeId), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Retrieves detailed information about a specific resume.
     *
     * @param resumeId The ID of the resume to retrieve details for.
     * @return HttpResult containing the detailed resume information if found, otherwise an error.
     */
    @Override
    public HttpResult queryResumeDetail(Integer resumeId) {
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

    /**
     * Updates the information of a student.
     *
     * @param account   The account associated with the student.
     * @param name      The updated name of the student.
     * @param gender    The updated gender of the student.
     * @param wechat    The updated WeChat ID of the student.
     * @param qq        The updated QQ ID of the student.
     * @param collegeId The updated college ID of the student.
     * @param majorId   The updated major ID of the student.
     * @param address   The updated address of the student.
     * @param age       The updated age of the student.
     * @return HttpResult containing the modified student information if successful, or an error if
     * not found.
     */
    @Override
    public HttpResult setStudentInfo(
            String account,
            String name,
            String gender,
            String wechat,
            String qq,
            Integer collegeId,
            Integer majorId,
            String address,
            Integer age) {
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("account", account);
        User user = userDao.selectOne(queryWrapper1);
        int studentId = user.getUserId();

        QueryWrapper<Student> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("student_id", studentId);
        Student oldStudent = studentDao.selectOne(queryWrapper2);

        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_id", studentId);
        Student student = new Student();
        student.setName(name);
        if (gender != null && !gender.isEmpty()) {
            student.setGender(gender);
        }
        if (wechat != null && !wechat.isEmpty()) {
            student.setWechat(wechat);
        }
        if (qq != null && !qq.isEmpty()) {
            student.setQq(qq);
        }
        if (collegeId != null) {
            QueryWrapper<College> collegeQueryWrapper = new QueryWrapper<>();
            collegeQueryWrapper.eq("college_id", collegeId);
            College college = collegeDao.selectOne(collegeQueryWrapper);

            UpdateWrapper<College> collegeUpdateWrapper = new UpdateWrapper<>();
            collegeUpdateWrapper.eq("college_id", oldStudent.getCollegeId()).set("student_num", college.getStudentNum() - 1);
            collegeDao.update(null, collegeUpdateWrapper);

            student.setCollegeId(collegeId);

            QueryWrapper<College> newCollegeQueryWrapper = new QueryWrapper<>();
            newCollegeQueryWrapper.eq("college_id", collegeId);
            College newCollege = collegeDao.selectOne(newCollegeQueryWrapper);

            UpdateWrapper<College> newCollegeUpdateWrapper = new UpdateWrapper<>();
            newCollegeUpdateWrapper.eq("college_id", collegeId).set("student_num", newCollege.getStudentNum() + 1);
            collegeDao.update(null, newCollegeUpdateWrapper);
        }
        if (majorId != null) {
            QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
            majorQueryWrapper.eq("major_id", majorId);
            Major major = majorDao.selectOne(majorQueryWrapper);

            UpdateWrapper<Major> majorUpdateWrapper = new UpdateWrapper<>();
            majorUpdateWrapper.eq("major_id", oldStudent.getMajorId()).set("student_num", major.getStudentNum() - 1);
            majorDao.update(null, majorUpdateWrapper);

            student.setMajorId(majorId);

            QueryWrapper<Major> newMajorQueryWrapper = new QueryWrapper<>();
            newMajorQueryWrapper.eq("major_id", majorId);
            Major newMajor = majorDao.selectOne(newMajorQueryWrapper);

            UpdateWrapper<Major> newMajorUpdateWrapper = new UpdateWrapper<>();
            newMajorUpdateWrapper.eq("major_id", majorId).set("student_num", newMajor.getStudentNum() + 1);
            majorDao.update(null, newMajorUpdateWrapper);
        }
        if (address != null && !address.isEmpty()) {
            student.setAddress(address);
        }
        if (age != null) {
            student.setAge(age);
        }
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("student_id", studentId)
                .select("name", "gender", "college_id", "major_id", "address", "age", "wechat", "qq");
        if (studentDao.update(student, updateWrapper) > 0) {
            return HttpResult.success(studentDao.selectOne(queryWrapper), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Queries recruitment information based on various parameters and user preferences.
     *
     * @param account   The account of the user querying recruitment information.
     * @param queryInfo The information used to query recruitment details.
     * @param minSalary The minimum salary threshold for job searching.
     * @param maxSalary The maximum salary threshold for job searching.
     * @param mark      Indicates whether to retrieve marked (favorited) recruitment info.
     * @return HttpResult containing the queried recruitment information or an error message.
     */
    @Override
    public HttpResult queryRecruitmentInfo(
            String account, String queryInfo, String minSalary, String maxSalary, boolean mark) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        if (mark) {
            return HttpResult.success(
                    recruitmentInfoDao.queryMarkedRecruitment(
                            queryInfo, maxSalary, minSalary, user.getUserId()),
                    "查询成功");
        } else {
            //            QueryWrapper<RecruitmentInfo> queryWrapper = new QueryWrapper<>();
            //            if (queryInfo != null && !queryInfo.isEmpty()) {
            //                queryWrapper.like("company_name", queryInfo).or().like("job_title",
            // queryInfo);
            //            }
            //            if (minSalary != null && maxSalary != null && !minSalary.isEmpty() &&
            // !maxSalary.isEmpty()) {
            //                queryWrapper.ge("min_salary", minSalary).le("max_salary", maxSalary);
            //            }
            //            queryWrapper.select("recruitment_id", "job_title", "company_name", "city",
            // "min_salary", "max_salary", "byword");
            //            return HttpResult.success(recruitmentInfoDao.selectList(queryWrapper), "查询成功");
            return HttpResult.success(
                    recruitmentInfoDao.queryRecruitmentInfo(
                            queryInfo, maxSalary, minSalary, user.getUserId()),
                    "查询成功");
        }
    }

    /**
     * Marks or unmarks a recruitment information as a favorite by a student.
     *
     * @param account           The account of the student marking the recruitment info.
     * @param RecruitmentInfoId The ID of the recruitment information being marked.
     * @return HttpResult containing the result of the marking operation or an error message.
     */
    @Override
    public HttpResult markRecruitmentInfo(String account, Integer RecruitmentInfoId) {
        QueryWrapper<MarkedRecruitmentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("recruitment_id", RecruitmentInfoId)
                .eq(
                        "student_id",
                        userDao.selectOne(new QueryWrapper<User>().eq("account", account)).getUserId());
        if (markedRecruitmentInfoDao.selectOne(queryWrapper) != null) {
            markedRecruitmentInfoDao.delete(queryWrapper);
            return HttpResult.success(null, "取消收藏成功");
        } else {
            MarkedRecruitmentInfo mri = new MarkedRecruitmentInfo();
            mri.setRecruitmentId(RecruitmentInfoId);
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("account", account);
            mri.setStudentId(userDao.selectOne(queryWrapper1).getUserId());
            if (markedRecruitmentInfoDao.insert(mri) > 0) {
                QueryWrapper<RecruitmentInfo> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("recruitment_id", RecruitmentInfoId);
                RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(queryWrapper2);
                QueryWrapper<Student> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.eq("student_id", mri.getStudentId());
                Student student = studentDao.selectOne(queryWrapper3);
                UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
                updateWrapper
                        .eq("student_id", student.getStudentId())
                        .set("byword", editByword(student.getByword(), recruitmentInfo.getByword()));
                return HttpResult.success(mri, "收藏成功");
            } else {
                return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
            }
        }
    }

    /**
     * Creates a job application for a specific recruitment information and resume.
     *
     * @param account           The account of the student applying for the job.
     * @param recruitmentInfoId The ID of the recruitment information the student is applying to.
     * @param resumeId          The ID of the resume used for the job application.
     * @return HttpResult containing the created job application or an error message.
     */
    @Override
    public HttpResult createJobApplication(
            String account, Integer recruitmentInfoId, Integer resumeId) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setRecruitmentId(recruitmentInfoId);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        jobApplication.setStudentId(userDao.selectOne(queryWrapper).getUserId());
        jobApplication.setResumeId(resumeId);
        jobApplication.setApplicationTime(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        jobApplication.setStatus('0');
        jobApplication.setEnterpriseVisible("1");
        if (jobApplicationDao.insert(jobApplication) > 0) {
            QueryWrapper<RecruitmentInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("recruitment_id", recruitmentInfoId);
            RecruitmentInfo recruitmentInfo = recruitmentInfoDao.selectOne(queryWrapper1);
            QueryWrapper<Student> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("student_id", jobApplication.getStudentId());
            Student student = studentDao.selectOne(queryWrapper2);
            UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("student_id", student.getStudentId())
                    .set("byword", editByword(student.getByword(), recruitmentInfo.getByword()));
            studentDao.update(null, updateWrapper);
            return HttpResult.success(jobApplication, "创建成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    /**
     * Queries detailed information about a specific job application.
     *
     * @param applicationId The ID of the job application.
     * @return HttpResult containing detailed information about the job application or an error
     * message.
     */
    @Override
    public HttpResult queryJobApplicationDetail(Integer applicationId) {
        QueryWrapper<JobApplication> queryWrapper0 = new QueryWrapper<>();
        JobApplication jobApplication =
                jobApplicationDao.selectOne(queryWrapper0.eq("application_id", applicationId));
        QueryWrapper<RecruitmentInfo> queryWrapper1 = new QueryWrapper<>();
        RecruitmentInfo recruitmentInfo =
                recruitmentInfoDao.selectOne(
                        queryWrapper1.eq("recruitment_id", jobApplication.getRecruitmentId()));
        QueryWrapper<Resume> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("resume_id", jobApplication.getResumeId());
        Map<String, String> map = new HashMap<>();
        map.put("投报职位", recruitmentInfo.getJobTitle());
        map.put("投报日期", jobApplication.getApplicationTime());
        map.put("投报公司名", recruitmentInfo.getCompanyName());
        String status = "" + jobApplication.getStatus();
        status =
                switch (status) {
                    case "0" -> "未审核";
                    case "2" -> "已安排面试";
                    case "1" -> "未通过";
                    default -> status;
                };
        map.put("简历情况", status);
        map.put("薪资情况", recruitmentInfo.getMinSalary() + "-" + recruitmentInfo.getMaxSalary());
        map.put("投递简历名", resumeDao.selectOne(queryWrapper2).getResumeName());
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Queries job applications associated with a specific user account.
     *
     * @param account The account of the user querying job applications.
     * @return HttpResult containing job application information or an error message.
     */
    @Override
    public HttpResult queryJobApplication(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);
        return HttpResult.success(jobApplicationDao.queryJobApplication(user.getUserId()), "查询成功");
    }

    /**
     * Deletes a job application based on its ID.
     *
     * @param applicationId The ID of the job application to be deleted.
     * @return HttpResult indicating the deletion success or an error message.
     */
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

    /**
     * Queries student information associated with the currently logged-in user.
     *
     * @return HttpResult containing student information or an error message.
     */
    @Override
    public HttpResult queryStudentInfo() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        Map<String, Object> map = new HashMap<>();
        map.put("student", student);
        QueryWrapper<School> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("school_id", student.getSchoolId());
        map.put("schoolName", schoolDao.selectOne(queryWrapper2).getSchoolName());
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Queries interview information based on specific criteria.
     *
     * @param queryInfo Information used to query interview details.
     * @return HttpResult containing interview information or an error message.
     */
    @Override
    public HttpResult queryInterviewInfo(String queryInfo) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        //        QueryWrapper<InterviewInfo> queryWrapper1 = new QueryWrapper<>();
        //        queryWrapper1.eq("student_id", user.getUserId()).eq("mark", mark);
        //        List<InterviewInfo> interviewInfo = interviewInfoDao.selectList(queryWrapper1);
        //        List<StudentInterviewVo> studentInterviewVo = new ArrayList<>();
        //        for (InterviewInfo tempInfo : interviewInfo) {
        //            QueryWrapper<JobApplication> queryWrapper2 = new QueryWrapper<>();
        //            queryWrapper2.eq("application_id", tempInfo.getApplicationId());
        //            JobApplication jobApplication = jobApplicationDao.selectOne(queryWrapper2);
        //            QueryWrapper<RecruitmentInfo> queryWrapper3 = new QueryWrapper<>();
        //            queryWrapper3.eq("recruitment_id", jobApplication.getRecruitmentId());
        //            StudentInterviewVo tempVo = new StudentInterviewVo(tempInfo.getDateTime(),
        // tempInfo.getPosition(), tempInfo.getMark(), tempInfo.getStatus(),
        // recruitmentInfoDao.selectOne(queryWrapper3).getCompanyName());
        //            studentInterviewVo.add(tempVo);
        //        }
        //        return HttpResult.success(studentInterviewVo, "查询成功");
        return HttpResult.success(
                interviewInfoDao.queryInterviewInfo(queryInfo, user.getUserId()), "查询成功");
    }

    /**
     * Retrieves user's homepage statistics including job applications, interviews, collections, and
     * recent activity.
     *
     * @return HttpResult containing user statistics or an error message.
     */
    @Override
    public HttpResult homepage() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<JobApplication> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Integer i = jobApplicationDao.selectCount(queryWrapper1);
        QueryWrapper<InterviewInfo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("student_id", user.getUserId());
        Integer j = interviewInfoDao.selectCount(queryWrapper2);
        QueryWrapper<MarkedRecruitmentInfo> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("student_id", user.getUserId());
        Integer k = markedRecruitmentInfoDao.selectCount(queryWrapper3);
        QueryWrapper<Student> queryWrapper4 = new QueryWrapper<>();
        queryWrapper4.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper4);
        Map<String, Object> map = new HashMap<>();
        map.put("投递简历数", i);
        map.put("面试数", j);
        map.put("收藏数", k);
        map.put("上次登录", student.getLoginTime());
        map.put("上次修改密码", student.getChangePasswordTime());
        map.put("手机号", student.getPhoneNumber());
        return HttpResult.success(map, "查询成功");
    }

    /**
     * Generates job recommendations based on user's profile and previously applied jobs.
     *
     * @param page The page number for the job recommendations.
     * @return HttpResult containing job recommendations or an error message.
     */
    @Override
    public HttpResult getRecommendation(Integer page) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("student_id", user.getUserId());
        Student student = studentDao.selectOne(queryWrapper1);
        String byword = "";
        if (student.getByword() != null && !student.getByword().isEmpty()) {
            byword = student.getByword();
        }
        List<RecruitmentInfo> list = recruitmentInfoDao.selectList(null);
        Map<RecruitmentInfo, Integer> map = new HashMap<>();
        for (RecruitmentInfo temp : list) {
            map.put(temp, getScore(byword, temp.getByword()));
        }
        LinkedHashMap<RecruitmentInfo, Integer> sortedMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        List<RecruitmentInfo> result = new ArrayList<>();
        QueryWrapper<JobApplication> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("student_id", user.getUserId());
        List<JobApplication> applicationList = jobApplicationDao.selectList(queryWrapper2);
        int size = 6;
        for (int i = 0; i < size; i++) {
            if (i + (page % 4) * 6 < sortedMap.size()) {
                result.add((RecruitmentInfo) sortedMap.keySet().toArray()[i + (page % 4) * 6]);
            } else {
                break;
            }
            for (JobApplication temp : applicationList) {
                if (temp.getRecruitmentId().equals(result.get(result.size() - 1).getRecruitmentId())) {
                    result.remove(result.size() - 1);
                    size++;
                    break;
                }
            }
        }
        QueryWrapper<MarkedRecruitmentInfo> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("student_id", user.getUserId());
        List<MarkedRecruitmentInfo> markedRecruitmentInfoList =
                markedRecruitmentInfoDao.selectList(queryWrapper3);
        for (RecruitmentInfo temp : result) {
            for (MarkedRecruitmentInfo temp1 : markedRecruitmentInfoList) {
                if (temp.getRecruitmentId().equals(temp1.getRecruitmentId())) {
                    temp.setStatus('1');
                    break;
                } else {
                    temp.setStatus('0');
                }
            }
        }
        return HttpResult.success(result, "查询成功");
    }

    /**
     * Retrieves information about career fairs.
     *
     * @return HttpResult containing career fair information or an error message.
     */
    @Override
    public HttpResult queryFair() {
        return HttpResult.success(careerFairDao.queryCareerFair(), "查询成功");
    }

    /**
     * Retrieves a list of messages for the logged-in user.
     *
     * @return HttpResult containing a list of messages or an error message.
     */
    @Override
    public HttpResult queryMessageList() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        return HttpResult.success(messageDao.queryMessageList(user.getUserId(), null, null), "查询成功");
    }

    /**
     * Retrieves messages for a user based on messageId or queryInfo.
     *
     * @param messageId The ID of the message to retrieve.
     * @param queryInfo Additional information for message retrieval.
     * @return HttpResult containing the requested messages or an error message.
     */
    @Override
    public HttpResult queryMessage(Integer messageId, String queryInfo) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        return HttpResult.success(
                messageDao.queryMessageList(user.getUserId(), messageId, queryInfo), "查询成功");
    }

    /**
     * Deletes all messages for the logged-in user.
     *
     * @return HttpResult indicating success or failure of the operation.
     */
    @Override
    public HttpResult deleteAllMessage() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("`to`", user.getUserId()).set("student_del",1);
        if (messageDao.update(null,updateWrapper) > 0) {
            return HttpResult.success(null, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Deletes a specific message based on its ID.
     *
     * @param messageId The ID of the message to be deleted.
     * @return HttpResult indicating success or failure of the operation.
     */
    @Override
    public HttpResult deleteMessage(Integer messageId) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("message_id", messageId).set("student_del",1);
        if (messageDao.update(null,updateWrapper) > 0) {
            return HttpResult.success(null, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    /**
     * Marks all messages as read for the logged-in user.
     *
     * @return HttpResult indicating success or failure of the operation.
     */
    @Override
    public HttpResult hasReadAllMessage() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        User user = loginUser.getUser();
        System.out.println(user.getUserId());
        updateWrapper.eq("`to`", user.getUserId()).set("status", "1");
        messageDao.update(null, updateWrapper);
        return HttpResult.success(null, "修改成功");
    }

    /**
     * Marks a specific message as read based on its ID.
     *
     * @param messageId The ID of the message to be marked as read.
     * @return HttpResult indicating success or failure of the operation.
     */
    @Override
    public HttpResult hasReadMessage(Integer messageId) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("message_id", messageId).set("status", "1");
        messageDao.update(null, updateWrapper);
        return HttpResult.success(null, "修改成功");
    }

    /**
     * Edits the user's phone number.
     *
     * @param phoneNumber The new phone number to be set for the user.
     * @return HttpResult indicating success or failure of the operation.
     */
    @Override
    public HttpResult editPhoneNumber(String phoneNumber) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken)
                        SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_id", user.getUserId()).set("phone_number", phoneNumber);
        studentDao.update(null, updateWrapper);
        return HttpResult.success(null, "修改成功");
    }

    /**
     * Retrieves a list of colleges and majors based on the provided school name.
     *
     * @param schoolName The name of the school to fetch colleges and majors for.
     * @return HttpResult containing the list of colleges and majors or an error message.
     */
    @Override
    public HttpResult queryCollegeMajor(String schoolName) {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_name", schoolName);
        Integer schoolId = schoolDao.selectOne(queryWrapper).getSchoolId();
        QueryWrapper<College> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("school_id", schoolId);
        List<College> collegeList = collegeDao.selectList(queryWrapper1);
        QueryWrapper<Major> queryWrapper2 = new QueryWrapper<>();
        queryWrapper1.eq("school_id", schoolId);
        List<Major> majorList = majorDao.selectList(queryWrapper2);
        List<CollegeMajorVo> list = new ArrayList<>();
        for (College temp : collegeList) {
            Integer collegeId = temp.getCollegeId();
            List<Major> tempMajorList = new ArrayList<>();
            for (Major major : majorList) {
                if (major.getCollegeId().equals(collegeId)) {
                    tempMajorList.add(major);
                }
            }
            CollegeMajorVo collegeMajorVo = new CollegeMajorVo();
            collegeMajorVo.setCollegeId(collegeId);
            collegeMajorVo.setCollegeName(temp.getCollegeName());
            collegeMajorVo.setMajorList(tempMajorList);
            list.add(collegeMajorVo);
        }
        return HttpResult.success(list, "查询成功");
    }
}
