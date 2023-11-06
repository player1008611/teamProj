package com.teamProj.controller;

import com.teamProj.entity.Administrator;
import com.teamProj.service.AdministratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;

    @PostMapping("/login")
    Administrator administratorLogin(@RequestBody Map<String, String> map) {
        return administratorService.administratorLogin(map.get("account"), map.get("password"));
    }
}
