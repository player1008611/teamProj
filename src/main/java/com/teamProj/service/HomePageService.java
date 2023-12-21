package com.teamProj.service;

import com.teamProj.utils.HttpResult;

public interface HomePageService {
    HttpResult queryNews();

    HttpResult queryAnnouncement();

    HttpResult getData(Integer announcementId);

    HttpResult homeCalendar();
}
