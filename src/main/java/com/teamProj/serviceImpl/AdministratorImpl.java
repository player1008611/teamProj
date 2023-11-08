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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    AdministratorDao administratorDao;

    @Resource
    StudentDao studentDao;

    @Resource
    SchoolDao schoolDao;

    public Administrator administratorLogin(String account, String password) {
        List<Administrator> administratorList = administratorDao.selectList(null);
        for (Administrator administrator : administratorList) {
            if (administrator.getLoginAccount().equals(account)) {
                if (administrator.getPassword().equals(password)) {
                    return administrator;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Student resetStudentPassword(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("student_password", "123456");
        if (studentDao.update(null, wrapper) > 0) {
            return studentDao.selectOne(wrapper);
        } else {
            return null;
        }
    }

    @Override
    public Student disableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 0);
        if (studentDao.update(null, wrapper) > 0) {
            return studentDao.selectOne(wrapper);
        } else {
            return null;
        }
    }

    @Override
    public Student enableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("user_status", 1);
        if (studentDao.update(null, wrapper) > 0) {
            return studentDao.selectOne(wrapper);
        } else {
            return null;
        }
    }

    @Override
    public Student deleteStudentAccount(String account) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("student_account", account);
        Student deletedStudent = studentDao.selectOne(wrapper);
        if (studentDao.delete(wrapper) > 0) {
            return deletedStudent;
        } else {
            return null;
        }
    }

    @Override
    public List<Student> queryStudent(String name, String account, Character status) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        if (!name.isEmpty()) {
            wrapper.eq("name", name);
        }
        if (!account.isEmpty()) {
            wrapper.eq("student_account", account);
        }
        wrapper.eq("user_status", status);
        return studentDao.selectList(wrapper);
    }

    @Override
    public School resetSchoolPassword(String account) {
        UpdateWrapper<School> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_account", account).set("school_password", "123456");
        if (schoolDao.update(null, wrapper) > 0) {
            return schoolDao.selectOne(wrapper);
        } else {
            return null;
        }
    }

    @Override
    public School disableSchoolAccount(String account) {
        return null;
    }

    @Override
    public School enableSchoolAccount(String account) {
        return null;
    }

    @Override
    public School deleteSchoolAccount(String account) {
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        wrapper.eq("school_account", account);
        School deletedSchool = schoolDao.selectOne(wrapper);
        if (schoolDao.delete(wrapper) > 0) {
            return deletedSchool;
        } else {
            return null;
        }
    }

    @Override
    public School createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal) {
        School newSchool = new School();
        newSchool.setSchoolAccount(account);
        newSchool.setSchoolPassword(password);
        newSchool.setSchoolName(schoolName);
        newSchool.setTel(telephone);
        newSchool.setPrincipal(principal);
        if (schoolDao.insert(newSchool) > 0) {
            return newSchool;
        } else {
            return null;
        }
    }

    @Override
    public List<School> querySchool(String schoolName, String account, Character status) {
        QueryWrapper<School> wrapper = new QueryWrapper<>();
        if (!schoolName.isEmpty()) {
            wrapper.eq("school_name", schoolName);
        }
        if (!account.isEmpty()) {
            wrapper.eq("school_account", account);
        }
        wrapper.eq("user_status", status);
        return schoolDao.selectList(wrapper);
    }
}
