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

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('admin')")
    String hello() {
        return "hello";
    }

    @PostMapping("/login")
    HttpResult administratorLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
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

    @PatchMapping("enableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult enableStudentAccount(@RequestParam String account) {
        return administratorService.enableStudentAccount(account);
    }

    @PatchMapping("disableStudentAccount")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult disableStudentAccount(@RequestParam String account) {
        return administratorService.disableStudentAccount(account);
    }

    @DeleteMapping("deleteStudent")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult deleteStudent(@RequestParam String account) {
        return administratorService.deleteStudentAccount(account);
    }
}
