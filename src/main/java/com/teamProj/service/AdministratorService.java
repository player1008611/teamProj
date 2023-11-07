package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;

import java.util.List;

public interface AdministratorService {
    Administrator administratorLogin(String account, String password);

    Student resetStudentPassword(String account);

    Student disableStudentAccount(String account);

    Student enableStudentAccount(String account);

    Student deleteStudentAccount(String account);

    List<Student> queryStudent(String name, String account, String status);

    School resetSchoolPassword(String account);

    School disableSchoolAccount(String account);

    School enableSchoolAccount(String account);

    School deleteSchoolAccount(String account);

    School createNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal);

    List<School> querySchool(String schoolName, String account, int status);
}
