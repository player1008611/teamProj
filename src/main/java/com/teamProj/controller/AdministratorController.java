package com.teamProj.controller;

import com.teamProj.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorController {
    @RequestMapping(value = "/addAdmin")
    public String addAdmin(){
        System.out.println("新增管理员");
        return "新增管理员";
    }
}
