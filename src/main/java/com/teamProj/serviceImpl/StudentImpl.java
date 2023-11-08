package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentImpl implements StudentService {
    @Resource
    StudentDao studentDao;

    @Override
    public Student studentLogin(String account, String password) {
        List<Student> studentList = studentDao.selectList(null);
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

    @Override
    public Student setStudentPassword(String account, String oldPassword, String password) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("studentpassword",oldPassword).eq("studentaccount",account).set("studentpassword",password);
        if (studentDao.update(null, wrapper) > 0) {
            return studentDao.selectOne(wrapper);
        } else {
            return null;
        }
    }
}
