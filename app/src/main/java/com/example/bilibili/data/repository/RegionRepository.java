package com.example.bilibili.data.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bilibili.data.api.ApiClient;
import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.data.api.RegionRankingResponse;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 分区数据仓库
 * 决定视频从哪里来
 */

/**
 * 分区数据仓库：通过 Retrofit 请求 B 站分区排行榜
 */
public class RegionRepository {

    private static final String TAG = "RegionRepository";

    public interface LoadCallback {
        void onResult(List<RecommendItem> items);
    }

    private final BiliApiService mApi;

    public RegionRepository() {
        mApi = ApiClient.getApi();
    }

    public void loadVideos(final LoadCallback callback) {
        // rid=4 是游戏区，day=3 是三日榜
        mApi.getRegionRanking(4, 3).enqueue(new Callback<RegionRankingResponse>() {
            @Override
            public void onResponse(Call<RegionRankingResponse> call, Response<RegionRankingResponse> response) {
                // 打印 HTTP 状态码和业务 code，方便排查
                Log.d(TAG, "httpCode=" + response.code()
                        + ", bizCode=" + (response.body() != null ? response.body().code : "null"));
                callback.onResult(mapToRecommendItems(response.body()));
            }

            @Override
            public void onFailure(Call<RegionRankingResponse> call, Throwable t) {
                // 失败一定要打印异常，否则日志里什么都看不到
                Log.e(TAG, "loadVideos failed", t);
                callback.onResult(new ArrayList<RecommendItem>());
            }
        });
    }

    // 把接口数据映射成 RecommendItem
    private List<RecommendItem> mapToRecommendItems(RegionRankingResponse response) {
        List<RecommendItem> items = new ArrayList<>();
        if (response == null || response.data == null) {
            return items;
        }

        for (RegionRankingResponse.Video video : response.data) {
            String title = video.title;
            String upName = video.author != null ? video.author : "";
            String play = formatView(video.play);
            // duration 已经是 "分:秒"，直接拿来用
            String duration = video.duration != null ? video.duration : "00:00";

            RecommendItem item = new RecommendItem(title, upName, play, duration);
            item.setCover(video.pic);
            items.add(item);
        }
        return items;
    }

    // 播放量格式化：超过 1 万显示成「x.x万」
    private String formatView(int view) {
        if (view >= 10000) {
            return String.format("%.1f万", view / 10000.0);
        }
        return String.valueOf(view);
    }
}