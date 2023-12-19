package com.teamProj.service;

import com.teamProj.utils.HttpResult;
import org.springframework.stereotype.Service;


public interface HomePageService {
    HttpResult queryNews();

    HttpResult queryAnnouncement();

    HttpResult getData(Integer announcementId);
}
