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


    @Override
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

    @Override
    public HttpResult schoolLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }

    @Override
    public HttpResult setSchoolPassword(String oldPassword, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
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
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major_id", majorId);
        if (majorDao.delete(queryWrapper) > 0) {
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
        SchoolApplicationData schoolApplicationData = new SchoolApplicationData();
        for(Student student:students){
            for(SchoolApplicationDataVo dataVo:schoolApplicationDataVos){
                if(student.getStudentId().equals(dataVo.getStudentId())){
                    //仅统计已通过的
                    if(!dataVo.getStatus().equals("2"))continue;

                    //修改school-num
                    schoolApplicationData.setStudentNum(schoolApplicationData.getStudentNum()+1);
                    //修改school-enterprise
                    Map<String,Integer> schoolEnterprise=schoolApplicationData.getEnterprise();
                    if(schoolEnterprise.containsKey(dataVo.getEnterpriseName())) {
                        schoolEnterprise.put(dataVo.getEnterpriseName(), schoolEnterprise.get(dataVo.getEnterpriseName()) + 1);
                    }else{
                        schoolEnterprise.put(dataVo.getEnterpriseName(),1);
                    }
                    schoolApplicationData.setEnterprise(schoolEnterprise);

                    //修改school-city
                    Map<String,Integer> schoolCity=schoolApplicationData.getCity();
                    if(schoolCity.containsKey(dataVo.getCity())) {
                        schoolCity.put(dataVo.getCity(), schoolCity.get(dataVo.getCity()) + 1);
                    }else{
                        schoolCity.put(dataVo.getCity(),1);
                    }
                    schoolApplicationData.setCity(schoolCity);

                    //修改college
                    if(schoolApplicationData.getCollege().containsKey(dataVo.getCollegeName())) {

                        SchoolApplicationData.CollegeApplicationData collegeApplicationData = schoolApplicationData.getCollege().get(dataVo.getCollegeName());
                        //修改college-num
                        collegeApplicationData.setStudentNum(collegeApplicationData.getStudentNum()+1);

                        //修改college-city
                        Map<String,Integer> collegeCity=collegeApplicationData.getCity();
                        if(collegeCity.containsKey(dataVo.getCity())) {
                            collegeCity.put(dataVo.getCity(), collegeCity.get(dataVo.getCity()) + 1);
                            collegeApplicationData.setCity(collegeCity);
                        }else{
                            collegeCity.put(dataVo.getCity(),1);
                            collegeApplicationData.setCity(collegeCity);
                        }


                        //修改college-enterprise
                        Map<String,Integer> collegeEnterprise=collegeApplicationData.getEnterprise();
                        if(collegeEnterprise.containsKey(dataVo.getEnterpriseName())){
                            collegeEnterprise.put(dataVo.getEnterpriseName(),collegeEnterprise.get(dataVo.getEnterpriseName())+1);
                            collegeApplicationData.setEnterprise(collegeEnterprise);
                        }else{
                            collegeEnterprise.put(dataVo.getEnterpriseName(),1);
                            collegeApplicationData.setEnterprise(collegeEnterprise);
                        }


                        //修改major
                        if(collegeApplicationData.getMajor().containsKey(dataVo.getMajorName())){
                            SchoolApplicationData.MajorApplicationData majorApplicationData = collegeApplicationData.getMajor().get(dataVo.getMajorName());
                            //修改major-num
                            majorApplicationData.setStudentNum(majorApplicationData.getStudentNum()+1);

                            //修改major-city
                            Map<String,Integer> majorCity = majorApplicationData.getCity();
                            if(majorCity.containsKey(dataVo.getCity())){
                                majorCity.put(dataVo.getCity(), majorCity.get(dataVo.getCity())+1);
                                majorApplicationData.setCity(majorCity);
                            }else{
                                majorCity.put(dataVo.getCity(),1);
                                majorApplicationData.setCity(majorCity);
                            }

                            //修改major-enterprise

                            Map<String,Integer> majorEnterprise = majorApplicationData.getEnterprise();
                            if(majorEnterprise.containsKey(dataVo.getEnterpriseName())){
                                majorEnterprise.put(dataVo.getEnterpriseName(), majorEnterprise.get(dataVo.getEnterpriseName())+1);
                                majorApplicationData.setEnterprise(majorEnterprise);
                            }else{
                                majorEnterprise.put(dataVo.getEnterpriseName(), 1);
                                majorApplicationData.setEnterprise(majorEnterprise);
                            }
                            System.out.println(majorApplicationData.getEnterprise());

                            Map<String,SchoolApplicationData.MajorApplicationData> map = collegeApplicationData.getMajor();
                            map.put(dataVo.getMajorName(),majorApplicationData);
                            collegeApplicationData.setMajor(map);
                        }else{
                            Map<String,SchoolApplicationData.MajorApplicationData> map = collegeApplicationData.getMajor();
                            SchoolApplicationData.MajorApplicationData majorApplicationData = new SchoolApplicationData.MajorApplicationData();
                            Map<String,Integer> city = new HashMap<>();
                            city.put(dataVo.getCity(),1);
                            majorApplicationData.setCity(city);
                            Map<String,Integer> enterprise = new HashMap<>();
                            enterprise.put(dataVo.getEnterpriseName(),1);
                            majorApplicationData.setEnterprise(enterprise);
                            majorApplicationData.setStudentNum(1);
                            map.put(dataVo.getMajorName(),majorApplicationData);

                            collegeApplicationData.setMajor(map);
                        }



                    }else{
                        Map<String,SchoolApplicationData.CollegeApplicationData> map = schoolApplicationData.getCollege();
                        //新建一个college
                        SchoolApplicationData.CollegeApplicationData collegeApplicationData = new SchoolApplicationData.CollegeApplicationData();
                        //设置num
                        collegeApplicationData.setStudentNum(1);
                        //设置college-city
                        Map<String,Integer> city = new HashMap<>();
                        city.put(dataVo.getCity(),1);
                        collegeApplicationData.setCity(city);
                        //设置college-enterprise
                        Map<String,Integer> enterprise = new HashMap<>();
                        enterprise.put(dataVo.getEnterpriseName(), 1);
                        collegeApplicationData.setEnterprise(enterprise);
                        //设置college-major
                        //新建major
                        Map<String,SchoolApplicationData.MajorApplicationData> major = collegeApplicationData.getMajor();
                        SchoolApplicationData.MajorApplicationData majorApplicationData = new SchoolApplicationData.MajorApplicationData();
                        //设置major-num
                        majorApplicationData.setStudentNum(1);
                        //设置major-city
                        city=new HashMap<>();
                        city.put(dataVo.getCity(),1);
                        majorApplicationData.setCity(city);
                        //设置major-enterprise
                        enterprise=new HashMap<>();
                        enterprise.put(dataVo.getEnterpriseName(),1);
                        majorApplicationData.setEnterprise(enterprise);
                        //写入college-major
                        major.put(dataVo.getMajorName(),majorApplicationData);
                        collegeApplicationData.setMajor(major);

                        //写入school-college
                        map.put(dataVo.getCollegeName(), collegeApplicationData);
                        schoolApplicationData.setCollege(map);
                    }
                }
            }
        }
//        Map<String,Integer> map2 = new HashMap<>();
//        Map<String,Integer> map3 = new HashMap<>();
//        for(SchoolApplicationDataVo dataVo:schoolApplicationDataVos){
//            if(map2.containsKey(dataVo.getEnterpriseName())){
//                map2.put(dataVo.getEnterpriseName(),map2.get(dataVo.getEnterpriseName())+1);
//            }else{
//                map2.put(dataVo.getEnterpriseName(),1);
//            }
//            if(map3.containsKey(dataVo.getCity())){
//                map3.put(dataVo.getCity(),map3.get(dataVo.getCity())+1);
//            }else{
//                map3.put(dataVo.getCity(),1);
//            }
//        }
//        Map<String,List> map = new HashMap<>();
//        Map<String,List<Map.Entry<String, Integer>>> temp = new HashMap<>();
//        for(Map.Entry<String, Map<String,Integer>> entry:map0.entrySet()){
//            Map<String,Integer> map1 = entry.getValue();
//            List<Map.Entry<String, Integer>> list = new ArrayList<>(map1.entrySet());
//            list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//            temp.put(entry.getKey(), list);
//        }
//        List<Map.Entry<String,List<Map.Entry<String,Integer>>>> list1 = new ArrayList<>(temp.entrySet());
//        list1.sort((o1, o2) -> o2.getValue().get(0).getValue().compareTo(o1.getValue().get(0).getValue()));
//        map.put("college",list1);
//        List<Map.Entry<String, Integer>> list2 = new ArrayList<>(map2.entrySet());
//        list2.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//        map.put("enterprise",list2);
//        List<Map.Entry<String, Integer>> list3 = new ArrayList<>(map3.entrySet());
//        list3.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//        map.put("city",list3);

        schoolApplicationData.setCity(SchoolApplicationData.sortMapByValue(schoolApplicationData.getCity()));
        schoolApplicationData.setEnterprise(SchoolApplicationData.sortMapByValue(schoolApplicationData.getEnterprise()));
        Map<String,SchoolApplicationData.CollegeApplicationData> college = schoolApplicationData.getCollege();
        for(Map.Entry<String,SchoolApplicationData.CollegeApplicationData> entry:college.entrySet()){
            entry.getValue().setCity(SchoolApplicationData.sortMapByValue(entry.getValue().getCity()));
            entry.getValue().setEnterprise(SchoolApplicationData.sortMapByValue(entry.getValue().getEnterprise()));
            Map<String,SchoolApplicationData.MajorApplicationData> major = entry.getValue().getMajor();
            for(Map.Entry<String,SchoolApplicationData.MajorApplicationData> entry1:major.entrySet()){
                entry1.getValue().setCity(SchoolApplicationData.sortMapByValue(entry1.getValue().getCity()));
                entry1.getValue().setEnterprise(SchoolApplicationData.sortMapByValue(entry1.getValue().getEnterprise()));
            }
        }
        schoolApplicationData.setCollege(college);
        return HttpResult.success(schoolApplicationData,"查询成功");
    }
}
