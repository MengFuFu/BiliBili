package com.example.bilibili.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 追番记录实体：一张表对应一个实体
 */
@Entity(tableName = "follow_bangumi")
public class FollowEntity {

    @PrimaryKey
    @NonNull
    public String title; //番剧名为主键

    public FollowEntity(String title) {
        this.title = title;
    }
}
