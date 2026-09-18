package com.example.bilibili.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.bilibili.model.bean.MessageItem;

import java.util.List;

/**
 * 消息数据仓库
 * 消息接口需要登录，暂时用假数据模拟异步加载
 */
public class MessageRepository {

    public interface LoadCallback {
        void onResult(List<MessageItem> items);
    }

    public void loadMessages(final LoadCallback callback) {
        // 模拟网络延迟，让下拉刷新转圈能看得到
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResult(MessageItem.createMockData());
            }
        }, 500);
    }
}