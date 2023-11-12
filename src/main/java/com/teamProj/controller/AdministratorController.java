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

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('admin')")
    String hello() {
        return "hello";
    }

    @PostMapping("/login")
    HttpResult administratorLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return administratorService.administratorLogin(account, password);
    }

    @PostMapping("/logout")
    HttpResult administratorLogout() {
        return administratorService.administratorLogout();
    }

    @PatchMapping("/resetStudentPassword")
    HttpResult resetStudentPassword(@RequestParam(value = "studentAccount") String account) {
        return administratorService.resetStudentPassword(account);
    }

    @PatchMapping("/disableStudentAccount")
    HttpResult disableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.disableStudentAccount(account);
    }

    @PatchMapping("/enableStudentAccount")
    HttpResult ableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.enableStudentAccount(account);
    }

    @DeleteMapping("deleteStudentAccount")
    HttpResult deleteStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.deleteStudentAccount(account);
    }

    @GetMapping("queryStudent")
    HttpResult queryStudent(@RequestParam(value = "studentName", required = false) String name, @RequestParam(value = "studentAccount", required = false) String account, @RequestParam(value = "status") String status) {
        return administratorService.queryStudent(name, account, status);
    }
}
