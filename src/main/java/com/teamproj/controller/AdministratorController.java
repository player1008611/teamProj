package com.teamproj.controller;

import com.teamproj.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @RequestMapping(value = "/addAdmin")
    public String addAdmin(){
        System.out.println("新增管理员");
        return administratorService.addAdmin();
    }
}
