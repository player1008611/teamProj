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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

    @Resource
    EnterpriseService enterpriseService;

    @GetMapping("/schoolList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult schoolList() {
        return enterpriseService.schoolList();
    }

    @GetMapping("/cityList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult cityList() {
        return enterpriseService.cityList();
    }

    @GetMapping("/hostList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult hostList() {
        return enterpriseService.hostList();
    }

    @GetMapping("/locationList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult locationList() {
        return enterpriseService.locationList();
    }

    @PostMapping("/login")
    HttpResult enterpriseLogin(@RequestParam String account, @RequestParam String password) {
        return enterpriseService.enterpriseLogin(account, password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult enterpriseLogout() {
        return enterpriseService.enterpriseLogout();
    }

    @PatchMapping("/enterpriseChangePassword")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult enterpriseChangePassword(
            @RequestParam String newPassword, @RequestParam String oldPassword) {
        return enterpriseService.enterpriseChangePassword(newPassword, oldPassword);
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
    HttpResult createNewRecruitmentInfo(
            @RequestParam String departmentName,
            @RequestParam String jobTitle,
            @RequestParam String jobDescription,
            @RequestParam String city,
            @RequestParam Integer recruitNum,
            @RequestParam String byword,
            @RequestParam String jobDuties,
            @RequestParam Integer minSalary,
            @RequestParam Integer maxSalary,
            @RequestParam Character status,
            @RequestParam(required = false) String draftName) {
        RecruitmentInfo recruitmentInfo =
                new RecruitmentInfo(
                        null,
                        null,
                        null,
                        jobTitle,
                        jobDescription,
                        null,
                        null,
                        city,
                        status,
                        null,
                        null,
                        null,
                        recruitNum,
                        null,
                        byword,
                        jobDuties,
                        minSalary,
                        maxSalary);
        return enterpriseService.createNewRecruitmentInfo(draftName, departmentName, recruitmentInfo);
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfo(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String salaryRange,
            @RequestParam String departmentName,
            @RequestParam Integer statusNum,
            @RequestParam Integer current) {
        return enterpriseService.queryRecruitmentInfo(
                city, salaryRange, departmentName, statusNum, current);
    }

    @GetMapping("queryRecruitmentInfoByDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfoByDraft(@RequestParam String draftName) {
        return enterpriseService.queryRecruitmentInfoByDraft(draftName);
    }

    @DeleteMapping("/deleteRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteRecruitmentInfo(
            @RequestParam String departmentName, @RequestParam String jobTitle) {
        return enterpriseService.deleteRecruitmentInfo(departmentName, jobTitle);
    }

    @PutMapping("/updateDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult updateDraft(
            @RequestParam String oldDraftName,
            @RequestParam String newDraftName,
            @RequestParam String jobTitle,
            @RequestParam String jobDescription,
            @RequestParam String city,
            @RequestParam Integer recruitNum,
            @RequestParam String byword,
            @RequestParam String jobDuties,
            @RequestParam Integer minSalary,
            @RequestParam Integer maxSalary,
            @RequestParam Character status) {
        RecruitmentInfo recruitmentInfo =
                new RecruitmentInfo(
                        null,
                        null,
                        null,
                        jobTitle,
                        jobDescription,
                        null,
                        null,
                        city,
                        status,
                        null,
                        null,
                        null,
                        recruitNum,
                        null,
                        byword,
                        jobDuties,
                        minSalary,
                        maxSalary);
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
    HttpResult queryJobApplication(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) Integer code,
            @RequestParam Integer current) {
        return enterpriseService.queryJobApplication(schoolName, departmentName, code, current);
    }

    @DeleteMapping("/deleteJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteJobApplication(
            @RequestParam String studentAccount,
            @RequestParam String departmentName,
            @RequestParam String jobTitle) {
        return enterpriseService.deleteJobApplication(studentAccount, departmentName, jobTitle);
    }

    @PatchMapping("/disagreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult disagreeJobApplication(@RequestParam Integer id, @RequestParam String rejectReason) {
        return enterpriseService.disagreeJobApplication(id, rejectReason);
    }

    @PostMapping("/agreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult agreeJobApplication(
            @RequestParam Integer id, @RequestParam String date, @RequestParam String position) {
        return enterpriseService.agreeJobApplication(id, date, position);
    }

    @GetMapping("/queryResume")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryResume(@RequestParam Integer jobApplicationId) {
        return enterpriseService.queryResume(jobApplicationId);
    }

    @PostMapping("/createFair")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createFair(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String location,
            @RequestParam String host,
            @RequestParam String schoolName) {
        return enterpriseService.createFair(
                title,
                content,
                Timestamp.valueOf(startTime + ":00"),
                Timestamp.valueOf(endTime + ":00"),
                location,
                host,
                schoolName);
    }

    @GetMapping("/queryFair")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryFair(
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer code,
            @RequestParam Integer current) {
        Timestamp timestamp = null;
        if (Objects.isNull(date) || date.isEmpty()) {
            timestamp = null;
        } else {
            timestamp = Timestamp.valueOf(date + " 00:00:00");
        }
        return enterpriseService.queryFair(host, location, schoolName, timestamp, code, current);
    }

    @PutMapping("/updateFair")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult updateFair(
            @RequestParam Integer id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String location,
            @RequestParam String host,
            @RequestParam String schoolName) {
        return enterpriseService.updateFair(
                id,
                title,
                content,
                Timestamp.valueOf(startTime + ":00"),
                Timestamp.valueOf(endTime + ":00"),
                location,
                host,
                schoolName);
    }

    @DeleteMapping("/deleteFair")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteFair(@RequestParam Integer id) {
        return enterpriseService.deleteFair(id);
    }

    @GetMapping("/queryInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryInfo() {
        return enterpriseService.queryInfo();
    }

    @PatchMapping("/updateInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult updateInfo(
            @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String birthday,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String graduationSchool,
            @RequestParam(required = false) String tel) {
        return enterpriseService.updateInfo(
                avatar.isEmpty() ? null : avatar,
                name.isEmpty() ? null : name,
                birthday.isEmpty() ? null : Date.valueOf(birthday),
                age,
                gender.isEmpty() ? null : gender,
                graduationSchool.isEmpty() ? null : graduationSchool,
                tel.isEmpty() ? null : tel);
    }

    @GetMapping("/queryInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryInterview(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) Integer code,
            @RequestParam Integer current) {
        return enterpriseService.queryInterview(date, school, code, current);
    }

    @DeleteMapping("/deleteInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteInterview(@RequestParam Integer id) {
        return enterpriseService.deleteInterview(id);
    }

    @PatchMapping("/agreeInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult agreeInterview(@RequestParam Integer id) {
        return enterpriseService.agreeInterview(id);
    }

    @PatchMapping("/disagreeInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult disagreeInterview(@RequestParam Integer id) {
        return enterpriseService.disagreeInterview(id);
    }

    @GetMapping("/applicationAnalysisByDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisByDepartment() {
        return enterpriseService.applicationAnalysisByDepartment();
    }

    @GetMapping("/applicationAnalysisBySchool")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisBySchool() {
        return enterpriseService.applicationAnalysisBySchool();
    }

    @GetMapping("/applicationAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisByPass() {
        return enterpriseService.applicationAnalysisByPass();
    }

    @GetMapping("/fairAnalysisByMon")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisByMon(@RequestParam String year) {
        return enterpriseService.fairAnalysisByMon(year);
    }

    @GetMapping("/fairAnalysisBySchool")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisBySchool(@RequestParam String mon) {
        return enterpriseService.fairAnalysisBySchool(mon);
    }

    @GetMapping("/fairAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisByPass() {
        return enterpriseService.fairAnalysisByPass();
    }

    @GetMapping("/interviewAnalysisByMon")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByMon(@RequestParam String year) {
        return enterpriseService.interviewAnalysisByMon(year);
    }

    @GetMapping("/interviewAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByPass() {
        return enterpriseService.interviewAnalysisByPass();
    }

    @GetMapping("/interviewAnalysisByDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByDepartment() {
        return enterpriseService.interviewAnalysisByDepartment();
    }

    @GetMapping("/recruitmentAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByPass() {
        return enterpriseService.recruitmentAnalysisByPass();
    }

    @GetMapping("/recruitmentAnalysisByCity")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByCity() {
        return enterpriseService.recruitmentAnalysisByCity();
    }

    @GetMapping("/recruitmentAnalysisByMaxSalary")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByMaxSalary() {
        return enterpriseService.recruitmentAnalysisByMaxSalary();
    }

    @GetMapping("/querySentMessageList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult querySentMessageList(@RequestParam Integer current) {
        return enterpriseService.querySentMessageList(current);
    }

    @GetMapping("/querySentMessage")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult querySentMessage(@RequestParam Integer id) {
        return enterpriseService.querySentMessage(id);
    }

    @DeleteMapping("/deleteSentMessage")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteSentMessage(@RequestParam Integer id) {
        return enterpriseService.deleteSentMessage(id);
    }

    @GetMapping("/queryRecentContacts")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecentContacts() {
        return enterpriseService.queryRecentContacts();
    }

    @PostMapping("/sendMessage")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult sendMessage(
            @RequestParam String account,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String content) {
        return enterpriseService.sendMessage(account, type, title, content);
    }
}
