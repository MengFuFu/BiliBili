package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播房间数据模型
 * 现在用假数据
 */
public class LiveRoom {

    private String userName; //主播名
    private String title; //直播间标题
    private String areaName; //分区名
    private int online; //在线人数
    private String coverUrl; //封面图地址

    public LiveRoom(String userName, String title, String areaName, int online) {
        this.userName = userName;
        this.title = title;
        this.areaName = areaName;
        this.online = online;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    /**
     * 假数据预览
     */
    public static List<LiveRoom> createMockData() {
        List<LiveRoom> list = new ArrayList<>();
        String[] areas = {"游戏", "娱乐", "手游", "绘画"};

        for (int i = 0; i < 12; i++) {
            String name = "主播" + (i + 1);
            String title = "这是第" + (i  + 1) + "个直播间的标题";
            String area = areas[i % areas.length];
            int online = 1000 + i * 137;
            list.add(new LiveRoom(name, title, area, online));
        }

        return list;
    }
}
