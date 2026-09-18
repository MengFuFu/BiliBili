package com.example.bilibili.data.repository;

import android.util.Log;

import com.example.bilibili.data.api.ApiClient;
import com.example.bilibili.data.api.BangumiResponse;
import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.model.bean.BangumiItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 番剧 / 影视数据仓库
 * 通过 seasonType 区分：1=番剧，2=电影
 */
public class BangumiRepository {

    private static final String TAG = "BangumiRepository";

    public interface LoadCallback {
        void onResult(List<BangumiItem> items);
        void onError();
    }

    private final BiliApiService mApi;

    public BangumiRepository() {
        mApi = ApiClient.getApi();
    }

    public void loadBangumi(int seasonType, int page, final LoadCallback callback) {
        mApi.getBangumi(1, seasonType, 20, page).enqueue(new Callback<BangumiResponse>() {
            @Override
            public void onResponse(Call<BangumiResponse> call, Response<BangumiResponse> response) {
                BangumiResponse body = response.body();
                if (body == null || body.code != 0) {
                    Log.e(TAG, "biz error, code=" + (body != null ? body.code : "null"));
                    callback.onError();
                    return;
                }
                callback.onResult(mapToBangumiItems(body));
            }

            @Override
            public void onFailure(Call<BangumiResponse> call, Throwable t) {
                Log.e(TAG, "loadBangumi failed", t);
                callback.onError();
            }
        });
    }

    private List<BangumiItem> mapToBangumiItems(BangumiResponse response) {
        List<BangumiItem> items = new ArrayList<>();
        if (response == null || response.data == null || response.data.list == null) {
            return items;
        }

        for (BangumiResponse.Season season : response.data.list) {
            String title = season.title;
            String progress = season.indexShow != null ? season.indexShow : "";

            // 番剧用 order（如"9.9分"）；电影 order 为空，改用 score
            String rating = (season.order != null && !season.order.isEmpty())
                    ? season.order
                    : (season.score != null ? season.score + "分" : "");

            // 没有评分就不拼 "·"
            String desc = rating.isEmpty() ? progress : progress + " · " + rating;

            BangumiItem item = new BangumiItem(title, desc);
            item.setCover(season.cover);
            items.add(item);
        }
        return items;
    }
}