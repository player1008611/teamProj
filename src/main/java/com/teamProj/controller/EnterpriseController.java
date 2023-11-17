package com.teamProj.controller;

import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/createNewDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewDepartment(@RequestParam String departmentName) {
        return enterpriseService.createNewDepartment(departmentName);
    }

    @GetMapping("/queryDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryDepartment() {
        return enterpriseService.queryDepartment();
    }

    @PostMapping("/createNewRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewRecruitmentInfo(@RequestParam String departmentName
            , @RequestParam String jobTitle
            , @RequestParam String salaryRange
            , @RequestParam String jobDescription
            , @RequestParam String city
            , @RequestParam Integer recruitNum
            , @RequestParam String byword
            , @RequestParam String jobDuties
            , @RequestParam Character status
            , @RequestParam(required = false) String draftName) {
        RecruitmentInfo recruitmentInfo = new RecruitmentInfo(null
                , null
                , null
                , jobTitle
                , salaryRange
                , jobDescription
                , null
                , null
                , city
                , status
                , null
                , null
                , null
                , recruitNum
                , null
                , byword
                , jobDuties);
        return enterpriseService.createNewRecruitmentInfo(draftName, departmentName, recruitmentInfo);
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfo(@RequestParam String departmentName, @RequestParam Integer current) {
        return enterpriseService.queryRecruitmentInfo(departmentName, current);
    }

    @DeleteMapping("/deleteRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteRecruitmentInfo(@RequestParam String departmentName, @RequestParam String jobTitle) {
        return enterpriseService.deleteRecruitmentInfo(departmentName, jobTitle);
    }
}
