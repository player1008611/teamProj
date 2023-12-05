package com.teamProj.service;

import com.teamProj.utils.HttpResult;

public interface SchoolService {
    HttpResult schoolLogin(String account, String password);

    HttpResult schoolLogout();

    HttpResult queryStudent(String name, Character status, Integer current, Integer size);

    HttpResult resetStudentPassword(String account);

    HttpResult enableStudentAccount(String account);

    HttpResult disableStudentAccount(String account);

    HttpResult deleteStudentAccount(String account);

}
