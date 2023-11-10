package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.teamProj.dao.StudentDao;
import com.teamProj.entity.Student;
import com.teamProj.service.StudentService;
import com.teamProj.util.HttpResult;
import com.teamProj.util.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentImpl implements StudentService {
    @Resource
    StudentDao studentDao;

    @Override
    public HttpResult<Student> studentLogin(String account, String password) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_account", account);
        Student loginStudent = studentDao.selectOne(queryWrapper);
        if(loginStudent != null){
            if(loginStudent.getStudentPassword().equals(password)){
                return HttpResult.success(studentDao.selectOne(new QueryWrapper<Student>().select("student_account","name").eq("student_account",account)),"登录成功");
            }
            return HttpResult.success(null,"密码错误");
        }
        return HttpResult.success(null,"账号不存在");
    }

    @Override
    public HttpResult<Student> setStudentPassword(String account, String oldPassword, String password) {
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("student_password",oldPassword).eq("student_account",account).set("student_password",password);
        if (studentDao.update(null, wrapper) > 0) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("student_account", "name").eq("student_account", account);
            return HttpResult.success(studentDao.selectOne(queryWrapper), "修改成功");
        } else {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
    }
}
