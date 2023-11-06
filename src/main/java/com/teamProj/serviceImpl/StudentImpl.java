package com.teamProj.serviceImpl;

import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentImpl implements StudentService {
    @Resource
    StudentDao StudentDao;

    @Override
    public Student studentLogin(String account, String password) {
        List<Student> studentList = StudentDao.selectList(null);
        for (Student student : studentList) {
            if (student.getStudentAccount().equals(account)) {
                if (student.getStudentPassword().equals(password)) {
                    return student;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

}
