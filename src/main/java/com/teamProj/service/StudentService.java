package com.teamProj.service;

import com.teamProj.entity.Resume;
import com.teamProj.utils.HttpResult;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StudentService {
    HttpResult studentLogin(String account, String password);
    HttpResult studentLogout();
    HttpResult studentRegister(String account, String password, String schoolName,String collegeName,String majorName, String name, String phoneNumber);
    HttpResult querySchool(Integer depth,String queryInfo);

    HttpResult setStudentPassword(String account, String oldPassword, String password);

//    HttpResult createResume(String account, MultipartFile imageByte, String selfDescription, String careerObjective,
//                            String educationExperience, String InternshipExperience, String projectExperience,
//                            String certificates, String skills, String resumeName);
    HttpResult createResume(String account, Resume resume);

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

    HttpResult verificationEmail(String email);
    HttpResult verificationPhone(String phone) throws Exception;

    HttpResult verificationPhoneCheck(String phone, String code) throws Exception;
    HttpResult queryStudentInfo();
    HttpResult queryInterviewInfo(String queryInfo);
    HttpResult homepage();

    HttpResult getRecommendation(Integer page);

    HttpResult queryFair();

    HttpResult queryMessageList();

    HttpResult queryMessage(Integer messageId,String queryInfo);

    HttpResult deleteAllMessage();

    HttpResult deleteMessage(Integer messageId);

    HttpResult hasReadAllMessage();

    HttpResult hasReadMessage(Integer messageId);

    HttpResult editPhoneNumber(String phoneNumber);

    HttpResult queryCollegeMajor(String schoolName);
}
