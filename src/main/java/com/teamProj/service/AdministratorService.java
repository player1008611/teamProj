package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.utils.HttpResult;

import java.util.List;
import java.util.Objects;

public interface AdministratorService {
    HttpResult administratorLogin(String account, String password);

    HttpResult resetStudentPassword(String account);

    HttpResult disableStudentAccount(String account);

    HttpResult enableStudentAccount(String account);

    HttpResult deleteStudentAccount(String account);

    HttpResult queryStudent(String name, String account, String status);

    HttpResult resetSchoolPassword(String account);

    HttpResult disableSchoolAccount(String account);

    HttpResult enableSchoolAccount(String account);

    HttpResult deleteSchoolAccount(String account);

    HttpResult createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal);

    HttpResult querySchool(String schoolName, String account, String status);
}
