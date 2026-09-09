package com.example.bilibili.model.bean;

/**
 * 直播列表里的“分区标题”数据
 * 例如：推荐、游戏、娱乐、手游
 */
public class SectionHeader {

    private String title;

    public SectionHeader(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
