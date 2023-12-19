package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.AnnouncementDao;
import com.teamProj.entity.Announcement;
import com.teamProj.service.HomePageService;
import com.teamProj.utils.HttpResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HomePageImpl implements HomePageService {
    @Resource
    AnnouncementDao announcementDao;

    @Override
    public HttpResult queryNews() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", "新闻");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = announcements.subList(0, Math.min(announcements.size(), 5));
        return HttpResult.success(result, "查询成功");
    }

    @Override
    public HttpResult queryAnnouncement() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", "公告");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = announcements.subList(0, Math.min(announcements.size(), 5));
        return HttpResult.success(result, "查询成功");
    }
}
