package com.teamProj;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.AdministratorDao;
import com.teamProj.dao.AnnouncementDao;
import com.teamProj.dao.CareerFairDao;
import com.teamProj.dao.EnterpriseDao;
import com.teamProj.dao.EnterpriseUserDao;
import com.teamProj.dao.FairParticipantDao;
import com.teamProj.dao.InterviewInfoDao;
import com.teamProj.dao.JobApplicationDao;
import com.teamProj.dao.MarkedRecruitmentInfoDao;
import com.teamProj.dao.RecruitmentInfoDao;
import com.teamProj.dao.ResumeDao;
import com.teamProj.dao.SchoolDao;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Administrator;
import com.teamProj.entity.Announcement;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.Enterprise;
import com.teamProj.entity.EnterpriseUser;
import com.teamProj.entity.FairParticipant;
import com.teamProj.entity.InterviewInfo;
import com.teamProj.entity.JobApplication;
import com.teamProj.entity.MarkedRecruitmentInfo;
import com.teamProj.entity.RecruitmentInfo;
import com.teamProj.entity.Resume;
import com.teamProj.entity.School;
import com.teamProj.entity.Student;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class TeamProjApplicationTests {
  @Autowired DataSource dataSource;

  @Autowired AdministratorDao administratorDao;

  @Autowired AnnouncementDao announcementDao;

  @Autowired CareerFairDao careerFairDao;

  @Autowired EnterpriseDao enterpriseDao;

  @Autowired EnterpriseUserDao enterpriseUserDao;

  @Autowired FairParticipantDao fairParticipantDao;

  @Autowired InterviewInfoDao interviewInfoDao;

  @Autowired JobApplicationDao jobApplicationDao;

  @Autowired MarkedRecruitmentInfoDao markedRecruitmentInfoDao;

  @Autowired RecruitmentInfoDao recruitmentInfoDao;

  @Autowired ResumeDao resumeDao;

  @Autowired SchoolDao schoolDao;

  @Autowired StudentDao studentDao;

  @Test
  void sqlLinkTest() throws SQLException {
    System.out.println(dataSource.getConnection());
  }

  @Test
  void getAllAdministrator() {
    List<Administrator> administratorList = administratorDao.selectList(null);
    System.out.println(administratorList);
  }

  @Test
  void getAllCareerFair() {
    List<CareerFair> careerFairList = careerFairDao.selectList(null);
    System.out.println(careerFairList);
  }

  @Test
  void getAllEnterprise() {
    List<Enterprise> enterpriseList = enterpriseDao.selectList(null);
    System.out.println(enterpriseList);
  }

  @Test
  void getAllEnterpriseUser() {
    List<EnterpriseUser> enterpriseUserList = enterpriseUserDao.selectList(null);
    System.out.println(enterpriseUserList);
  }

  @Test
  void getAllFairParticipant() {
    List<FairParticipant> fairParticipantList = fairParticipantDao.selectList(null);
    System.out.println(fairParticipantList);
  }

  @Test
  void getAllInterviewInfo() {
    List<InterviewInfo> interviewInfoList = interviewInfoDao.selectList(null);
    System.out.println(interviewInfoList);
  }

  @Test
  void getAllJobApplication() {
    List<JobApplication> jobApplicationList = jobApplicationDao.selectList(null);
    System.out.println(jobApplicationList);
  }

  @Test
  void getAllMarkedRecruitmentInfo() {
    List<MarkedRecruitmentInfo> markedRecruitmentInfoList =
        markedRecruitmentInfoDao.selectList(null);
    System.out.println(markedRecruitmentInfoList);
  }

  @Test
  void getAllRecruitmentInfo() {
    List<RecruitmentInfo> recruitmentInfoList = recruitmentInfoDao.selectList(null);
    System.out.println(recruitmentInfoList);
  }

  @Test
  void getAllResume() {
    List<Resume> resumeList = resumeDao.selectList(null);
    System.out.println(resumeList);
  }

  @Test
  void getAllSchool() {
    List<School> schoolList = schoolDao.selectList(null);
    System.out.println(schoolList);
  }

  @Test
  void getAllStudent() {
    List<Student> studentList = studentDao.selectList(null);
    System.out.println(studentList);
  }

  @Test
  void getAllAnnouncement() {
    List<Announcement> announcementList = announcementDao.selectList(null);
    System.out.println(announcementList);
  }

  @Test
  void TestBCryptPasswordEncoder() {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    System.out.println(passwordEncoder.encode("123456"));
  }

  @Test
  void TestFile() throws IOException {
    QueryWrapper<Announcement> announcementQueryWrapper = new QueryWrapper<>();
    announcementQueryWrapper.eq("announcement_id", 53);
    byte[] bytes = announcementDao.selectOne(announcementQueryWrapper).getData();
    for (int i = 0; i < bytes.length; i++) {
      System.out.print(bytes[i]);
      System.out.print(',');
    }
    System.out.println(bytes.length);
    //        File file = new File("./test.rar");
    //        FileOutputStream fos = new FileOutputStream(file);
    //        fos.write(bytes);
  }
}
