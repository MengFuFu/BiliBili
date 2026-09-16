package com.example.bilibili.model.bean;

import com.example.bilibili.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息条目数据模型
 */
public class MessageItem {

    private int icon; // 图标
    private String title; // 标题
    private String subtitle; //副标题

    public MessageItem(int icon, String title, String subtitle) {
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public static List<MessageItem> createMockData() {
        List<MessageItem> list = new ArrayList<>();
        list.add(new MessageItem(R.drawable.ic_settings_black_24dp, "系统通知", "你有一条新通知"));
        list.add(new MessageItem(R.drawable.ic_people_black_24dp, "回复我的", "3条新回复"));
        list.add(new MessageItem(R.drawable.ic_star_black_24dp, "@我的", "2条新消息"));
        list.add(new MessageItem(R.drawable.ic_star_black_24dp, "收到的赞", "收到 128 个赞"));
        list.add(new MessageItem(R.drawable.ic_history_black_24dp, "私信", "某某发来一条私信"));
        return list;
    }
}
