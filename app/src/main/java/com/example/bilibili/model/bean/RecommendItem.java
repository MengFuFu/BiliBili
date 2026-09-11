package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐页视频卡片的数据模型
 */
public class RecommendItem {

    private String title; //视频标题
    private String upName; //UP主名字
    private String play; //播放量文字
    private String duration; //视频时长

    public RecommendItem(String title, String upName, String play, String duration) {
        this.title = title;
        this.upName = upName;
        this.play = play;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpName() {
        return upName;
    }

    public void setUpName(String upName) {
        this.upName = upName;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public static List<RecommendItem> createMockData() {
        return createMockData(1);
    }

    //page表示第几页，用来生成不同标题的假数据，以便看出“加载更多”生效
    public static List<RecommendItem> createMockData(int page) {
        List<RecommendItem> list = new ArrayList<>();
        String[] ups = {"小卤蛋", "肥嘟嘟", "胖猫", "奶龙"};

        for (int i = 0; i < 20; i++) {
            //全局序号：第一页是0~19，第二页是20~39.。。
            int index = (page - 1) * 20 + i;

            String title = "这是第" + (index + 1) + "个推荐视频的标题";
            String up = ups[index % ups.length];
            String play = (index + 1) * 8 + "万";

            int minutes = 5 + (index % 30);
            int seconds = (index % 7) % 60;
            String duration = String.format("%02d:%02d", minutes, seconds);

            list.add(new RecommendItem(title, up, play, duration));
        }

        return list;
    }
}
