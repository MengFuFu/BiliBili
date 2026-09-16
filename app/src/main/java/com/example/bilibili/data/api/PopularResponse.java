package com.example.bilibili.data.api;

import java.util.List;

/**
 * 热门视频接口返回的JSON对应的模型
 * 字段名和接口返回的key一致，GSON会自动映射
 */
public class PopularResponse {

    public int code;
    public String message;
    public Data data;

    public static class Data {
        public List<Video> list;
        public boolean no_more;
    }

    public static class Video {
        public String title; //标题
        public String pic; //封面图URL
        public long duration; //时长 （秒）
        public Owner owner; //UP主信息
        public Stat stat; //统计信息
    }

    public static class Owner {
        public String name; //UP主名
    }

    public static class Stat {
        public int view; //播放量
        public int danmaku; //弹幕数
    }
}
