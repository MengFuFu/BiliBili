package com.example.bilibili.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.bilibili.data.api.ApiClient;
import com.example.bilibili.data.api.BangumiResponse;
import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.model.bean.BangumiItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 番剧/影视数据仓库
 * 通过seasonType区分：1 = 番剧，2 = 电影
 */
public class BangumiRepository {

    public interface LoadCallback {
        void onResult(List<BangumiItem> items);
    }

    private final BiliApiService mApi;

    public BangumiRepository() {
        mApi = ApiClient.getApi();
    }

    //加载某类PGC内容的某一页
    public void loadBangumi(int seasonType, int page, final LoadCallback callback) {
        mApi.getBangumi(1, seasonType, 20, page).enqueue(new Callback<BangumiResponse>() {
            @Override
            public void onResponse(Call<BangumiResponse> call, Response<BangumiResponse> response) {
                callback.onResult(mapToBangumiItems(response.body()));
            }

            @Override
            public void onFailure(Call<BangumiResponse> call, Throwable t) {
                callback.onResult(new ArrayList<BangumiItem>());
            }
        });
    }

    //把接口数据映射成BangumiItem
    private List<BangumiItem> mapToBangumiItems(BangumiResponse response) {
        List<BangumiItem> items = new ArrayList<>();
        if(response == null || response.data == null || response.data.list == null) {
            return items;
        }

        for(BangumiResponse.Season season : response.data.list) {
            String title = season.title;
            String progress = season.indexShow != null ? season.indexShow : "";

            //番剧用order（如“9.9分”）；电影order是空字符串，改用score
            String rating = (season.order != null && !season.order.isEmpty() ? season.order : (season.score != null ? season.score + "分" : "" ));
            //没有评分就不拼"·"
            String desc = rating.isEmpty() ? progress : progress + " · " + rating;

            BangumiItem item = new BangumiItem(title, desc);
            item.setCover(season.cover);
            items.add(item);
        }
        return items;
    }
}
