package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Student;

public interface StudentService {
    Student studentLogin(String account, String password);
}
