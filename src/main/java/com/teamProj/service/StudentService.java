package com.teamProj.service;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Student;
import com.teamProj.utils.HttpResult;

import java.util.Map;

public interface StudentService {
    HttpResult studentLogin(String account, String password);

    HttpResult setStudentPassword(String account, String oldPassword, String password);

    HttpResult createResume(String account, Map<String,Object> map);

    HttpResult setStudentInfo(String account, Map<String, Object> map);
    HttpResult studentLogout();
    HttpResult queryRecruitmentInfo(String enterpriseName);
    HttpResult markRecruitmentInfo(String account, Integer recruitmentInfoId);
    HttpResult queryResume(String account);
    HttpResult deleteResume(String account, Integer resumeId);
}
