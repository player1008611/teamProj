package com.teamProj.service;

import com.teamProj.utils.HttpResult;

public interface AdministratorService {
    HttpResult administratorLogin(String account, String password);

    HttpResult administratorLogout();

    HttpResult queryStudent(String name,String schoolName,Character status,Integer current,Integer size);

    HttpResult resetStudentPassword(String account);

    HttpResult enableStudentAccount(String account);

    HttpResult disableStudentAccount(String account);
}
