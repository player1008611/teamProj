package com.teamProj.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teamProj.dao.AnnouncementDao;
import com.teamProj.dao.CareerFairDao;
import com.teamProj.dao.SchoolDao;
import com.teamProj.entity.Announcement;
import com.teamProj.entity.CareerFair;
import com.teamProj.entity.School;
import com.teamProj.service.HomePageService;
import com.teamProj.utils.HttpResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomePageImpl implements HomePageService {
    @Resource
    AnnouncementDao announcementDao;

    @Resource
    CareerFairDao careerFairDao;

    @Resource
    SchoolDao schoolDao;

    /**
     * Queries the latest news announcements (limited to 6) from the system.
     *
     * @return HttpResult Result containing a list of latest news announcements.
     */
    @Override
    public HttpResult queryNews() {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("category", "新闻").eq("top", "0")
                .select("announcement_id", "title", "cover", "creation_time", "content", "top");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        queryWrapper.clear();
        queryWrapper.eq("category", "新闻").eq("top", "1")
                .select("announcement_id", "title", "cover", "creation_time", "content", "top", "top_time");
        List<Announcement> topAnnouncements = announcementDao.selectList(queryWrapper);
        topAnnouncements.sort(((o1, o2) -> o2.getTopTime().compareTo(o1.getTopTime())));
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = topAnnouncements.subList(0, Math.min(topAnnouncements.size(), 6));
        result.addAll(announcements.subList(0, Math.min(announcements.size(), 6 - result.size())));
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
                .eq("category", "公告").eq("top", "0")
                .select("announcement_id", "title", "cover", "creation_time", "content", "top");
        List<Announcement> announcements = announcementDao.selectList(queryWrapper);
        queryWrapper.clear();
        queryWrapper.eq("category", "公告").eq("top", "1")
                .select("announcement_id", "title", "cover", "creation_time", "content", "top", "top_time");
        List<Announcement> topAnnouncements = announcementDao.selectList(queryWrapper);
        topAnnouncements.sort(((o1, o2) -> o2.getTopTime().compareTo(o1.getTopTime())));
        announcements.sort((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()));
        List<Announcement> result = topAnnouncements.subList(0, Math.min(topAnnouncements.size(), 6));
        result.addAll(announcements.subList(0, Math.min(announcements.size(), 6 - result.size())));
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

    @Override
    public HttpResult homeCalendar() {
        List<Map<String, String>> list = new ArrayList<>();
        QueryWrapper<CareerFair> careerFairQueryWrapper = new QueryWrapper<>();
        careerFairQueryWrapper.eq("status", "1");
        List<CareerFair> careerFairList = careerFairDao.selectList(careerFairQueryWrapper);
        for (CareerFair careerFair : careerFairList) {
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.eq("school_id", careerFair.getSchoolId());
            School school = schoolDao.selectOne(schoolQueryWrapper);
            String time = String.valueOf(careerFair.getStartTime()).substring(0, 10);
            list.add(new HashMap<>() {
                {
                    put("date", time);
                    put("content", school.getSchoolName() + "宣讲会");
                    put("div_num", 'x' + time);
                }
            });
        }
        return HttpResult.success(list, "查询成功");
    }
}
