package com.teamProj.serviceImpl;

import com.teamProj.dao.AdministratorDao;
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
    public Student ResetStudentPassword(String account) {
        return null;
    }

    @Override
    public Student DisableStudentAccount(String account) {
        return null;
    }

    @Override
    public Student AbleStudentAccount(String account) {
        return null;
    }

    @Override
    public Student DeleteStudentAccount(String account) {
        return null;
    }

    @Override
    public List<Student> QueryStudent(String name, String account, int status) {
        return null;
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
