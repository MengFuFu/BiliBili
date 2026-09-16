package com.example.bilibili.data.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 番剧索引接口返回的GSON
 */
public class BangumiResponse {

    public int code;
    public String message;
    public Data data;

    public static class Data {
        public List<Season> list;
        public int has_next;
    }

    public static class Season {
        public String title; //番剧名
        public String cover; //封面URL
        public String order; // 排序文本

        @SerializedName("index_show")
        public String indexShow; //更新进度
    }
}
