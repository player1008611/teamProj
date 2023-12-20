package com.teamProj.service;

import com.teamProj.utils.HttpResult;
import org.springframework.web.multipart.MultipartFile;

public interface AdministratorService {
    HttpResult administratorLogin(String account, String password);

    HttpResult administratorLogout();

    HttpResult queryHome();

    HttpResult queryStudent(
            String name, String schoolName, Character status, Integer current, Integer size);

    HttpResult resetStudentPassword(String account);

    HttpResult enableStudentAccount(String account);

    HttpResult disableStudentAccount(String account);

    HttpResult deleteStudentAccount(String account);

    HttpResult queryEnterprise(String name, Integer current, Integer size);

    HttpResult queryEnterpriseUser(
            String enterpriseName, String userName, Integer current, Integer size);

    HttpResult createNewEnterprise(String name, String url);

    HttpResult createNewEnterpriseUser(
            String account, String enterpriseName, String name, String tel);

    HttpResult resetEnterpriseUserPassword(String account);

    HttpResult enableEnterpriseUser(String account);

    HttpResult disableEnterpriseUser(String account);

    HttpResult deleteEnterpriseUser(String account);

    HttpResult querySchoolUser(String principal, Character status, Integer current, Integer size);

    HttpResult createNewSchoolUser(String account, String schoolName, String principal, String tel);

    HttpResult resetSchoolUserPassword(String account);

    HttpResult enableSchoolUser(String account);

    HttpResult disableSchoolUser(String account);

    HttpResult deleteSchoolUser(String account);

    HttpResult queryRecruitmentInfo(
            String companyName, String departmentName, String jobTitle, Integer current, Integer size);

    HttpResult auditRecruitmentInfo(
            String enterpriseName,
            String departmentName,
            String jobTitle,
            String status,
            String rejectReason);

    HttpResult createAnnouncement(
            String title, MultipartFile cover, String category, String content, MultipartFile data);

    HttpResult deleteAnnouncement(Integer id);

    HttpResult queryAnnouncement(String title, String category, Integer current);

    HttpResult queryAnnouncementCover(Integer id);

    HttpResult queryAnnouncementData(Integer id);
}
