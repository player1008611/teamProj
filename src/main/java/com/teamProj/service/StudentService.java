package com.teamProj.service;

import com.teamProj.utils.HttpResult;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Map;

public interface StudentService {
    HttpResult studentLogin(String account, String password);
    HttpResult studentLogout();
    HttpResult studentRegister(String account, String password, String schoolName, String name, String phoneNumber);
    HttpResult querySchool();

    HttpResult setStudentPassword(String account, String oldPassword, String password);

    HttpResult createResume(String account, MultipartFile imageByte, String selfDescription, String careerObjective,
                            String educationExperience, String InternshipExperience, String projectExperience,
                            String certificates, String skills, String resumeName);

    HttpResult editResume(String resumeId, String selfDescription, String careerObjective,
                          String educationExperience, String InternshipExperience, String projectExperience,
                          String certificates, String skills, String resumeName);


    HttpResult setStudentInfo(String account, String name,  String gender, String wechat, String qq, Integer collegeId, Integer majorId, String address, Integer age);



    HttpResult queryRecruitmentInfo(String account, String queryInfo, String minSalary, String maxSalary, boolean mark);

    HttpResult queryJobApplicationDetail(Integer recruitmentId);

    HttpResult markRecruitmentInfo(String account, Integer recruitmentInfoId);

    HttpResult queryResume();

    HttpResult queryResumeDetail(Integer resumeId) throws SQLException;

    HttpResult deleteResume(Integer resumeId);

    HttpResult createJobApplication(String account, Integer recruitmentInfoId, Integer resumeId);

    HttpResult deleteJobApplication(Integer applicationId);

    HttpResult queryJobApplication(String account);

    HttpResult verification(String email);

    HttpResult queryStudentInfo();
    HttpResult queryInterviewInfo(String mark);
}
