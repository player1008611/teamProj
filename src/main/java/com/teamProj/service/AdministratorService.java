package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import com.teamProj.utils.HttpResult;

import java.util.List;
import java.util.Objects;

public interface AdministratorService {
    HttpResult<Administrator> administratorLogin(String account, String password);

    HttpResult<Student> resetStudentPassword(String account);

    HttpResult<Student> disableStudentAccount(String account);

    HttpResult<Student> enableStudentAccount(String account);

    HttpResult<Student> deleteStudentAccount(String account);

    HttpResult<List<Student>> queryStudent(String name, String account, String status);

    HttpResult<School> resetSchoolPassword(String account);

    HttpResult<School> disableSchoolAccount(String account);

    HttpResult<School> enableSchoolAccount(String account);

    HttpResult<School> deleteSchoolAccount(String account);

    HttpResult<School> createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal);

    HttpResult<List<School>> querySchool(String schoolName, String account, String status);
}
