package com.teamProj.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.itextpdf.layout.element.Image;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.Resume;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.awt.*;
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

    @PostMapping("/createResume")
    //@PreAuthorize("hasAuthority('student')")
    HttpResult createResume(@RequestParam(value = "studentAccount") String account,
                            @RequestParam(value = "image") Image image,
                            @RequestParam(value = "selfDescription") String selfDescription,
                            @RequestParam(value = "careerObjective") String careerObjective,
                            @RequestParam(value = "educationExperience") String educationExperience,
                            @RequestParam(value = "internshipExperience") String InternshipExperience,
                            @RequestParam(value = "projectExperience") String projectExperience,
                            @RequestParam(value = "certificates") String certificates,
                            @RequestParam(value = "skills") String skills
    ) {
        return studentService.createResume(account, image, selfDescription, careerObjective, educationExperience, InternshipExperience, projectExperience, certificates, skills);
    }

    @DeleteMapping("/deleteResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteResume(@RequestParam(value = "studentAccount") String account, @RequestParam(value = "resumeId") Integer resumeId) {
        return studentService.deleteResume(account, resumeId);
    }

    @GetMapping("/queryResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryResume(@RequestParam(value = "studentAccount") String account) {
        return studentService.queryResume(account);
    }

    @PatchMapping("/setStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult setStudentInfo(@RequestParam(value = "studentAccount") String account, @RequestBody Map<String, Object> map) {
        return studentService.setStudentInfo(account, map);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentLogout() {
        return studentService.studentLogout();
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(@RequestParam(value = "enterpriseName") String enterpriseName){
        return studentService.queryRecruitmentInfo(enterpriseName);
    }

    @PostMapping("/markRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult markRecruitmentInfo(@RequestParam(value = "studentAccount") String account, @RequestParam(value = "recruitmentInfoId") Integer recruitmentInfoId){
        return studentService.markRecruitmentInfo(account, recruitmentInfoId);
    }
}