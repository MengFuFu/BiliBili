package com.example.bilibili.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * 追番记录的增删改查
 */
@Dao
public interface FollowDao {

    //插入一条追番记录
    @Insert
    void insert(FollowEntity entity);

    //删除一条追番记录
    @Delete
    void delete(FollowEntity entity);

    //按番剧名查询是否已追番
    @Query("SELECT * FROM follow_bangumi WHERE title = :title")
    FollowEntity findByTitle(String title);

    // 查所有追番记录
    @Query("SELECT * FROM follow_bangumi")
    java.util.List<FollowEntity> getAll();
}
