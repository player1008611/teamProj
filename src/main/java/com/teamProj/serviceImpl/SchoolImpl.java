package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teamProj.dao.*;
import com.teamProj.entity.*;
import com.teamProj.entity.vo.AdminStudentVo;
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
import java.util.*;
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

    public HttpResult schoolLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
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
        map.put("schoolName",schoolName);
        redisCache.setCacheObject(userId, loginUser, 24, TimeUnit.HOURS);
        return HttpResult.success(map, "登录成功");
    }

    public HttpResult schoolLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult queryStudent(String name,Integer majorId, Character status, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_id", schoolId);
        String schoolName = schoolDao.selectOne(schoolQueryWrapper).getSchoolName();
        Page<SchoolStudentVo> page = new Page<>(current, size);
        return HttpResult.success(studentDao.queryStudentWithMajor(page, name, schoolName,majorId, status), "查询成功");
    }

    @Override
    public HttpResult resetStudentPassword(String account) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).eq("permission", "student").set("password", bCryptPasswordEncoder.encode("123456"));
        if (userDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(account, "重置成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

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

    @Override
    public HttpResult deleteStudentAccount(String account) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account).eq("permission", "student");
        if (userDao.delete(userQueryWrapper) > 0) {
            return HttpResult.success(account, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult queryCollege(String name, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<College> page = new Page<>(current, size);
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("college_name", name).eq("school_id", schoolId);
        return HttpResult.success(collegeDao.selectPage(page, queryWrapper), "查询成功");
    }

    @Override
    public HttpResult createCollege(String name) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
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

    @Override
    public HttpResult deleteCollege(Integer collegeId) {
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_id", collegeId);
        if (collegeDao.delete(queryWrapper) > 0) {
            return HttpResult.success(collegeId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult editCollege(Integer collegeId, String name) {
        UpdateWrapper<College> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("college_id", collegeId).set("college_name", name);
        if (collegeDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(collegeId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult queryMajor(String name, Integer current, Integer size, Integer collegeId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<Major> page = new Page<>(current, size);
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("major_name", name).eq("college_id", collegeId).eq("school_id", schoolId);
        return HttpResult.success(majorDao.selectPage(page, queryWrapper), "查询成功");
    }

    @Override
    public HttpResult createMajor(String name, Integer collegeId) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
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

    @Override
    public HttpResult deleteMajor(Integer majorId) {
        if (majorDao.deleteById(majorId) > 0) {
            return HttpResult.success(majorId, "删除成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult editMajor(Integer majorId, String name) {
        UpdateWrapper<Major> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("major_id", majorId).set("major_name", name);
        if (majorDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(majorId, "修改成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }


    @Override
    public HttpResult auditCareerFair(Integer careerFairId, String status, String reason) {
        UpdateWrapper<CareerFair> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("fair_id", careerFairId).set("status", status).set("reason", reason);
        if (careerFairDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(careerFairId, "审核成功");
        }
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
    }

    @Override
    public HttpResult queryCareerFair(String name, Integer current, Integer size) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        Page<SchoolFairVo> page = new Page<>(current, size);
//        QueryWrapper<CareerFair> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("school_id", schoolId).like("title", name);
        return HttpResult.success(careerFairDao.queryFair(page,name,schoolId), "查询成功");
    }

    @Override
    public HttpResult applicationData(){
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int schoolId = loginUser.getUser().getUserId();
        QueryWrapper<Student> queryStudent = new QueryWrapper<>();
        queryStudent.eq("school_id",schoolId);
        List<Student> students = studentDao.selectList(queryStudent);
        List<SchoolApplicationDataVo> schoolApplicationDataVos = jobApplicationDao.querySchoolApplicationData(schoolId);
        Integer studentNum = students.size();
        Map<String,Integer> map0 = new HashMap<>();
        Map<String,Integer> map1 = new HashMap<>();
        for(Student student:students){
            for(SchoolApplicationDataVo dataVo:schoolApplicationDataVos){
                if(student.getStudentId().equals(dataVo.getStudentId())){

                    if(!dataVo.getStatus().equals("2"))continue;

                    if(map1.containsKey(dataVo.getMajorName())){
                        map1.put(dataVo.getMajorName(),map1.get(dataVo.getMajorName())+1);
                    }else{
                        map1.put(dataVo.getMajorName(),1);
                    }
                    if (map0.containsKey(dataVo.getCollegeName())){
                        map0.put(dataVo.getCollegeName(),map0.get(dataVo.getCollegeName())+1);
                    }else{
                        map0.put(dataVo.getCollegeName(),1);
                    }
                    break;
                }
            }
        }
        Map<String,Integer> map2 = new HashMap<>();
        Map<String,Integer> map3 = new HashMap<>();
        for(SchoolApplicationDataVo dataVo:schoolApplicationDataVos){
            if(map2.containsKey(dataVo.getEnterpriseName())){
                map2.put(dataVo.getEnterpriseName(),map2.get(dataVo.getEnterpriseName())+1);
            }else{
                map2.put(dataVo.getEnterpriseName(),1);
            }
            if(map3.containsKey(dataVo.getCity())){
                map3.put(dataVo.getCity(),map3.get(dataVo.getCity())+1);
            }else{
                map3.put(dataVo.getCity(),1);
            }
        }
        Map<String,List<Map.Entry<String, Integer>>> map = new HashMap<>();
        List<Map.Entry<String, Integer>> list0 = new ArrayList<>(map0.entrySet());
        list0.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        map.put("college",list0);
        List<Map.Entry<String, Integer>> list1 = new ArrayList<>(map1.entrySet());
        list1.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        map.put("major",list1);
        List<Map.Entry<String, Integer>> list2 = new ArrayList<>(map2.entrySet());
        list2.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        map.put("enterprise",list2);
        List<Map.Entry<String, Integer>> list3 = new ArrayList<>(map3.entrySet());
        list3.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        map.put("city",list3);
        return HttpResult.success(map,"查询成功");
    }
}
