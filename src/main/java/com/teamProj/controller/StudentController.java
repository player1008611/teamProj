package com.teamProj.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.Resume;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource
    StudentService studentService;

    @PostMapping("/login")
    HttpResult studentLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return studentService.studentLogin(account, password);
    }

    @PatchMapping("/setPassword")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentSetPassword(@RequestParam(value = "account") String account, @RequestParam(value = "oldPassword") String oldPassword, @RequestParam(value = "password") String password) {
        return studentService.setStudentPassword(account, oldPassword, password);
    }

    @PatchMapping("/createResume")
    HttpResult createResume(@RequestParam(value = "studentAccount") String account, @RequestBody Map<String, Object> map) {
        return studentService.createResume(account, map);
    }

    @PatchMapping("/setStudentInfo")
    HttpResult setStudentInfo(@RequestParam(value = "studentAccount") String account, @RequestBody Map<String, Object> map) {
        return studentService.setStudentInfo(account, map);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentLogout() {
        return studentService.studentLogout();
    }
}