package com.example.bilibili.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论数据模型
 */
public class Comment {

    private String userName; //评论用户
    private String content; //评论内容
    private int likeCount; //点赞数

    public Comment(String userName, String content, int likeCount) {
        this.userName = userName;
        this.content = content;
        this.likeCount = likeCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public static List<Comment> createMockData() {
        List<Comment> list = new ArrayList<>();
        String[] users = {"用户A", "用户B", "用户C", "用户D"};

        for (int i = 0; i < 10; i++) {
            list.add(new Comment(
                    users[i % users.length],
                    "这是第" + (i + 1) + "条评论，用于占位",
                    100 + i * 7
            ));
        }

        return list;
    }
}
