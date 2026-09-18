package com.example.bilibili.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.bilibili.model.bean.RecommendItem;

import java.util.List;

/**
 * 动态数据仓库
 * 动态接口需要登录，暂时用假数据模拟异步加载
 */
public class DynamicRepository {

    public interface LoadCallback {
        void onResult(List<RecommendItem> items);
    }

    public void loadDynamic(final LoadCallback callback) {
        // 用 Handler 模拟网络延迟，让下拉刷新转圈能看得到
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResult(RecommendItem.createMockData(4));
            }
        }, 500);
    }
}