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

/**
 * The type Enterprise controller.
 */
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

    /**
     * The Enterprise service.
     */
    @Resource
    EnterpriseService enterpriseService;

    /**
     * School list http result.
     *
     * @return the http result
     */
    @GetMapping("/schoolList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult schoolList() {
        return enterpriseService.schoolList();
    }

    /**
     * City list http result.
     *
     * @return the http result
     */
    @GetMapping("/cityList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult cityList() {
        return enterpriseService.cityList();
    }

    /**
     * Host list http result.
     *
     * @return the http result
     */
    @GetMapping("/hostList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult hostList() {
        return enterpriseService.hostList();
    }

    /**
     * Location list http result.
     *
     * @return the http result
     */
    @GetMapping("/locationList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult locationList() {
        return enterpriseService.locationList();
    }

    /**
     * Enterprise login http result.
     *
     * @param account  the account
     * @param password the password
     * @return the http result
     */
    @PostMapping("/login")
    HttpResult enterpriseLogin(@RequestParam String account, @RequestParam String password) {
        return enterpriseService.enterpriseLogin(account, password);
    }

    /**
     * Enterprise logout http result.
     *
     * @return the http result
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult enterpriseLogout() {
        return enterpriseService.enterpriseLogout();
    }

    /**
     * Enterprise change password http result.
     *
     * @param newPassword the new password
     * @param oldPassword the old password
     * @return the http result
     */
    @PatchMapping("/enterpriseChangePassword")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult enterpriseChangePassword(
            @RequestParam String newPassword, @RequestParam String oldPassword) {
        return enterpriseService.enterpriseChangePassword(newPassword, oldPassword);
    }

    /**
     * Create new department http result.
     *
     * @param departmentName the department name
     * @return the http result
     */
    @PostMapping("/createNewDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult createNewDepartment(@RequestParam String departmentName) {
        return enterpriseService.createNewDepartment(departmentName);
    }

    /**
     * Query department http result.
     *
     * @param departmentName the department name
     * @return the http result
     */
    @GetMapping("/queryDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryDepartment(@RequestParam(required = false) String departmentName) {
        return enterpriseService.queryDepartment(departmentName);
    }

    /**
     * Delete department http result.
     *
     * @param departmentName the department name
     * @return the http result
     */
    @DeleteMapping("/deleteDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteDepartment(@RequestParam String departmentName) {
        return enterpriseService.deleteDepartment(departmentName);
    }

    /**
     * Create new recruitment info http result.
     *
     * @param departmentName the department name
     * @param jobTitle       the job title
     * @param jobDescription the job description
     * @param city           the city
     * @param recruitNum     the recruit num
     * @param byword         the byword
     * @param jobDuties      the job duties
     * @param minSalary      the min salary
     * @param maxSalary      the max salary
     * @param status         the status
     * @param draftName      the draft name
     * @return the http result
     */
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

    /**
     * Query recruitment info http result.
     *
     * @param city           the city
     * @param salaryRange    the salary range
     * @param departmentName the department name
     * @param statusNum      the status num
     * @param current        the current
     * @return the http result
     */
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

    /**
     * Query recruitment info by draft http result.
     *
     * @param draftName the draft name
     * @return the http result
     */
    @GetMapping("queryRecruitmentInfoByDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecruitmentInfoByDraft(@RequestParam String draftName) {
        return enterpriseService.queryRecruitmentInfoByDraft(draftName);
    }

    /**
     * Delete recruitment info http result.
     *
     * @param departmentName the department name
     * @param jobTitle       the job title
     * @return the http result
     */
    @DeleteMapping("/deleteRecruitmentInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteRecruitmentInfo(
            @RequestParam String departmentName, @RequestParam String jobTitle) {
        return enterpriseService.deleteRecruitmentInfo(departmentName, jobTitle);
    }

    /**
     * Update draft http result.
     *
     * @param oldDraftName   the old draft name
     * @param newDraftName   the new draft name
     * @param jobTitle       the job title
     * @param jobDescription the job description
     * @param city           the city
     * @param recruitNum     the recruit num
     * @param byword         the byword
     * @param jobDuties      the job duties
     * @param minSalary      the min salary
     * @param maxSalary      the max salary
     * @param status         the status
     * @return the http result
     */
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

    /**
     * Query draft http result.
     *
     * @return the http result
     */
    @GetMapping("/queryDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryDraft() {
        return enterpriseService.queryDraft();
    }

    /**
     * Delete draft http result.
     *
     * @param draftName the draft name
     * @return the http result
     */
    @DeleteMapping("/deleteDraft")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteDraft(@RequestParam String draftName) {
        return enterpriseService.deleteDraft(draftName);
    }

    /**
     * Query job application http result.
     *
     * @param schoolName     the school name
     * @param departmentName the department name
     * @param code           the code
     * @param current        the current
     * @return the http result
     */
    @GetMapping("/queryJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryJobApplication(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) Integer code,
            @RequestParam Integer current) {
        return enterpriseService.queryJobApplication(schoolName, departmentName, code, current);
    }

    /**
     * Delete job application http result.
     *
     * @param studentAccount the student account
     * @param departmentName the department name
     * @param jobTitle       the job title
     * @return the http result
     */
    @DeleteMapping("/deleteJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteJobApplication(
            @RequestParam String studentAccount,
            @RequestParam String departmentName,
            @RequestParam String jobTitle) {
        return enterpriseService.deleteJobApplication(studentAccount, departmentName, jobTitle);
    }

    /**
     * Disagree job application http result.
     *
     * @param id           the id
     * @param rejectReason the reject reason
     * @return the http result
     */
    @PatchMapping("/disagreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult disagreeJobApplication(@RequestParam Integer id, @RequestParam String rejectReason) {
        return enterpriseService.disagreeJobApplication(id, rejectReason);
    }

    /**
     * Agree job application http result.
     *
     * @param id       the id
     * @param date     the date
     * @param position the position
     * @return the http result
     */
    @PostMapping("/agreeJobApplication")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult agreeJobApplication(
            @RequestParam Integer id, @RequestParam String date, @RequestParam String position) {
        return enterpriseService.agreeJobApplication(id, date, position);
    }

    /**
     * Query resume http result.
     *
     * @param jobApplicationId the job application id
     * @return the http result
     */
    @GetMapping("/queryResume")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryResume(@RequestParam Integer jobApplicationId) {
        return enterpriseService.queryResume(jobApplicationId);
    }

    /**
     * Create fair http result.
     *
     * @param title      the title
     * @param content    the content
     * @param startTime  the start time
     * @param endTime    the end time
     * @param location   the location
     * @param host       the host
     * @param schoolName the school name
     * @return the http result
     */
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

    /**
     * Query fair http result.
     *
     * @param host       the host
     * @param location   the location
     * @param schoolName the school name
     * @param date       the date
     * @param code       the code
     * @param current    the current
     * @return the http result
     */
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

    /**
     * Update fair http result.
     *
     * @param id         the id
     * @param title      the title
     * @param content    the content
     * @param startTime  the start time
     * @param endTime    the end time
     * @param location   the location
     * @param host       the host
     * @param schoolName the school name
     * @return the http result
     */
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

    /**
     * Delete fair http result.
     *
     * @param id the id
     * @return the http result
     */
    @DeleteMapping("/deleteFair")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteFair(@RequestParam Integer id) {
        return enterpriseService.deleteFair(id);
    }

    /**
     * Query info http result.
     *
     * @return the http result
     */
    @GetMapping("/queryInfo")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryInfo() {
        return enterpriseService.queryInfo();
    }

    /**
     * Update info http result.
     *
     * @param avatar           the avatar
     * @param name             the name
     * @param birthday         the birthday
     * @param age              the age
     * @param gender           the gender
     * @param graduationSchool the graduation school
     * @param tel              the tel
     * @return the http result
     */
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
                avatar,
                name.isEmpty() ? null : name,
                birthday.isEmpty() ? null : Date.valueOf(birthday),
                age,
                gender.isEmpty() ? null : gender,
                graduationSchool.isEmpty() ? null : graduationSchool,
                tel.isEmpty() ? null : tel);
    }

    /**
     * Query interview http result.
     *
     * @param date    the date
     * @param school  the school
     * @param code    the code
     * @param current the current
     * @return the http result
     */
    @GetMapping("/queryInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryInterview(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) Integer code,
            @RequestParam Integer current) {
        return enterpriseService.queryInterview(date, school, code, current);
    }

    /**
     * Delete interview http result.
     *
     * @param id the id
     * @return the http result
     */
    @DeleteMapping("/deleteInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteInterview(@RequestParam Integer id) {
        return enterpriseService.deleteInterview(id);
    }

    /**
     * Agree interview http result.
     *
     * @param id the id
     * @return the http result
     */
    @PatchMapping("/agreeInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult agreeInterview(@RequestParam Integer id) {
        return enterpriseService.agreeInterview(id);
    }

    /**
     * Disagree interview http result.
     *
     * @param id the id
     * @return the http result
     */
    @PatchMapping("/disagreeInterview")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult disagreeInterview(@RequestParam Integer id) {
        return enterpriseService.disagreeInterview(id);
    }

    /**
     * Application analysis by department http result.
     *
     * @return the http result
     */
    @GetMapping("/applicationAnalysisByDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisByDepartment() {
        return enterpriseService.applicationAnalysisByDepartment();
    }

    /**
     * Application analysis by school http result.
     *
     * @return the http result
     */
    @GetMapping("/applicationAnalysisBySchool")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisBySchool() {
        return enterpriseService.applicationAnalysisBySchool();
    }

    /**
     * Application analysis by pass http result.
     *
     * @return the http result
     */
    @GetMapping("/applicationAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult applicationAnalysisByPass() {
        return enterpriseService.applicationAnalysisByPass();
    }

    /**
     * Fair analysis by mon http result.
     *
     * @param year the year
     * @return the http result
     */
    @GetMapping("/fairAnalysisByMon")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisByMon(@RequestParam String year) {
        return enterpriseService.fairAnalysisByMon(year);
    }

    /**
     * Fair analysis by school http result.
     *
     * @param mon the mon
     * @return the http result
     */
    @GetMapping("/fairAnalysisBySchool")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisBySchool(@RequestParam String mon) {
        return enterpriseService.fairAnalysisBySchool(mon);
    }

    /**
     * Fair analysis by pass http result.
     *
     * @return the http result
     */
    @GetMapping("/fairAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult fairAnalysisByPass() {
        return enterpriseService.fairAnalysisByPass();
    }

    /**
     * Interview analysis by mon http result.
     *
     * @param year the year
     * @return the http result
     */
    @GetMapping("/interviewAnalysisByMon")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByMon(@RequestParam String year) {
        return enterpriseService.interviewAnalysisByMon(year);
    }

    /**
     * Interview analysis by pass http result.
     *
     * @return the http result
     */
    @GetMapping("/interviewAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByPass() {
        return enterpriseService.interviewAnalysisByPass();
    }

    /**
     * Interview analysis by department http result.
     *
     * @return the http result
     */
    @GetMapping("/interviewAnalysisByDepartment")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult interviewAnalysisByDepartment() {
        return enterpriseService.interviewAnalysisByDepartment();
    }

    /**
     * Recruitment analysis by pass http result.
     *
     * @return the http result
     */
    @GetMapping("/recruitmentAnalysisByPass")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByPass() {
        return enterpriseService.recruitmentAnalysisByPass();
    }

    /**
     * Recruitment analysis by city http result.
     *
     * @return the http result
     */
    @GetMapping("/recruitmentAnalysisByCity")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByCity() {
        return enterpriseService.recruitmentAnalysisByCity();
    }

    /**
     * Recruitment analysis by max salary http result.
     *
     * @return the http result
     */
    @GetMapping("/recruitmentAnalysisByMaxSalary")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult recruitmentAnalysisByMaxSalary() {
        return enterpriseService.recruitmentAnalysisByMaxSalary();
    }

    /**
     * Query sent message list http result.
     *
     * @param current the current
     * @return the http result
     */
    @GetMapping("/querySentMessageList")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult querySentMessageList(@RequestParam Integer current) {
        return enterpriseService.querySentMessageList(current);
    }

    /**
     * Query sent message http result.
     *
     * @param id the id
     * @return the http result
     */
    @GetMapping("/querySentMessage")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult querySentMessage(@RequestParam Integer id) {
        return enterpriseService.querySentMessage(id);
    }

    /**
     * Delete sent message http result.
     *
     * @param id the id
     * @return the http result
     */
    @DeleteMapping("/deleteSentMessage")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult deleteSentMessage(@RequestParam Integer id) {
        return enterpriseService.deleteSentMessage(id);
    }

    /**
     * Query recent contacts http result.
     *
     * @return the http result
     */
    @GetMapping("/queryRecentContacts")
    @PreAuthorize("hasAuthority('enterprise')")
    HttpResult queryRecentContacts() {
        return enterpriseService.queryRecentContacts();
    }

    /**
     * Send message http result.
     *
     * @param account the account
     * @param type    the type
     * @param title   the title
     * @param content the content
     * @return the http result
     */
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
