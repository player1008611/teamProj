package com.teamProj.service;

import com.teamProj.utils.HttpResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

public interface StudentService {
    HttpResult studentLogin(String account, String password);

    HttpResult setStudentPassword(String account, String oldPassword, String password);

    HttpResult createResume(String account, MultipartFile imageByte, String selfDescription, String careerObjective,
                            String educationExperience, String InternshipExperience, String projectExperience,
                            String certificates, String skills, String resumeName, MultipartFile attachPDF);

    HttpResult studentRegister(String account, String password);

    HttpResult setStudentInfo(String account, Map<String, Object> map);

    HttpResult studentLogout();

    HttpResult queryRecruitmentInfo(String queryInfo,String minSalary,String maxSalary,boolean mark);

    HttpResult queryJobApplicationDetail(Integer recruitmentId);

    HttpResult markRecruitmentInfo(String account, Integer recruitmentInfoId);

    HttpResult queryResume();

    HttpResult queryResumeDetail(Integer resumeId) throws SQLException;

    HttpResult deleteResume(Integer resumeId);

    HttpResult createJobApplication(String account, Integer recruitmentInfoId, Integer resumeId);

    HttpResult deleteJobApplication(Integer applicationId);

    HttpResult queryJobApplication(String account);
}
