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

    /**
     * Queries the latest news announcements (limited to 6) from the system.
     *
     * @return HttpResult Result containing a list of latest news announcements.
     */
    @Override
    public HttpResult queryNews() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("category", "新闻")
                .select("announcement_id", "title", "cover", "creation_time", "content");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = announcements.subList(0, Math.min(announcements.size(), 6));
        return HttpResult.success(result, "查询成功");
    }

    /**
     * Queries the latest general announcements (limited to 6) from the system.
     *
     * @return HttpResult Result containing a list of latest general announcements.
     */
    @Override
    public HttpResult queryAnnouncement() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("category", "公告")
                .select("announcement_id", "title", "cover", "creation_time", "content");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = announcements.subList(0, Math.min(announcements.size(), 6));
        return HttpResult.success(result, "查询成功");
    }

    /**
     * Retrieves specific data related to a particular announcement by its ID.
     *
     * @param announcementId Integer value representing the ID of the announcement for data retrieval.
     * @return HttpResult Result containing the retrieved data related to the specified announcement.
     */
    @Override
    public HttpResult getData(Integer announcementId) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcement_id", announcementId).select("data");
        Announcement announcement = announcementDao.selectOne(queryWrapper);
        return HttpResult.success(announcement, "查询成功");
    }
}
