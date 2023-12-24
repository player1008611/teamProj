package com.teamProj.controller;

import com.teamProj.service.SchoolService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * The type School controller.
 */
@RestController
@RequestMapping("/school")
public class SchoolController {
    /**
     * The School service.
     */
    @Resource
    SchoolService schoolService;

    /**
     * School login http result.
     *
     * @param account  the account
     * @param password the password
     * @return the http result
     */
    @PostMapping("/login")
    HttpResult schoolLogin(@RequestParam String account, @RequestParam String password) {
        return schoolService.schoolLogin(account, password);
    }

    /**
     * School logout http result.
     *
     * @return the http result
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('school')")
    HttpResult schoolLogout() {
        return schoolService.schoolLogout();
    }

    /**
     * Sets school password.
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return the school password
     */
    @PatchMapping("/resetPassword")
    @PreAuthorize("hasAuthority('school')")
    HttpResult setSchoolPassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        return schoolService.setSchoolPassword(oldPassword, newPassword);
    }

    /**
     * Query student http result.
     *
     * @param name    the name
     * @param majorId the major id
     * @param status  the status
     * @param current the current
     * @param size    the size
     * @return the http result
     */
    @GetMapping("/queryStudent")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryStudent(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "majorId", required = false) Integer majorId,
            @RequestParam(value = "status", required = false) Character status,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return schoolService.queryStudent(name, majorId, status, current, size);
    }

    /**
     * Reset student password http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/resetStudentPassword")
    @PreAuthorize("hasAuthority('school')")
    HttpResult resetStudentPassword(@RequestParam String account) {
        return schoolService.resetStudentPassword(account);
    }

    /**
     * Enable student account http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/enableStudentAccount")
    @PreAuthorize("hasAuthority('school')")
    HttpResult enableStudentAccount(@RequestParam String account) {
        return schoolService.enableStudentAccount(account);
    }

    /**
     * Disable student account http result.
     *
     * @param account the account
     * @return the http result
     */
    @PatchMapping("/disableStudentAccount")
    @PreAuthorize("hasAuthority('school')")
    HttpResult disableStudentAccount(@RequestParam String account) {
        return schoolService.disableStudentAccount(account);
    }

    /**
     * Delete student http result.
     *
     * @param account the account
     * @return the http result
     */
    @DeleteMapping("/deleteStudent")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteStudent(@RequestParam String account) {
        return schoolService.deleteStudentAccount(account);
    }

    /**
     * Query college http result.
     *
     * @param name    the name
     * @param current the current
     * @param size    the size
     * @return the http result
     */
    @GetMapping("/College/query")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCollege(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return schoolService.queryCollege(name, current, size);
    }

    /**
     * Create college http result.
     *
     * @param name the name
     * @return the http result
     */
    @PostMapping("/College/create")
    @PreAuthorize("hasAuthority('school')")
    HttpResult createCollege(@RequestParam String name) {
        return schoolService.createCollege(name);
    }

    /**
     * Delete college http result.
     *
     * @param collegeId the college id
     * @return the http result
     */
    @DeleteMapping("/College/delete")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteCollege(@RequestParam Integer collegeId) {
        return schoolService.deleteCollege(collegeId);
    }

    /**
     * Edit college http result.
     *
     * @param collegeId the college id
     * @param name      the name
     * @return the http result
     */
    @PatchMapping("/College/edit")
    @PreAuthorize("hasAuthority('school')")
    HttpResult editCollege(@RequestParam Integer collegeId, @RequestParam String name) {
        return schoolService.editCollege(collegeId, name);
    }

    /**
     * Query major http result.
     *
     * @param name      the name
     * @param current   the current
     * @param size      the size
     * @param collegeId the college id
     * @return the http result
     */
    @GetMapping("/Major/query")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryMajor(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "collegeId") Integer collegeId) {
        return schoolService.queryMajor(name, current, size, collegeId);
    }

    /**
     * Create major http result.
     *
     * @param name      the name
     * @param collegeId the college id
     * @return the http result
     */
    @PostMapping("/Major/create")
    @PreAuthorize("hasAuthority('school')")
    HttpResult createMajor(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "collegeId") Integer collegeId) {
        return schoolService.createMajor(name, collegeId);
    }

    /**
     * Delete major http result.
     *
     * @param majorId the major id
     * @return the http result
     */
    @DeleteMapping("/Major/delete")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteMajor(@RequestParam(value = "majorId") Integer majorId) {
        return schoolService.deleteMajor(majorId);
    }

    /**
     * Edit major http result.
     *
     * @param majorId the major id
     * @param name    the name
     * @return the http result
     */
    @PatchMapping("/Major/edit")
    @PreAuthorize("hasAuthority('school')")
    HttpResult editMajor(
            @RequestParam(value = "majorId") Integer majorId, @RequestParam(value = "name") String name) {
        return schoolService.editMajor(majorId, name);
    }

    /**
     * Audit career fair http result.
     *
     * @param careerFairId the career fair id
     * @param status       the status
     * @param reason       the reason
     * @return the http result
     */
    @PatchMapping("/auditCareerFair")
    @PreAuthorize("hasAuthority('school')")
    HttpResult auditCareerFair(
            @RequestParam(value = "careerFairId") Integer careerFairId,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "reason", required = false) String reason) {
        if (status.equals("2") && reason == null) {
            return HttpResult.failure(ResultCodeEnum.SERVER_ERROR, "请填写拒绝理由");
        }
        return schoolService.auditCareerFair(careerFairId, status, reason);
    }

    /**
     * Query career fair http result.
     *
     * @param name    the name
     * @param current the current
     * @param size    the size
     * @return the http result
     */
    @GetMapping("/queryCareerFair")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFair(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return schoolService.queryCareerFair(name, current, size);
    }

    /**
     * Query career fair num http result.
     *
     * @return the http result
     */
    @GetMapping("/queryCareerFairNum")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFairNum() {
        return schoolService.queryCareerFairNum();
    }

    /**
     * Query career fair today http result.
     *
     * @return the http result
     */
    @GetMapping("/queryCareerFairToday")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFairToday() {
        return schoolService.queryCareerFairToday();
    }

    /**
     * Application data http result.
     *
     * @return the http result
     */
    @GetMapping("/applicationData")
    @PreAuthorize("hasAuthority('school')")
    HttpResult applicationData() {
        return schoolService.applicationData();
    }
}
