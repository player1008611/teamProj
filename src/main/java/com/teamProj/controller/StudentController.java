package com.teamProj.controller;

import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
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

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentLogout() {
        return studentService.studentLogout();
    }

    @PostMapping("/register")
    HttpResult studentRegister(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return studentService.studentRegister(account, password);
    }

    @PatchMapping("/setPassword")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentSetPassword(@RequestParam(value = "account") String account, @RequestParam(value = "oldPassword") String oldPassword, @RequestParam(value = "password") String password) {
        return studentService.setStudentPassword(account, oldPassword, password);
    }

    @PatchMapping("/setStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult setStudentInfo(@RequestParam(value = "studentAccount") String account, @RequestBody Map<String, Object> map) {
        return studentService.setStudentInfo(account, map);
    }

    @PostMapping("/createResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createResume(@RequestParam(value = "studentAccount",required = false) String account,
                            @RequestParam(value = "image",required = false) MultipartFile imageByte,
                            @RequestParam(value = "selfDescription",required = false) String selfDescription,
                            @RequestParam(value = "careerObjective",required = false) String careerObjective,
                            @RequestParam(value = "educationExperience",required = false) String educationExperience,
                            @RequestParam(value = "internshipExperience",required = false) String InternshipExperience,
                            @RequestParam(value = "projectExperience",required = false) String projectExperience,
                            @RequestParam(value = "certificates",required = false) String certificates,
                            @RequestParam(value = "skills",required = false) String skills,
                            @RequestParam(value = "resumeName",required = false) String resumeName,
                            @RequestParam(value = "attachPDF", required = false) MultipartFile attachPDF

    ) {
        return studentService.createResume(account, imageByte, selfDescription, careerObjective, educationExperience, InternshipExperience, projectExperience, certificates, skills, resumeName, attachPDF);
    }

    @DeleteMapping("/deleteResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteResume(@RequestParam(value = "resumeId") Integer resumeId) {
        return studentService.deleteResume(resumeId);
    }

    @GetMapping("/queryResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryResume() {
        return studentService.queryResume();
    }
    @GetMapping("/queryResumeDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryResumeDetail(@RequestParam(value = "resumeId") Integer resumeId){
        return studentService.queryResumeDetail(resumeId);
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(@RequestParam(value = "queryInfo") String queryInfo, @RequestParam(value = "minSalary") String minSalary,@RequestParam(value = "maxSalary") String maxSalary, @RequestParam(value = "mark") boolean mark) {
        return studentService.queryRecruitmentInfo(queryInfo, minSalary,maxSalary, mark);
    }

    @PostMapping("/markRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult markRecruitmentInfo(@RequestParam(value = "studentAccount") String account, @RequestParam(value = "recruitmentInfoId") Integer recruitmentInfoId) {
        return studentService.markRecruitmentInfo(account, recruitmentInfoId);
    }

    @PostMapping("/createJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createJobApplication(@RequestParam(value = "studentAccount") String account, @RequestParam(value = "recruitmentInfoId") Integer recruitmentInfoId, @RequestParam(value = "resumeId") Integer resumeId) {
        return studentService.createJobApplication(account, recruitmentInfoId, resumeId);
    }

    @DeleteMapping("/deleteJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteJobApplication(@RequestParam(value = "jobApplicationId") Integer jobApplicationId) {
        return studentService.deleteJobApplication(jobApplicationId);
    }

    @GetMapping("/queryJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(@RequestParam(value = "account") String account) {
        return studentService.queryJobApplication(account);
    }

    @GetMapping("/queryJobApplicationDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfoDetail(@RequestParam(value = "studentAccount") Integer applicationId) {
        return studentService.queryJobApplicationDetail(applicationId);
    }


}