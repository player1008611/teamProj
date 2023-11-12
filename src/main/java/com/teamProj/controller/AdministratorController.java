package com.teamProj.controller;

import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
public class AdministratorController {
    @Resource
    AdministratorService administratorService;

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('admin')")
    String hello() {
        return "hello";
    }

    @PostMapping("/login")
    HttpResult administratorLogin(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
        return administratorService.administratorLogin(account, password);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('admin')")
    HttpResult administratorLogout() {
        return administratorService.administratorLogout();
    }
}
