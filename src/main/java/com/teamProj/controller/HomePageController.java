package com.teamProj.controller;

import com.teamProj.service.HomePageService;
import com.teamProj.utils.HttpResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/homePage")
public class HomePageController {
    @Resource
    HomePageService homePageService;

    @GetMapping("/queryNews")
    HttpResult queryNews() {
        return homePageService.queryNews();
    }

    @GetMapping("/queryAnnouncement")
    HttpResult queryAnnouncement() {
        return homePageService.queryAnnouncement();
    }

    @GetMapping("/getData")
    HttpResult getData(Integer announcementId) {
        return homePageService.getData(announcementId);
    }

    @GetMapping("/homeCalendar")
    HttpResult homeCalendar() {
        return homePageService.homeCalendar();
    }
}
