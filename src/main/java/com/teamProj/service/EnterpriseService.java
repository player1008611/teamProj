package com.teamProj.service;

import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.utils.HttpResult;
import org.springframework.security.config.web.servlet.headers.HttpPublicKeyPinningDsl;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Timestamp;

public interface EnterpriseService {

    HttpResult schoolList();

    HttpResult cityList();

    HttpResult hostList();

    HttpResult locationList();

    HttpResult enterpriseLogin(String account, String password);

    HttpResult enterpriseLogout();

    HttpResult enterpriseChangePassword(String newPassword, String oldPassword);

    HttpResult createNewDepartment(String departmentName);

    HttpResult queryDepartment(String departmentName);

    HttpResult deleteDepartment(String departmentName);

    HttpResult createNewRecruitmentInfo(String draftName, String departmentName, RecruitmentInfo recruitmentInfo);

    HttpResult queryRecruitmentInfo(String city, String salaryRange, String departmentName, Integer statusName, Integer current);

    HttpResult queryRecruitmentInfoByDraft(String draftName);

    HttpResult deleteRecruitmentInfo(String departmentName, String jobTitle);

    HttpResult updateDraft(String oldDraftName, String newDraftName, RecruitmentInfo recruitmentInfo);

    HttpResult queryDraft();

    HttpResult deleteDraft(String draftName);

    HttpResult queryJobApplication(String schoolName, String departmentName, Integer code, Integer current);

    HttpResult queryResume(Integer jobApplicationId);

    HttpResult deleteJobApplication(String studentAccount, String departmentName, String jobTitle);

    HttpResult disagreeJobApplication(Integer id, String rejectReason);

    HttpResult agreeJobApplication(Integer id, String date, String position);

    HttpResult createFair(String title, String content, Timestamp startTime, Timestamp endTime, String location, String host, String schoolName);

    HttpResult queryFair(String host, String location, String schoolName, Timestamp date, Integer code, Integer current);

    HttpResult updateFair(Integer id, String title, String content, Timestamp startTime, Timestamp endTime, String location, String host, String schoolName);

    HttpResult deleteFair(Integer id);

    HttpResult queryInfo();

    HttpResult updateInfo(MultipartFile avatar, String name, Date birthday, Integer age, String gender, String graduationSchool);

    HttpResult queryInterview(String date, String school, Integer status, Integer current);

    HttpResult deleteInterview(Integer id);

    HttpResult agreeInterview(Integer id);

    HttpResult disagreeInterview(Integer id);

    HttpResult applicationAnalysisByDepartment();

    HttpResult applicationAnalysisBySchool();

    HttpResult applicationAnalysisByPass();

    HttpResult fairAnalysisByMon(String year);

    HttpResult fairAnalysisBySchool(String mon);

    HttpResult fairAnalysisByPass();

    HttpResult interviewAnalysisByMon(String year);

    HttpResult interviewAnalysisByPass();

    HttpResult interviewAnalysisByDepartment();

    HttpResult recruitmentAnalysisByPass();

    HttpResult recruitmentAnalysisByCity();

    HttpResult recruitmentAnalysisByMaxSalary();
}
