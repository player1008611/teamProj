package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.dao.SchoolDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    private AdministratorDao administratorDao;

    @Resource
    private StudentDao studentDao;

    @Resource
    private SchoolDao schoolDao;

    @Resource
    private AuthenticationManager authenticationManager;

    public HttpResult<Administrator> administratorLogin(String account, String password) {
        QueryWrapper<Administrator> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_account", account);
        Administrator loginAdmin = administratorDao.selectOne(queryWrapper);
        if (loginAdmin != null) {
            if (loginAdmin.getPassword().equals(password)) {
                return HttpResult.success(administratorDao.selectOne(new QueryWrapper<Administrator>().select("name", "login_account").eq("login_account", account)), "登录成功");
            }
            return HttpResult.success(null, "密码错误");
        }
        return HttpResult.success(null, "账号不存在");
    }

    @Override
    public HttpResult<Student> resetStudentPassword(String account) {
        UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("student_account", account).set("student_password", "123456");
        if (studentDao.update(null, updateWrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "重置成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<Student> disableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 0);
        if (studentDao.update(null, wrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<Student> enableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 1);
        if (studentDao.update(null, wrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<Student> deleteStudentAccount(String account) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("student_account", account);
        Student deletedStudent = studentDao.selectOne(wrapper);
        if (studentDao.delete(wrapper) > 0) {
            Student result = new Student();
            result.setName(deletedStudent.getName());
            result.setStudentAccount(deletedStudent.getStudentAccount());
            return HttpResult.success(result, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<List<Student>> queryStudent(String name, String account, String status) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.eq("name", name);
        }
        if (account != null && !account.isEmpty()) {
            queryWrapper.eq("student_account", account);
        }
        queryWrapper.select("student_account", "name", "phone_number", "user_status", "creation_time").eq("user_status", status);
        return HttpResult.success(studentDao.selectList(queryWrapper), "查询成功");
    }

    @Override
    public HttpResult<School> resetSchoolPassword(String account) {
        UpdateWrapper<School> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("school_account", account).set("school_password", "123456");
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("school_account", "school_name").eq("school_account", account);
        if (schoolDao.update(null, updateWrapper) > 0) {
            return HttpResult.success(schoolDao.selectOne(queryWrapper), "操作成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<School> disableSchoolAccount(String account) {
        return null;
    }

    @Override
    public HttpResult<School> enableSchoolAccount(String account) {
        return null;
    }

    @Override
    public HttpResult<School> deleteSchoolAccount(String account) {
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        wrapper.eq("school_account", account);
        School deletedSchool = schoolDao.selectOne(wrapper);
        if (schoolDao.delete(wrapper) > 0) {
            School result = new School();
            result.setSchoolName(deletedSchool.getSchoolName());
            result.setSchoolAccount(deletedSchool.getSchoolAccount());
            return HttpResult.success(result, "删除成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }

    @Override
    public HttpResult<School> createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal) {
        School newSchool = new School();
        newSchool.setSchoolAccount(account);
        newSchool.setSchoolPassword(password);
        newSchool.setSchoolName(schoolName);
        newSchool.setTel(telephone);
        newSchool.setPrincipal(principal);
        QueryWrapper<School> queryWrapper = new QueryWrapper<School>();
        queryWrapper.select("school_account", "school_name").eq("school_account", account);
        if (schoolDao.insert(newSchool) > 0) {
            return HttpResult.success(schoolDao.selectOne(queryWrapper), "添加成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public HttpResult<List<School>> querySchool(String schoolName, String account, String status) {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        if (schoolName != null && !schoolName.isEmpty()) {
            queryWrapper.eq("school_name", schoolName);
        }
        if (account != null && !account.isEmpty()) {
            queryWrapper.eq("school_account", account);
        }
        queryWrapper.select("school_name", "principal", "tel", "state", "school_account").eq("user_status", status);
        return HttpResult.success(schoolDao.selectList(queryWrapper), "查询成功");
    }
}
