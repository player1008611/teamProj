package com.teamProj.controller;

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

    @PostMapping("/createResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createResume(@RequestParam(value = "studentAccount") String account,
                            @RequestParam(value = "image") byte[] imageByte,
                            @RequestParam(value = "selfDescription") String selfDescription,
                            @RequestParam(value = "careerObjective") String careerObjective,
                            @RequestParam(value = "educationExperience") String educationExperience,
                            @RequestParam(value = "internshipExperience") String InternshipExperience,
                            @RequestParam(value = "projectExperience") String projectExperience,
                            @RequestParam(value = "certificates") String certificates,
                            @RequestParam(value = "skills") String skills,
                            @RequestParam(value = "resumeName") String resumeName,
                            @RequestParam(value = "attachPDF") byte[] attachPDF

    ) {
        return studentService.createResume(account, imageByte, selfDescription, careerObjective, educationExperience, InternshipExperience, projectExperience, certificates, skills, resumeName, attachPDF);
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
    HttpResult queryRecruitmentInfo(@RequestParam(value = "queryInfo") String queryInfo, @RequestParam(value = "salaryRange") String salaryRange, @RequestParam(value = "mark") boolean mark) {
        return studentService.queryRecruitmentInfo(queryInfo, salaryRange, mark);
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

    @PostMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(@RequestParam(value = "account") String account) {
        return studentService.queryJobApplication(account);
    }

    @PostMapping("/queryRecruitmentInfoDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfoDetail(@RequestParam(value = "recruitmentId") Integer recruitmentId) {
        return studentService.queryJobApplicationDetail(recruitmentId);
    }
}