package com.teamProj.controller;

import com.teamProj.entity.Administrator;
import com.teamProj.entity.Student;
import com.teamProj.service.AdministratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    Administrator administratorLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return administratorService.administratorLogin(account, password);
    }

    @PatchMapping("/resetStudentPassword")
    Student resetStudentPassword(@RequestParam(value = "studentAccount") String account) {
        return administratorService.resetStudentPassword(account);
    }

    @PatchMapping("/disableStudentAccount")
    Student disableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.disableStudentAccount(account);
    }

    @PatchMapping("/enableStudentAccount")
    Student ableStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.enableStudentAccount(account);
    }

    @DeleteMapping("deleteStudentAccount")
    Student deleteStudentAccount(@RequestParam(value = "studentAccount") String account) {
        return administratorService.deleteStudentAccount(account);
    }

    @GetMapping("queryStudent")
    List<Student> queryStudent(@RequestParam(value = "studentName") String name, @RequestParam(value = "studentAccount") String account, @RequestParam(value = "status") int status) {
        return administratorService.queryStudent(name, account, status);
    }
}
