package com.teamProj.entity;

public class Announcement {
    int announcementId;
    String adminId;
    String name;
    String cover;
    String category;
    String data;

    @Override
    public String toString() {
        return "Announcement{" +
                "announcementId=" + announcementId +
                ", adminId='" + adminId + '\'' +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", category='" + category + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
