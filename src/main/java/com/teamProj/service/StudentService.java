package com.teamProj.service;

import com.teamProj.utils.HttpResult;

import java.util.Map;

public interface StudentService {
    HttpResult studentLogin(String account, String password);

    HttpResult setStudentPassword(String account, String oldPassword, String password);

    HttpResult createResume(String account, byte[] imageByte, String selfDescription, String careerObjective,
                            String educationExperience, String InternshipExperience, String projectExperience,
                            String certificates, String skills,String resumeName,byte[] attachPDF);

    HttpResult setStudentInfo(String account, Map<String, Object> map);

    HttpResult studentLogout();

    HttpResult queryRecruitmentInfo(String queryInfo,String minSalary,String maxSalary,boolean mark);

    HttpResult queryJobApplicationDetail(Integer recruitmentId);

    HttpResult markRecruitmentInfo(String account, Integer recruitmentInfoId);

    HttpResult queryResume(String account);

    HttpResult deleteResume(String account, Integer resumeId);

    HttpResult createJobApplication(String account, Integer recruitmentInfoId, Integer resumeId);

    HttpResult deleteJobApplication(Integer applicationId);

    HttpResult queryJobApplication(String account);
}
