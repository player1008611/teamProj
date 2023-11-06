package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class EnterpriseUser {
    @TableId(type = IdType.AUTO)
    String userId;
    String enterpriseId;
    String name;
    String age;
    String birthday;
    String gender;
    String graduation_school;

    @Override
    public String toString() {
        return "EnterpriseUser{" +
                "userId='" + userId + '\'' +
                ", enterpriseId='" + enterpriseId + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", birthday='" + birthday + '\'' +
                ", gender='" + gender + '\'' +
                ", graduation_school='" + graduation_school + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGraduation_school() {
        return graduation_school;
    }

    public void setGraduation_school(String graduation_school) {
        this.graduation_school = graduation_school;
    }
}
