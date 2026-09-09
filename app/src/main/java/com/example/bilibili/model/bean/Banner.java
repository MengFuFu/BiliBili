package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图数据模型
 */
public class Banner {

    private String title; //轮播图标题
    private int coverColor; //占位背景色

    public Banner(String title, int coverColor) {
        this.title = title;
        this.coverColor = coverColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(int coverColor) {
        this.coverColor = coverColor;
    }

    public static List<Banner> createMockData() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner("Banner 1", 0xFFFB7299)); // 粉
        list.add(new Banner("Banner 2", 0xFF21C1FC)); // 蓝
        list.add(new Banner("Banner 3", 0xFFFFA726)); // 橙
        list.add(new Banner("Banner 4", 0xFF66BB6A)); // 绿
        return list;
    }
}
