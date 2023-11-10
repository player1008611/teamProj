package com.teamProj.controller;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Resume;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import com.teamProj.util.HttpResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource
    StudentService studentService;

    @PostMapping("/login")
    HttpResult<Student> studentLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return studentService.studentLogin(account, password);
    }

    @PatchMapping("/setPassword")
    HttpResult<Student> studentSetPassword(@RequestParam(value = "account") String account, @RequestParam(value = "oldPassword") String oldPassword, @RequestParam(value = "password") String password) {
        return studentService.setStudentPassword(account, oldPassword, password);
    }


}