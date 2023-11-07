package com.teamProj.controller;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;

    @PostMapping("/login")
    Administrator administratorLogin(@RequestBody Map<String, String> map) {
        return administratorService.administratorLogin(map.get("account"), map.get("password"));
    }

    @PatchMapping("/resetStudentPassword")
    Student resetStudentPassword(@RequestBody Map<String, String> map) {
        return administratorService.resetStudentPassword(map.get("studentAccount"));
    }

    @PatchMapping("/disableStudentAccount")
    Student disableStudentAccount(@RequestBody Map<String, String> map) {
        return administratorService.disableStudentAccount(map.get("studentAccount"));
    }

    @PatchMapping("/enableStudentAccount")
    Student ableStudentAccount(@RequestBody Map<String, String> map) {
        return administratorService.enableStudentAccount(map.get("studentAccount"));
    }

    @DeleteMapping("deleteStudentAccount")
    Student deleteStudentAccount(@RequestBody Map<String, String> map) {
        return administratorService.deleteStudentAccount(map.get("studentAccount"));
    }

    @GetMapping("queryStudent")
    List<Student> queryStudent(@RequestBody Map<String, String> map) {
        return administratorService.queryStudent(map.get("studentName"),map.get("studentAccount"),map.get("status"));
    }
}
