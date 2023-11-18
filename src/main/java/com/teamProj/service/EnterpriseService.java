package com.teamProj.service;

import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.utils.HttpResult;

public interface EnterpriseService {

    HttpResult enterpriseLogin(String account, String password);

    HttpResult enterpriseLogout();

    HttpResult createNewDepartment(String departmentName);

    HttpResult queryDepartment(String departmentName);

    HttpResult createNewRecruitmentInfo(String draftName, String departmentName, RecruitmentInfo recruitmentInfo);

    HttpResult queryRecruitmentInfo(String city, String salaryRange, String departmentName, Integer current);

    HttpResult deleteRecruitmentInfo(String departmentName, String jobTitle);

    HttpResult updateDraft(String oldDraftName, String newDraftName, RecruitmentInfo recruitmentInfo);

    HttpResult queryDraft();

    HttpResult deleteDraft(String draftName);

    HttpResult queryJobApplication(String schoolName, String departmentName, Integer current);

    HttpResult deleteJobApplication(String departmentName, String jobTitle);
}
