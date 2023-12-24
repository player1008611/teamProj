package com.teamProj.controller;

import com.teamProj.service.StudentService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.SQLException;

import static com.teamProj.utils.ResultCodeEnum.SERVER_ERROR;

/**
 * The type Student controller.
 */
@RestController
@RequestMapping("/student")
public class StudentController {
    /**
     * The Student service.
     */
    @Resource
    StudentService studentService;

    /**
     * Student login http result.
     *
     * @param account  the account
     * @param password the password
     * @return the http result
     */
    @PostMapping("/login")
    HttpResult studentLogin(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password) {
        return studentService.studentLogin(account, password);
    }

    /**
     * Student logout http result.
     *
     * @return the http result
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentLogout() {
        return studentService.studentLogout();
    }

    /**
     * Student register http result.
     *
     * @param account     the account
     * @param password    the password
     * @param schoolName  the school name
     * @param name        the name
     * @param phoneNumber the phone number
     * @param collegeName the college name
     * @param majorName   the major name
     * @return the http result
     */
    @PostMapping("/register")
    HttpResult studentRegister(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "schoolName") String schoolName,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "collegeName") String collegeName,
            @RequestParam(value = "majorName") String majorName) {
        return studentService.studentRegister(
                account, password, schoolName, collegeName, majorName, name, phoneNumber);
    }

    /**
     * Query school http result.
     *
     * @param depth     the depth
     * @param queryInfo the query info
     * @return the http result
     */
    @GetMapping("/querySchool/all")
    HttpResult querySchool(
            @RequestParam(value = "depth") Integer depth,
            @RequestParam(value = "queryInfo") String queryInfo) {
        return studentService.querySchool(depth, queryInfo);
    }

    /**
     * Student set password forget http result.
     *
     * @param account  the account
     * @param password the password
     * @return the http result
     */
    @PatchMapping("/setPassword/forget")
    HttpResult studentSetPasswordForget(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password) {
        return studentService.setStudentPasswordForget(account, password);
    }

    /**
     * Student set password http result.
     *
     * @param account     the account
     * @param oldPassword the old password
     * @param password    the password
     * @return the http result
     */
    @PatchMapping("/setPassword")
    @PreAuthorize("hasAuthority('student')")
    HttpResult studentSetPassword(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "password") String password) {
        return studentService.setStudentPassword(account, oldPassword, password);
    }

    /**
     * Sets student info.
     *
     * @param account   the account
     * @param name      the name
     * @param gender    the gender
     * @param wechat    the wechat
     * @param qq        the qq
     * @param collegeId the college id
     * @param majorId   the major id
     * @param address   the address
     * @param age       the age
     * @return the student info
     */
    @PatchMapping("/setStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult setStudentInfo(
            @RequestParam(value = "studentAccount") String account,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "wechat", required = false) String wechat,
            @RequestParam(value = "qq", required = false) String qq,
            @RequestParam(value = "collegeId", required = false) Integer collegeId,
            @RequestParam(value = "majorId", required = false) Integer majorId,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "age", required = false) Integer age) {
        return studentService.setStudentInfo(
                account, name, gender, wechat, qq, collegeId, majorId, address, age);
    }

    /**
     * Query student info http result.
     *
     * @return the http result
     */
    @GetMapping("/queryStudentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryStudentInfo() {
        return studentService.queryStudentInfo();
    }

    /**
     * Query interview info http result.
     *
     * @param queryInfo the query info
     * @return the http result
     */
    @GetMapping("/queryInterviewInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryInterviewInfo(
            @RequestParam(value = "queryInfo", required = false) String queryInfo) {
        return studentService.queryInterviewInfo(queryInfo);
    }

    /**
     * Create resume http result.
     *
     * @param account              the account
     * @param imageFile            the image file
     * @param selfDescription      the self description
     * @param careerObjective      the career objective
     * @param educationExperience  the education experience
     * @param InternshipExperience the internship experience
     * @param projectExperience    the project experience
     * @param certificates         the certificates
     * @param skills               the skills
     * @param resumeName           the resume name
     * @return the http result
     */
    @PostMapping("/createResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createResume(
            @RequestParam(value = "studentAccount")
            String account,
            @RequestParam(value = "image"/*, required = false*/)
            MultipartFile imageFile,
            @RequestParam(value = "selfDescription", required = false)
            String selfDescription,
            @RequestParam(value = "careerObjective", required = false)
            String careerObjective,
            @RequestParam(value = "educationExperience", required = false)
            String educationExperience,
            @RequestParam(value = "internshipExperience", required = false)
            String InternshipExperience,
            @RequestParam(value = "projectExperience", required = false)
            String projectExperience,
            @RequestParam(value = "certificates", required = false) String
                    certificates,
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "resumeName") String resumeName
            //@RequestBody Resume resume
    ) {

         return studentService.createResume(account, imageFile, selfDescription, careerObjective,
         educationExperience, InternshipExperience, projectExperience, certificates, skills,
         resumeName);
        //return studentService.createResume(resume);
    }

    /**
     * Delete resume http result.
     *
     * @param resumeId the resume id
     * @return the http result
     */
    @DeleteMapping("/deleteResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteResume(@RequestParam(value = "resumeId") Integer resumeId) {
        return studentService.deleteResume(resumeId);
    }

    /**
     * Query resume http result.
     *
     * @return the http result
     */
    @GetMapping("/queryResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryResume() {
        return studentService.queryResume();
    }

    /**
     * Query resume detail http result.
     *
     * @param resumeId the resume id
     * @return the http result
     * @throws SQLException the sql exception
     */
    @GetMapping("/queryResumeDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryResumeDetail(@RequestParam(value = "resumeId") Integer resumeId)
            throws SQLException {
        return studentService.queryResumeDetail(resumeId);
    }

    /**
     * Edit resume http result.
     *
     * @param resumeId             the resume id
     * @param selfDescription      the self description
     * @param careerObjective      the career objective
     * @param educationExperience  the education experience
     * @param InternshipExperience the internship experience
     * @param projectExperience    the project experience
     * @param certificates         the certificates
     * @param skills               the skills
     * @param resumeName           the resume name
     * @return the http result
     */
    @PostMapping("/editResume")
    @PreAuthorize("hasAuthority('student')")
    HttpResult editResume(
            @RequestParam(value = "resumeId") String resumeId,
            @RequestParam(value = "selfDescription", required = false) String selfDescription,
            @RequestParam(value = "careerObjective", required = false) String careerObjective,
            @RequestParam(value = "educationExperience", required = false) String educationExperience,
            @RequestParam(value = "internshipExperience", required = false) String InternshipExperience,
            @RequestParam(value = "projectExperience", required = false) String projectExperience,
            @RequestParam(value = "certificates", required = false) String certificates,
            @RequestParam(value = "skills", required = false) String skills,
            @RequestParam(value = "resumeName") String resumeName) {
        return studentService.editResume(
                resumeId,
                selfDescription,
                careerObjective,
                educationExperience,
                InternshipExperience,
                projectExperience,
                certificates,
                skills,
                resumeName);
    }

    /**
     * Query recruitment info http result.
     *
     * @param account   the account
     * @param queryInfo the query info
     * @param minSalary the min salary
     * @param maxSalary the max salary
     * @param mark      the mark
     * @return the http result
     */
    @GetMapping("/queryRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryRecruitmentInfo(
            @RequestParam(value = "account") String account,
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

    /**
     * Mark recruitment info http result.
     *
     * @param account           the account
     * @param recruitmentInfoId the recruitment info id
     * @return the http result
     */
    @PostMapping("/markRecruitmentInfo")
    @PreAuthorize("hasAuthority('student')")
    HttpResult markRecruitmentInfo(
            @RequestParam(value = "studentAccount") String account,
            @RequestParam(value = "recruitmentInfoId") Integer recruitmentInfoId) {
        return studentService.markRecruitmentInfo(account, recruitmentInfoId);
    }

    /**
     * Create job application http result.
     *
     * @param account           the account
     * @param recruitmentInfoId the recruitment info id
     * @param resumeId          the resume id
     * @return the http result
     */
    @PostMapping("/createJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult createJobApplication(
            @RequestParam(value = "studentAccount") String account,
            @RequestParam(value = "recruitmentInfoId") Integer recruitmentInfoId,
            @RequestParam(value = "resumeId") Integer resumeId) {
        return studentService.createJobApplication(account, recruitmentInfoId, resumeId);
    }

    /**
     * Delete job application http result.
     *
     * @param jobApplicationId the job application id
     * @return the http result
     */
    @DeleteMapping("/deleteJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteJobApplication(
            @RequestParam(value = "jobApplicationId") Integer jobApplicationId) {
        return studentService.deleteJobApplication(jobApplicationId);
    }

    /**
     * Query job application http result.
     *
     * @param account the account
     * @return the http result
     */
    @GetMapping("/queryJobApplication")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryJobApplication(@RequestParam(value = "account") String account) {
        return studentService.queryJobApplication(account);
    }

    /**
     * Query job application detail http result.
     *
     * @param applicationId the application id
     * @return the http result
     */
    @GetMapping("/queryJobApplicationDetail")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryJobApplicationDetail(
            @RequestParam(value = "applicationId") Integer applicationId) {
        return studentService.queryJobApplicationDetail(applicationId);
    }

    /**
     * Verification email http result.
     *
     * @param email the email
     * @return the http result
     */
    @PostMapping("/verification/email")
    HttpResult verificationEmail(@RequestParam(value = "email") String email) {
        return studentService.verificationEmail(email);
    }

    /**
     * Verification phone number http result.
     *
     * @param phoneNumber the phone number
     * @return the http result
     */
    @PostMapping("/verification/phone")
    HttpResult verificationPhoneNumber(@RequestParam(value = "phoneNumber") String phoneNumber) {
        try {
            return studentService.verificationPhone(phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verification phone number check http result.
     *
     * @param messageId the message id
     * @param code      the code
     * @return the http result
     */
    @PostMapping("/verification/phone/check")
    HttpResult verificationPhoneNumberCheck(
            @RequestParam(value = "messageId") String messageId,
            @RequestParam(value = "code") String code) {
        try {
            return studentService.verificationPhoneCheck(messageId, code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Homepage http result.
     *
     * @return the http result
     */
    @GetMapping("/homepage")
    @PreAuthorize("hasAuthority('student')")
    HttpResult homepage() {
        return studentService.homepage();
    }

    /**
     * Gets recommendation.
     *
     * @param page the page
     * @return the recommendation
     */
    @GetMapping("/getRecommendation")
    @PreAuthorize("hasAuthority('student')")
    HttpResult getRecommendation(@RequestParam(value = "page", required = false) Integer page) {
        return studentService.getRecommendation(page);
    }

    /**
     * Query fair http result.
     *
     * @return the http result
     */
    @GetMapping("/queryFair")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryFair() {
        return studentService.queryFair();
    }

    /**
     * Query message list http result.
     *
     * @return the http result
     */
    @GetMapping("/message/queryList")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryMessageList() {
        return studentService.queryMessageList();
    }

    /**
     * Query message http result.
     *
     * @param messageId the message id
     * @param queryInfo the query info
     * @return the http result
     */
    @GetMapping("/message/query")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryMessage(
            @RequestParam(value = "messageId", required = false) Integer messageId,
            @RequestParam(value = "queryInfo", required = false) String queryInfo) {
        return studentService.queryMessage(messageId, queryInfo);
    }

    /**
     * Delete all message http result.
     *
     * @return the http result
     */
    @DeleteMapping("/message/deleteAll")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteAllMessage() {
        return studentService.deleteAllMessage();
    }

    /**
     * Delete message http result.
     *
     * @param messageId the message id
     * @return the http result
     */
    @DeleteMapping("/message/delete")
    @PreAuthorize("hasAuthority('student')")
    HttpResult deleteMessage(@RequestParam(value = "messageId") Integer messageId) {
        return studentService.deleteMessage(messageId);
    }

    /**
     * Read all message http result.
     *
     * @return the http result
     */
    @PatchMapping("/message/readAll")
    @PreAuthorize("hasAuthority('student')")
    HttpResult readAllMessage() {
        return studentService.hasReadAllMessage();
    }

    /**
     * Read message http result.
     *
     * @param messageId the message id
     * @return the http result
     */
    @PatchMapping("/message/read")
    @PreAuthorize("hasAuthority('student')")
    HttpResult readMessage(@RequestParam(value = "messageId") Integer messageId) {
        return studentService.hasReadMessage(messageId);
    }

    /**
     * Edit phone number http result.
     *
     * @param phoneNumber the phone number
     * @return the http result
     */
    @PatchMapping("/editPhoneNumber")
    @PreAuthorize("hasAuthority('student')")
    HttpResult editPhoneNumber(@RequestParam(value = "phoneNumber") String phoneNumber) {
        return studentService.editPhoneNumber(phoneNumber);
    }

    /**
     * Query college major http result.
     *
     * @param schoolName the school name
     * @return the http result
     */
    @GetMapping("/queryCollegeMajor")
    @PreAuthorize("hasAuthority('student')")
    HttpResult queryCollegeMajor(@RequestParam(value = "schoolName") String schoolName) {
        return studentService.queryCollegeMajor(schoolName);
    }
}
