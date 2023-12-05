package com.teamProj.controller;

import com.teamProj.service.SchoolService;
import com.teamProj.utils.HttpResult;
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
        return schoolService.schoolLogin(account,password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('school')")
    HttpResult schoolLogout() {
        return schoolService.schoolLogout();
    }

    @GetMapping("/queryStudent")
    @PreAuthorize("hasAuthority('school')")
    HttpResult queryStudent(@RequestParam(value = "name", required = false) String name
            , @RequestParam(value = "status", required = false) Character status
            , @RequestParam(value = "current") Integer current
            , @RequestParam(value = "size") Integer size) {
        return schoolService.queryStudent(name, status, current, size);
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

}
