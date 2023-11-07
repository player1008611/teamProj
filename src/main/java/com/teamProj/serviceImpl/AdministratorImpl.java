package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    AdministratorDao administratorDao;

    @Resource
    StudentDao studentDao;

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
    public int ResetStudentPassword(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("studentAccount", account).set("studentPassword", "123456");
        return studentDao.update(null, wrapper);
    }

    @Override
    public int DisableStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("studentAccount", account).set("userStatus", 1);
        return studentDao.update(null, wrapper);
    }

    @Override
    public int AbleStudentAccount(String account) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("studentAccount", account).set("userStatus", 0);
        return studentDao.update(null, wrapper);
    }

    @Override
    public int DeleteStudentAccount(String account) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("studentAccount", account);
        return studentDao.delete(wrapper);
    }

    @Override
    public List<Student> QueryStudent(String name, String account, int status) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name.isEmpty() ? "*" : name)
                .eq("studentAccount", account.isEmpty() ? "*" : account)
                .eq("userStatus", status);
        return studentDao.selectList(wrapper);
    }

    @Override
    public School ResetSchoolPassword(String account) {
        return null;
    }

    @Override
    public School DisableSchoolAccount(String account) {
        return null;
    }

    @Override
    public School AbleSchoolAccount(String account) {
        return null;
    }

    @Override
    public School DeleteSchoolAccount(String account) {
        return null;
    }

    @Override
    public School CreateNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal) {
        return null;
    }

    @Override
    public List<School> QuerySchool(String schoolName, String account, int status) {
        return null;
    }
}
