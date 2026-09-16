package com.example.bilibili.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.data.api.PopularResponse;
import com.example.bilibili.model.bean.RecommendItem;

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
 * 推荐数据仓库：负责“推荐数据从哪里来”，通过Retrofit请求真实接口
 */
public class RecommendRepository {

    //加载结果回调
    public interface LoadCallback {
        void onResult(List<RecommendItem> items);
        void onError(); //加载失败
    }

    private final BiliApiService mApi;

    public RecommendRepository() {
        //自定义OkHttpClient：加User-Agent，避免接口风控
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                            "Chrome/120.0.0.0 Safari/537.36")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        //创建Retrofit实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bilibili.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(BiliApiService.class);
    }

    //加载某一页推荐数据
    public void loadRecommend(int page, final LoadCallback callback) {
        mApi.getPopular(20, page).enqueue(new Callback<PopularResponse>() {
            @Override
            public void onResponse(Call<PopularResponse> call, Response<PopularResponse> response) {
                //把接口返回的数据映射成RecommendItem
                List<RecommendItem> items = mapToRecommendItems(response.body());
                callback.onResult(items);
            }

            @Override
            public void onFailure(Call<PopularResponse> call, Throwable t) {
                callback.onError(); //通知加载失败
            }
        });
    }

    //把热门视频数据映射成推荐列表所需的RecommendItem
    private List<RecommendItem> mapToRecommendItems(PopularResponse response) {
        List<RecommendItem> items = new ArrayList<>();
        if(response == null || response.data == null || response.data.list == null) {
            return items;
        }

        for(PopularResponse.Video video : response.data.list) {
            String title = video.title;
            String upName = video.owner != null ? video.owner.name : "";
            String play = formatView(video.stat != null ? video.stat.view : 0);
            String duration = formatDuration(video.duration);

            RecommendItem item = new RecommendItem(title, upName, play, duration);
            item.setCover(video.pic);
            items.add(item);
        }
        return items;
    }

    //播放量格式化
    private String formatView(int view) {
        if(view >= 10000) {
            return String.format("%.1f万", view / 10000.0);
        }
        return String.valueOf(view);
    }
    //时长格式化
    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}
