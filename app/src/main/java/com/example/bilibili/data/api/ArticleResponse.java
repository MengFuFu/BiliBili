package com.example.bilibili.data.api;

import java.util.List;

/**
 * 专栏文章接口返回的JSON模型
 */
public class ArticleResponse {

    public int code;
    public String message;
    public List<Article> data;

    public static class Article {
        public String title; //标题
        public String summary; //摘要
        public String banner_url; //大图
        public Author author; //作者
        public Stats stats; //统计
        public List<String> image_urls; //配图列表
    }

    public static class Author {
        public String name; //作者名
    }

    public static class Stats {
        public int view; //阅读数
    }
}
