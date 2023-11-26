package com.teamProj.controller;

import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.service.EnterpriseService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

    @Resource
    EnterpriseService enterpriseService;

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
    HttpResult queryDepartment(@RequestParam(required = false) String departmentName) {
        return enterpriseService.queryDepartment(departmentName);
    }

    @DeleteMapping("/deleteDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteDepartment(@RequestParam String departmentName) {
        return enterpriseService.deleteDepartment(departmentName);
    }

    @PostMapping("/createNewRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewRecruitmentInfo(@RequestParam String departmentName
            , @RequestParam String jobTitle
            , @RequestParam String jobDescription
            , @RequestParam String city
            , @RequestParam Integer recruitNum
            , @RequestParam String byword
            , @RequestParam String jobDuties
            , @RequestParam Integer minSalary
            , @RequestParam Integer maxSalary
            , @RequestParam Character status
            , @RequestParam(required = false) String draftName) {
        RecruitmentInfo recruitmentInfo = new RecruitmentInfo(null
                , null
                , null
                , jobTitle
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
                , jobDuties
                , minSalary
                , maxSalary);
        return enterpriseService.createNewRecruitmentInfo(draftName, departmentName, recruitmentInfo);
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfo(@RequestParam(required = false) String city
            , @RequestParam(required = false) String salaryRange
            , @RequestParam String departmentName
            , @RequestParam Integer statusNum
            , @RequestParam Integer current) {
        return enterpriseService.queryRecruitmentInfo(city, salaryRange, departmentName, statusNum, current);
    }

    @GetMapping("queryRecruitmentInfoByDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfoByDraft(@RequestParam String draftName) {
        return enterpriseService.queryRecruitmentInfoByDraft(draftName);
    }

    @DeleteMapping("/deleteRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteRecruitmentInfo(@RequestParam String departmentName, @RequestParam String jobTitle) {
        return enterpriseService.deleteRecruitmentInfo(departmentName, jobTitle);
    }

    @PutMapping("/updateDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult updateDraft(@RequestParam String oldDraftName
            , @RequestParam String newDraftName
            , @RequestParam String jobTitle
            , @RequestParam String jobDescription
            , @RequestParam String city
            , @RequestParam Integer recruitNum
            , @RequestParam String byword
            , @RequestParam String jobDuties
            , @RequestParam Integer minSalary
            , @RequestParam Integer maxSalary
            , @RequestParam Character status) {
        RecruitmentInfo recruitmentInfo = new RecruitmentInfo(null
                , null
                , null
                , jobTitle
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
                , jobDuties
                , minSalary
                , maxSalary);
        return enterpriseService.updateDraft(oldDraftName, newDraftName, recruitmentInfo);
    }

    @GetMapping("/queryDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryDraft() {
        return enterpriseService.queryDraft();
    }

    @DeleteMapping("/deleteDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteDraft(@RequestParam String draftName) {
        return enterpriseService.deleteDraft(draftName);
    }

    @GetMapping("/queryJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryJobApplication(@RequestParam(required = false) String schoolName
            , @RequestParam(required = false) String departmentName
            , @RequestParam Integer current) {
        return enterpriseService.queryJobApplication(schoolName, departmentName, current);
    }

    @DeleteMapping("/deleteJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteJobApplication(@RequestParam String studentAccount
            , @RequestParam String departmentName
            , @RequestParam String jobTitle) {
        return enterpriseService.deleteJobApplication(studentAccount, departmentName, jobTitle);
    }

    @PatchMapping("/disagreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult disagreeJobApplication(@RequestParam Integer id, @RequestParam String rejectReason) {
        return enterpriseService.disagreeJobApplication(id, rejectReason);
    }

    @PostMapping("/agreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult agreeJobApplication(@RequestParam Integer id, @RequestParam String date, @RequestParam String position) {
        return enterpriseService.agreeJobApplication(id, Timestamp.valueOf(date), position);
    }
}
