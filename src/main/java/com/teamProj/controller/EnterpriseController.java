package com.teamProj.controller;

import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    RecruitmentInfoDao recruitmentInfoDao;

    @PostMapping("/login")
    HttpResult enterpriseLogin(@RequestParam String account, @RequestParam String password) {
        return enterpriseService.enterpriseLogin(account, password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult enterpriseLogout() {
        return enterpriseService.enterpriseLogout();
    }

    @PutMapping("/createNewDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewDepartment(@RequestParam String enterpriseName, @RequestParam String departmentName) {
        return enterpriseService.createNewDepartment(enterpriseName, departmentName);
    }

    @PutMapping("/createNewRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewRecruitmentInfo(@RequestParam String departmentName
            , @RequestParam String jobTitle
            , @RequestParam String salaryRange
            , @RequestParam String jobDescription
            , @RequestParam String city
            , @RequestParam Integer recruitNum
            , @RequestParam String byword
            , @RequestParam String jobDuties) {
        RecruitmentInfo recruitmentInfo = new RecruitmentInfo(null
                , null
                , null
                , jobTitle
                , salaryRange
                , jobDescription
                , null
                , null
                , city
                , '0'
                , null
                , null
                , null
                , recruitNum
                , null
                , byword
                , jobDuties);
        return enterpriseService.createNewRecruitmentInfo(departmentName, recruitmentInfo);
    }
}
