package com.teamProj.controller;

import com.teamProj.service.SchoolService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/school")
public class SchoolController {
    @Resource
    SchoolService schoolService;

    @PostMapping("/login")
    HttpResult schoolLogin(@RequestParam String account, @RequestParam String password) {
        return schoolService.schoolLogin(account, password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('school')")
    HttpResult schoolLogout() {
        return schoolService.schoolLogout();
    }

    @PatchMapping("/resetPassword")
    @PreAuthorize("hasAuthority('school')")
    HttpResult setSchoolPassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        return schoolService.setSchoolPassword(oldPassword, newPassword);
    }

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

    @PatchMapping("/resetStudentPassword")
    @PreAuthorize("hasAuthority('school')")
    HttpResult resetStudentPassword(@RequestParam String account) {
        return schoolService.resetStudentPassword(account);
    }

    @PatchMapping("/enableStudentAccount")
    @PreAuthorize("hasAuthority('school')")
    HttpResult enableStudentAccount(@RequestParam String account) {
        return schoolService.enableStudentAccount(account);
    }

    @PatchMapping("/disableStudentAccount")
    @PreAuthorize("hasAuthority('school')")
    HttpResult disableStudentAccount(@RequestParam String account) {
        return schoolService.disableStudentAccount(account);
    }

    @DeleteMapping("/deleteStudent")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteStudent(@RequestParam String account) {
        return schoolService.deleteStudentAccount(account);
    }

    @GetMapping("/College/query")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCollege(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return schoolService.queryCollege(name, current, size);
    }

    @PostMapping("/College/create")
    @PreAuthorize("hasAuthority('school')")
    HttpResult createCollege(@RequestParam String name) {
        return schoolService.createCollege(name);
    }

    @DeleteMapping("/College/delete")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteCollege(@RequestParam Integer collegeId) {
        return schoolService.deleteCollege(collegeId);
    }

    @PatchMapping("/College/edit")
    @PreAuthorize("hasAuthority('school')")
    HttpResult editCollege(@RequestParam Integer collegeId, @RequestParam String name) {
        return schoolService.editCollege(collegeId, name);
    }

    @GetMapping("/Major/query")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryMajor(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "collegeId") Integer collegeId) {
        return schoolService.queryMajor(name, current, size, collegeId);
    }

    @PostMapping("/Major/create")
    @PreAuthorize("hasAuthority('school')")
    HttpResult createMajor(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "collegeId") Integer collegeId) {
        return schoolService.createMajor(name, collegeId);
    }

    @DeleteMapping("/Major/delete")
    @PreAuthorize("hasAuthority('school')")
    HttpResult deleteMajor(@RequestParam(value = "majorId") Integer majorId) {
        return schoolService.deleteMajor(majorId);
    }

    @PatchMapping("/Major/edit")
    @PreAuthorize("hasAuthority('school')")
    HttpResult editMajor(
            @RequestParam(value = "majorId") Integer majorId, @RequestParam(value = "name") String name) {
        return schoolService.editMajor(majorId, name);
    }

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

    @GetMapping("/queryCareerFair")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFair(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current") Integer current,
            @RequestParam(value = "size") Integer size) {
        return schoolService.queryCareerFair(name, current, size);
    }

    @GetMapping("/queryCareerFairNum")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFairNum() {
        return schoolService.queryCareerFairNum();
    }

    @GetMapping("/queryCareerFairToday")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryCareerFairToday() {
        return schoolService.queryCareerFairToday();
    }

    @GetMapping("/applicationData")
    @PreAuthorize("hasAuthority('school')")
    HttpResult applicationData() {
        return schoolService.applicationData();
    }
}
