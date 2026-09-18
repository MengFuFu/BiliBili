package com.example.bilibili.data.api;

import java.util.List;

/**
 * 直播推荐房间接口返回的 JSON 模型
 */
public class LiveResponse {

    public int code;
    public String message;
    public List<Room> data;

    public static class Room {
        public String title;      // 直播标题
        public String uname;      // 主播名
        public int online;        // 在线人数
        public String user_cover; // 房间封面
        public String areaName;   // 分区名（可能为空）
    }
}