package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 专栏文章卡片数据模型
 */
public class ArticleItem {

    private String title; //文章标题
    private String summary; //摘要
    private String author; //作者
    private String view; //浏览数
    private String cover; //封面图URL

    public ArticleItem(String title, String summary, String author, String view) {
        this.title = title;
        this.summary = summary;
        this.author = author;
        this.view = view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    // 暂时先用假数据跑通界面
    public static List<ArticleItem> createMockData() {
        List<ArticleItem> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArticleItem item = new ArticleItem(
                    "这是第" + (i + 1) + "篇专栏文章的标题",
                    "这是第" + (i + 1) + "篇文章的摘要内容，用来占位展示效果，后续会换成真实接口数据。",
                    "作者" + (i + 1),
                    (i + 1) * 3 + "万阅读");
            list.add(item);
        }
        return list;
    }
}
