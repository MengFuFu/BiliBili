package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图数据模型
 */
public class Banner {

    private String title; //轮播图标题
    private String imageUrl; //图片地址
    private String link; //点击跳转地址

    public Banner(String title, String imageUrl, String link) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public static List<Banner> createMockData() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner("英雄联盟赛事",
                "https://i0.hdslb.com/bfs/live-key-frame/keyframe09181830000007734200a09a73.jpg",
                "https://www.bilibili.com/"));
        list.add(new Banner("德云色",
                "https://i0.hdslb.com/bfs/live/new_room_cover/258c61418fc8483e5c191ff9c4f771509de606a5.jpg",
                "https://www.bilibili.com/"));
        list.add(new Banner("三角洲行动",
                "https://i0.hdslb.com/bfs/live/new_room_cover/9abe60236c7252ce473babf65b023843e7d36959.jpg",
                "https://www.bilibili.com/"));
        list.add(new Banner("点唱",
                "https://i0.hdslb.com/bfs/live/new_room_cover/13c5990a893d4de8e3a5a09d70d44aab354b26e4.jpg",
                "https://www.bilibili.com/"));
        return list;
    }
}
