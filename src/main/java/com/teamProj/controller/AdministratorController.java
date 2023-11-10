package com.teamProj.controller;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import com.teamProj.util.HttpResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;

    @PostMapping("/login")
    HttpResult<Administrator> administratorLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return administratorService.administratorLogin(account, password);
    }

    @PatchMapping("/resetStudentPassword")
    HttpResult<Student> resetStudentPassword(@RequestParam(value = "studentAccount") String account) {
        return administratorService.resetStudentPassword(account);
    }

    @PatchMapping("/disableStudentAccount")
    HttpResult<Student> disableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.disableStudentAccount(account);
    }

    @PatchMapping("/enableStudentAccount")
    HttpResult<Student> ableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.enableStudentAccount(account);
    }

    @DeleteMapping("deleteStudentAccount")
    HttpResult<Student> deleteStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.deleteStudentAccount(account);
    }

    @GetMapping("queryStudent")
    HttpResult<List<Student>> queryStudent(@RequestParam(value = "studentName", required = false) String name, @RequestParam(value = "studentAccount", required = false) String account, @RequestParam(value = "status") String status) {
        return administratorService.queryStudent(name, account, status);
    }
}
