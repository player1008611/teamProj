package com.teamProj.controller;

import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;

    @PostMapping("/login")
    HttpResult administratorLogin(@RequestParam String account, @RequestParam String password) {
        return administratorService.administratorLogin(account, password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult administratorLogout() {
        return administratorService.administratorLogout();
    }

    @GetMapping("/queryStudent")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryStudent(@RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "schoolName", required = false) String schoolName
            , @RequestParam(value = "status", required = false) Character status
            , @RequestParam(value = "current") Integer current
            , @RequestParam(value = "size") Integer size) {
        return administratorService.queryStudent(name, schoolName, status, current, size);
    }

    @PatchMapping("/resetStudentPassword")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult resetStudentPassword(@RequestParam String account) {
        return administratorService.resetStudentPassword(account);
    }

    @PatchMapping("/enableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableStudentAccount(@RequestParam String account) {
        return administratorService.enableStudentAccount(account);
    }

    @PatchMapping("/disableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableStudentAccount(@RequestParam String account) {
        return administratorService.disableStudentAccount(account);
    }

    @DeleteMapping("/deleteStudent")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteStudent(@RequestParam String account) {
        return administratorService.deleteStudentAccount(account);
    }

    @GetMapping("/queryEnterprise")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryEnterprise(@RequestParam(required = false) String name
            , @RequestParam Integer current
            , @RequestParam Integer size) {
        return administratorService.queryEnterprise(name, current, size);
    }

    @GetMapping("/queryEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult queryEnterpriseUser(@RequestParam String enterpriseName
            , @RequestParam(required = false) String userName
            , @RequestParam Integer current
            , @RequestParam Integer size) {
        return administratorService.queryEnterpriseUser(enterpriseName, userName, current, size);
    }

    @PostMapping("/createNewEnterprise")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewEnterprise(@RequestParam String name, @RequestParam String url) {
        return administratorService.createNewEnterprise(name, url);
    }

    @PostMapping("/createNewEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewEnterpriseUser(@RequestParam String account
            , @RequestParam String enterpriseName
            , @RequestParam String name
            , @RequestParam String tel) {
        return administratorService.createNewEnterpriseUser(account, enterpriseName, name, tel);
    }

    @PatchMapping("resetEnterpriseUserPassword")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult resetEnterpriseUserPassword(@RequestParam String account) {
        return administratorService.resetEnterpriseUserPassword(account);
    }

    @PatchMapping("/enableEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableEnterpriseUser(@RequestParam String account) {
        return administratorService.enableEnterpriseUser(account);
    }

    @PatchMapping("/disableEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableEnterpriseUser(@RequestParam String account) {
        return administratorService.disableEnterpriseUser(account);
    }

    @DeleteMapping("/deleteEnterpriseUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteEnterpriseUser(@RequestParam String account) {
        return administratorService.deleteEnterpriseUser(account);
    }

    @GetMapping("/querySchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult querySchoolUser(@RequestParam(required = false) String principal
            , @RequestParam(required = false) Character status
            , @RequestParam Integer current
            , @RequestParam Integer size) {
        return administratorService.querySchoolUser(principal, status, current, size);
    }

    @PostMapping("/createNewSchoolUser")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult createNewSchoolUser(@RequestParam String account
            , @RequestParam String schoolName
            , @RequestParam String principal
            , @RequestParam String tel) {
        return administratorService.createNewSchoolUser(account, schoolName, principal, tel);
    }
}
