package com.teamProj.service;

import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.utils.HttpResult;

public interface EnterpriseService {

    HttpResult enterpriseLogin(String account, String password);

    HttpResult enterpriseLogout();

    HttpResult createNewDepartment(String departmentName);

    HttpResult queryDepartment();

    HttpResult createNewRecruitmentInfo(String draftName, String departmentName, RecruitmentInfo recruitmentInfo);

    HttpResult queryRecruitmentInfo(String departmentName, Integer current);

    HttpResult updateDraft(String oldDraftName,String newDraftName,RecruitmentInfo recruitmentInfo);

    HttpResult deleteRecruitmentInfo(String departmentName, String jobTitle);
}
