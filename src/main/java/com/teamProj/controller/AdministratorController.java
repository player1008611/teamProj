package com.teamProj.controller;

import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * The type Administrator controller.
 */
@RestController
@RequestMapping("/admin")
public class AdministratorController {
    /**
     * The Administrator service.
     */
    @Resource
    AdministratorService administratorService;

    /**
     * Administrator login http result.
     *
     * @param account  the account
     * @param password the password
     * @return the http result
     */
    @PostMapping("/login")
    HttpResult administratorLogin(@RequestParam String account, @RequestParam String password) {
        return administratorService.administratorLogin(account, password);
    }

    /**
     * Administrator logout http result.
     *
     * @return the http result
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult administratorLogout() {
        return administratorService.administratorLogout();
    }

    /**
     * Query home http result.
     *
     * @return the http result
     */
    @GetMapping("/queryHome")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryHome() {
        return administratorService.queryHome();
    }

    /**
     * Query student http result.
     *
     * @param name       the name
     * @param schoolName the school name
     * @param status     the status
     * @param current    the current
     * @param size       the size
     * @return the http result
     */
    @GetMapping("/queryStudent")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryStudent(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "schoolName", required = false) String schoolName,
            @RequestParam(value = "status", required = false) Character status,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return administratorService.queryStudent(name, schoolName, status, current, size);
    }

    /**
     * Reset student password http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/resetStudentPassword")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult resetStudentPassword(@RequestParam String account) {
        return administratorService.resetStudentPassword(account);
    }

    /**
     * Enable student account http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/enableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableStudentAccount(@RequestParam String account) {
        return administratorService.enableStudentAccount(account);
    }

    /**
     * Disable student account http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/disableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableStudentAccount(@RequestParam String account) {
        return administratorService.disableStudentAccount(account);
    }

    /**
     * Delete student http result.
     *
     * @param account the account
     * @return the http result
     */
    @DeleteMapping("/deleteStudent")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteStudent(@RequestParam String account) {
        return administratorService.deleteStudentAccount(account);
    }

    /**
     * Query enterprise http result.
     *
     * @param name    the name
     * @param current the current
     * @param size    the size
     * @return the http result
     */
    @GetMapping("/queryEnterprise")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryEnterprise(
            @RequestParam(required = false) String name,
            @RequestParam Integer current,
            @RequestParam Integer size) {
        return administratorService.queryEnterprise(name, current, size);
    }

    /**
     * Query enterprise user http result.
     *
     * @param enterpriseName the enterprise name
     * @param userName       the user name
     * @param current        the current
     * @param size           the size
     * @return the http result
     */
    @GetMapping("/queryEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryEnterpriseUser(
            @RequestParam String enterpriseName,
            @RequestParam(required = false) String userName,
            @RequestParam Integer current,
            @RequestParam Integer size) {
        return administratorService.queryEnterpriseUser(enterpriseName, userName, current, size);
    }

    /**
     * Create new enterprise http result.
     *
     * @param name the name
     * @param url  the url
     * @return the http result
     */
    @PostMapping("/createNewEnterprise")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewEnterprise(@RequestParam String name, @RequestParam String url) {
        return administratorService.createNewEnterprise(name, url);
    }

    /**
     * Create new enterprise user http result.
     *
     * @param account        the account
     * @param enterpriseName the enterprise name
     * @param name           the name
     * @param tel            the tel
     * @return the http result
     */
    @PostMapping("/createNewEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewEnterpriseUser(
            @RequestParam String account,
            @RequestParam String enterpriseName,
            @RequestParam String name,
            @RequestParam String tel) {
        return administratorService.createNewEnterpriseUser(account, enterpriseName, name, tel);
    }

    /**
     * Reset enterprise user password http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("resetEnterpriseUserPassword")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult resetEnterpriseUserPassword(@RequestParam String account) {
        return administratorService.resetEnterpriseUserPassword(account);
    }

    /**
     * Enable enterprise user http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/enableEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableEnterpriseUser(@RequestParam String account) {
        return administratorService.enableEnterpriseUser(account);
    }

    /**
     * Disable enterprise user http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/disableEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableEnterpriseUser(@RequestParam String account) {
        return administratorService.disableEnterpriseUser(account);
    }

    /**
     * Delete enterprise user http result.
     *
     * @param account the account
     * @return the http result
     */
    @DeleteMapping("/deleteEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteEnterpriseUser(@RequestParam String account) {
        return administratorService.deleteEnterpriseUser(account);
    }

    /**
     * Query school user http result.
     *
     * @param principal the principal
     * @param status    the status
     * @param current   the current
     * @param size      the size
     * @return the http result
     */
    @GetMapping("/querySchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult querySchoolUser(
            @RequestParam(required = false) String principal,
            @RequestParam(required = false) Character status,
            @RequestParam Integer current,
            @RequestParam Integer size) {
        return administratorService.querySchoolUser(principal, status, current, size);
    }

    /**
     * Create new school user http result.
     *
     * @param account    the account
     * @param schoolName the school name
     * @param principal  the principal
     * @param tel        the tel
     * @return the http result
     */
    @PostMapping("/createNewSchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewSchoolUser(
            @RequestParam String account,
            @RequestParam String schoolName,
            @RequestParam String principal,
            @RequestParam String tel) {
        return administratorService.createNewSchoolUser(account, schoolName, principal, tel);
    }

    /**
     * Reset school user password http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("resetSchoolUserPassword")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult resetSchoolUserPassword(@RequestParam String account) {
        return administratorService.resetSchoolUserPassword(account);
    }

    /**
     * Enable school user http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("enableSchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableSchoolUser(@RequestParam String account) {
        return administratorService.enableSchoolUser(account);
    }

    /**
     * Disable school user http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("disableSchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableSchoolUser(@RequestParam String account) {
        return administratorService.disableSchoolUser(account);
    }

    /**
     * Delete school user http result.
     *
     * @param account the account
     * @return the http result
     */
    @DeleteMapping("deleteSchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteSchoolUser(@RequestParam String account) {
        return administratorService.deleteSchoolUser(account);
    }

    /**
     * Query recruitment info http result.
     *
     * @param companyName    the company name
     * @param departmentName the department name
     * @param jobTitle       the job title
     * @param current        the current
     * @param size           the size
     * @return the http result
     */
    @GetMapping("queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryRecruitmentInfo(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String jobTitle,
            @RequestParam Integer current,
            @RequestParam Integer size) {
        return administratorService.queryRecruitmentInfo(
                companyName, departmentName, jobTitle, current, size);
    }

    /**
     * Audit recruitment info http result.
     *
     * @param enterpriseName the enterprise name
     * @param departmentName the department name
     * @param jobTitle       the job title
     * @param status         the status
     * @param rejectReason   the reject reason
     * @return the http result
     */
    @PatchMapping("auditRecruitmentInfo")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult auditRecruitmentInfo(
            @RequestParam String enterpriseName,
            @RequestParam String departmentName,
            @RequestParam String jobTitle,
            @RequestParam String status,
            @RequestParam(required = false) String rejectReason) {
        if (status.equals("3") && (Objects.isNull(rejectReason) || rejectReason.isEmpty())) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "请填写拒绝理由");
        }
        return administratorService.auditRecruitmentInfo(
                enterpriseName, departmentName, jobTitle, status, rejectReason);
    }

    /**
     * Create announcement http result.
     *
     * @param title    the title
     * @param cover    the cover
     * @param category the category
     * @param content  the content
     * @param data     the data
     * @return the http result
     */
    @PostMapping("createAnnouncement")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createAnnouncement(
            @RequestParam String title,
            @RequestParam(required = false) MultipartFile cover,
            @RequestParam String category,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile data) {
        return administratorService.createAnnouncement(title, cover, category, content, data);
    }

    /**
     * Delete announcement http result.
     *
     * @param id the id
     * @return the http result
     */
    @DeleteMapping("deleteAnnouncement")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteAnnouncement(@RequestParam Integer id) {
        return administratorService.deleteAnnouncement(id);
    }

    /**
     * Query announcement http result.
     *
     * @param title    the title
     * @param category the category
     * @param current  the current
     * @return the http result
     */
    @GetMapping("queryAnnouncement")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryAnnouncement(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam Integer current) {
        return administratorService.queryAnnouncement(title, category, current);
    }

    /**
     * Query announcement cover http result.
     *
     * @param id the id
     * @return the http result
     */
    @GetMapping("/queryAnnouncementCover")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryAnnouncementCover(@RequestParam Integer id) {
        return administratorService.queryAnnouncementCover(id);
    }

    /**
     * Query announcement data http result.
     *
     * @param id the id
     * @return the http result
     */
    @GetMapping("/queryAnnouncementData")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryAnnouncementData(@RequestParam Integer id) {
        return administratorService.queryAnnouncementData(id);
    }

    /**
     * Sets announcement top.
     *
     * @param id the id
     * @return the announcement top
     */
    @PatchMapping("/setAnnouncementTop")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult setAnnouncementTop(@RequestParam Integer id) {
        return administratorService.setAnnouncementTop(id);
    }

    /**
     * Sets announcement down.
     *
     * @param id the id
     * @return the announcement down
     */
    @PatchMapping("/setAnnouncementDown")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult setAnnouncementDown(@RequestParam Integer id) {
        return administratorService.setAnnouncementDown(id);
    }
}
