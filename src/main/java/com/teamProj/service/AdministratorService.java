package com.teamProj.service;

import com.teamProj.utils.HttpResult;

public interface AdministratorService {
    HttpResult administratorLogin(String account, String password);

    HttpResult administratorLogout();

    HttpResult queryStudent(String name, String schoolName, Character status, Integer current, Integer size);

    HttpResult resetStudentPassword(String account);

    HttpResult enableStudentAccount(String account);

    HttpResult disableStudentAccount(String account);

    HttpResult deleteStudentAccount(String account);

    HttpResult queryEnterprise(String name, Integer current, Integer size);

    HttpResult queryEnterpriseUser(String enterpriseName, String userName, Integer current, Integer size);

    //TODO 不确定的实现
    //HttpResult createNewEnterprise(String name, String url);

    //TODO 参数不确定
    //HttpResult createNewEnterpriseUser();
}
