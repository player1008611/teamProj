package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;

import java.util.List;

public interface AdministratorService {
    Administrator administratorLogin(String account, String password);

    Student ResetStudentPassword(String account);

    Student DisableStudentAccount(String account);

    Student AbleStudentAccount(String account);

    Student DeleteStudentAccount(String account);

    List<Student> QueryStudent(String name, String account, int status);

    School ResetSchoolPassword(String account);

    School DisableSchoolAccount(String account);

    School AbleSchoolAccount(String account);

    School DeleteSchoolAccount(String account);

    School CreateNewSchoolAccount(String account, String password, String schoolName, String telephone, String principal);

    List<School> QuerySchool(String schoolName, String account, int status);
}
