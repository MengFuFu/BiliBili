package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 番剧卡片数据模型
 */
public class BangumiItem {

    private String title; //番剧名
    private String desc; //描述：比如“更新至。。。”

    public BangumiItem(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static List<BangumiItem> createMockData() {
        List<BangumiItem> list = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            String title = "番剧第" + (i + 1) + "部";
            String desc = "更新至第" + (i % 12 + 1) + "话 · " + (i + 3) * 100 + "万追番";
            list.add(new BangumiItem(title, desc));
        }

        return list;
    }
}
