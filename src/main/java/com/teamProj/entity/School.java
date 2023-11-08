package com.teamProj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class School {
    @TableId(type = IdType.AUTO)
    int schoolId;
    String schoolAccount;
    String schoolPassword;
    String schoolName;
    String tel;
    String principal;

    @Override
    public String toString() {
        return "School{" +
                "schoolId=" + schoolId +
                ", schoolAccount='" + schoolAccount + '\'' +
                ", schoolPassword='" + schoolPassword + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", tel='" + tel + '\'' +
                ", principal='" + principal + '\'' +
                '}';
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolAccount() {
        return schoolAccount;
    }

    public void setSchoolAccount(String schoolAccount) {
        this.schoolAccount = schoolAccount;
    }

    public String getSchoolPassword() {
        return schoolPassword;
    }

    public void setSchoolPassword(String schoolPassword) {
        this.schoolPassword = schoolPassword;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
