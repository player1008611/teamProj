package com.teamProj.controller;

import com.teamProj.service.HomePageService;
import com.teamProj.utils.HttpResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * The type Home page controller.
 */
@RestController
@RequestMapping("/homePage")
public class HomePageController {
    /**
     * The Home page service.
     */
    @Resource
    HomePageService homePageService;

    /**
     * Query news http result.
     *
     * @return the http result
     */
    @GetMapping("/queryNews")
    HttpResult queryNews() {
        return homePageService.queryNews();
    }

    /**
     * Query announcement http result.
     *
     * @return the http result
     */
    @GetMapping("/queryAnnouncement")
    HttpResult queryAnnouncement() {
        return homePageService.queryAnnouncement();
    }

    /**
     * Gets data.
     *
     * @param announcementId the announcement id
     * @return the data
     */
    @GetMapping("/getData")
    HttpResult getData(Integer announcementId) {
        return homePageService.getData(announcementId);
    }

    /**
     * Home calendar http result.
     *
     * @return the http result
     */
    @GetMapping("/homeCalendar")
    HttpResult homeCalendar() {
        return homePageService.homeCalendar();
    }
}
