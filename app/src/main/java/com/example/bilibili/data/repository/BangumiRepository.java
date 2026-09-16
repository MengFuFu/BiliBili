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
 * 番剧数据仓库
 */
public class BangumiRepository {

    public interface LoadCallback {
        void onResult(List<BangumiItem> items);
    }

    private final BiliApiService mApi;

    public BangumiRepository() {
        mApi = ApiClient.getApi();
    }

    //加载某一页的番剧数据
    public void loadBangumi(int page, final LoadCallback callback) {
        mApi.getBangumi(1, 1, 20, page).enqueue(new Callback<BangumiResponse>() {
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
            String order = season.order != null ? season.order : "";
            String desc = progress + " · " + order;

            BangumiItem item = new BangumiItem(title, desc);
            item.setCover(season.cover);
            items.add(item);
        }
        return items;
    }
}
