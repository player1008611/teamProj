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

    HttpResult createNewEnterprise(String name, String url);

    HttpResult createNewEnterpriseUser(String account, String enterpriseName, String name, String tel);

    HttpResult resetEnterpriseUserPassword(String account);

    HttpResult enableEnterpriseUser(String account);

    HttpResult disableEnterpriseUser(String account);

    HttpResult deleteEnterpriseUser(String account);

    HttpResult querySchoolUser(String principal, Character status, Integer current, Integer size);

    HttpResult createNewSchoolUser(String account, String schoolName, String principal, String tel);
}
