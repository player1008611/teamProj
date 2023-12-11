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

    HttpResult queryCollege(String name, Integer current, Integer size);
    HttpResult createCollege(String name);
    HttpResult deleteCollege(Integer collegeId);
    HttpResult editCollege(Integer collegeId, String name);
    HttpResult queryMajor(String name, Integer current, Integer size);
    HttpResult createMajor(String name, Integer collegeId);
    HttpResult deleteMajor(Integer majorId);
    HttpResult editMajor(Integer majorId, String name);

}
