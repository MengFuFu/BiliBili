package com.example.bilibili.model.bean;

import com.example.bilibili.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 分区入口数据模型
 */
public class RegionItem {

    private String name; //分区名
    private int icon; //分区图标
    private int rid; //B站一级分区id，用来请求排行榜

    public RegionItem(String name, int icon, int rid) {
        this.name = name;
        this.icon = icon;
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public RegionItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public static List<RegionItem> createMockData() {
        List<RegionItem> list = new ArrayList<>();
        list.add(new RegionItem("游戏", R.drawable.ic_live_home_game, 4));
        list.add(new RegionItem("娱乐", R.drawable.ic_live_home_entertainment, 5));
        list.add(new RegionItem("绘画", R.drawable.live_home_painting, 1));       // 归到动画区
        list.add(new RegionItem("手游", R.drawable.ic_live_home_mobile_game, 4)); // 归到游戏区
        list.add(new RegionItem("剪辑", R.drawable.live_home_clip_video, 119));   // 归到鬼畜区
        list.add(new RegionItem("全部", R.drawable.live_home_all_category, 4));   // 全站接口没接，先指游戏区
        return list;
    }
}
