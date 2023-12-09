package com.teamProj.controller;

import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.teamProj.utils.ResultCodeEnum.SERVER_ERROR;

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
    HttpResult studentRegister(@RequestParam(value = "account") String account,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "schoolName") String schoolName,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "phoneNumber") String phoneNumber
    ) {
        return studentService.studentRegister(account, password, schoolName, name, phoneNumber);
    }

    @GetMapping("/querySchool/all")
    HttpResult querySchool() {
        return studentService.querySchool();
    }

    @PatchMapping("/setPassword")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentSetPassword(@RequestParam(value = "account") String account, @RequestParam(value = "oldPassword") String oldPassword, @RequestParam(value = "password") String password) {
        return studentService.setStudentPassword(account, oldPassword, password);
    }

    @PatchMapping("/setStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult setStudentInfo(@RequestParam(value = "studentAccount") String account, @RequestParam(value = "name") String name,
                              @RequestParam(value = "gender" , required = false)String gender,
                              @RequestParam(value = "wechat" , required = false) String wechat, @RequestParam(value = "qq", required = false) String qq,
                              @RequestParam(value = "collegeId", required = false) Integer collegeId, @RequestParam(value = "majorId", required = false) Integer majorId,
                              @RequestParam(value = "address", required = false) String address, @RequestParam(value = "age", required = false) Integer age) {
        return studentService.setStudentInfo(account,  name,gender, wechat,qq, collegeId,  majorId, address, age);
    }

    @GetMapping("/queryStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryStudentInfo() {
        return studentService.queryStudentInfo();
    }

    @GetMapping("/queryInterviewInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryInterviewInfo(@RequestParam(value = "queryInfo",required = false) String queryInfo) {
        return studentService.queryInterviewInfo(queryInfo);
    }

    @PostMapping("/createResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createResume(@RequestParam(value = "studentAccount") String account,
                            @RequestParam(value = "image"/*, required = false*/) MultipartFile imageFile,
                            @RequestParam(value = "selfDescription", required = false) String selfDescription,
                            @RequestParam(value = "careerObjective", required = false) String careerObjective,
                            @RequestParam(value = "educationExperience", required = false) String educationExperience,
                            @RequestParam(value = "internshipExperience", required = false) String InternshipExperience,
                            @RequestParam(value = "projectExperience", required = false) String projectExperience,
                            @RequestParam(value = "certificates", required = false) String certificates,
                            @RequestParam(value = "skills", required = false) String skills,
                            @RequestParam(value = "resumeName") String resumeName

    ) {
        return studentService.createResume(account, imageFile, selfDescription, careerObjective, educationExperience, InternshipExperience, projectExperience, certificates, skills, resumeName);
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
    HttpResult queryResumeDetail(@RequestParam(value = "resumeId") Integer resumeId) throws SQLException {
        return studentService.queryResumeDetail(resumeId);
    }

    @PostMapping("/editResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult editResume(@RequestParam(value = "resumeId") String resumeId,
                          @RequestParam(value = "selfDescription", required = false) String selfDescription,
                          @RequestParam(value = "careerObjective", required = false) String careerObjective,
                          @RequestParam(value = "educationExperience", required = false) String educationExperience,
                          @RequestParam(value = "internshipExperience", required = false) String InternshipExperience,
                          @RequestParam(value = "projectExperience", required = false) String projectExperience,
                          @RequestParam(value = "certificates", required = false) String certificates,
                          @RequestParam(value = "skills", required = false) String skills,
                          @RequestParam(value = "resumeName") String resumeName) {
        return studentService.editResume(resumeId, selfDescription, careerObjective, educationExperience, InternshipExperience, projectExperience, certificates, skills, resumeName);
    }

    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(@RequestParam(value = "account") String account,
                                    @RequestParam(value = "queryInfo", required = false) String queryInfo,
                                    @RequestParam(value = "minSalary", required = false) String minSalary,
                                    @RequestParam(value = "maxSalary", required = false) String maxSalary,
                                    @RequestParam(value = "mark") String mark) {
        if (mark.equalsIgnoreCase("T") || mark.equalsIgnoreCase("True")) {
            return studentService.queryRecruitmentInfo(account, queryInfo, minSalary, maxSalary, true);
        } else if (mark.equalsIgnoreCase("F") || mark.equalsIgnoreCase("False")) {
            return studentService.queryRecruitmentInfo(account, queryInfo, minSalary, maxSalary, false);
        } else {
            return HttpResult.failure(SERVER_ERROR);
        }
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
    HttpResult queryJobApplication(@RequestParam(value = "account") String account) {
        return studentService.queryJobApplication(account);
    }

    @GetMapping("/queryJobApplicationDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryJobApplicationDetail(@RequestParam(value = "applicationId") Integer applicationId) {
        return studentService.queryJobApplicationDetail(applicationId);
    }

    @PostMapping("/verification")
    HttpResult verification(@RequestParam(value = "email") String email) {
        return studentService.verification(email);
    }

    @GetMapping("/homepage")
    @PreAuthorize("hasAuthority('student')")
    HttpResult homepage() {
        return studentService.homepage();
    }

    @GetMapping("/getRecommendation")
    @PreAuthorize("hasAuthority('student')")
    HttpResult getRecommendation() {
        return studentService.getRecommendation();
    }

    @GetMapping("/queryFair")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryFair(){
        return studentService.queryFair();
    }

    @GetMapping("/message/queryList")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryMessageList(){
        return studentService.queryMessageList();
    }

    @GetMapping("/message/query")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryMessage(@RequestParam(value = "messageId") Integer messageId){
        return studentService.queryMessage(messageId);
    }

    @DeleteMapping("/message/deleteAll")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteAllMessage(){
        return studentService.deleteAllMessage();
    }

    @DeleteMapping("/message/delete")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteMessage(@RequestParam(value = "messageId") Integer messageId){
        return studentService.deleteMessage(messageId);
    }

    @PatchMapping("/message/readAll")
    @PreAuthorize("hasAuthority('student')")
    HttpResult readAllMessage(){
        return studentService.hasReadAllMessage();
    }

    @PatchMapping("/message/read")
    @PreAuthorize("hasAuthority('student')")
    HttpResult readMessage(@RequestParam(value = "messageId") Integer messageId){
        return studentService.hasReadMessage(messageId);
    }
}