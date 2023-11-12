package com.teamProj.service;

import com.teamProj.entity.Student;
import com.teamProj.utils.HttpResult;

public interface StudentService {
    HttpResult<Student> studentLogin(String account, String password);

    HttpResult<Student> setStudentPassword(String account, String oldPassword, String password);
}
