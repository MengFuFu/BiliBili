package com.example.bilibili.data.api;

import java.util.List;

/**
 * 分区排行榜接口返回的是JSON模型
 */
public class RegionRankingResponse {

    public int code;
    public String message;
    public List<Video> data;

    public static class Video {
        public String title;    // 标题
        public String pic;      // 封面 URL
        public String duration; // 时长（已经是 "分:秒" 字符串）
        public String author;   // UP 主名字
        public int play;        // 播放量
    }
}