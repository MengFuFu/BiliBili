package com.example.bilibili.data.repository;

import android.util.Log;

import com.example.bilibili.data.api.ApiClient;
import com.example.bilibili.data.api.BiliApiService;
import com.example.bilibili.data.api.LiveResponse;
import com.example.bilibili.model.bean.LiveRoom;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 直播数据仓库：请求直播推荐房间列表
 */
public class LiveRepository {

    private static final String TAG = "LiveRepository";

    public interface LoadCallback {
        void onResult(List<LiveRoom> rooms);
        void onError();
    }

    private final BiliApiService mApi;

    public LiveRepository() {
        mApi = ApiClient.getApi();
    }

    public void loadRooms(final LoadCallback callback) {
        mApi.getLiveRecommend(1, 20).enqueue(new Callback<LiveResponse>() {
            @Override
            public void onResponse(Call<LiveResponse> call, Response<LiveResponse> response) {
                LiveResponse body = response.body();
                if (body == null || body.code != 0) {
                    Log.e(TAG, "biz error, code=" + (body != null ? body.code : "null"));
                    callback.onError();
                    return;
                }
                callback.onResult(mapToLiveRooms(body));
            }

            @Override
            public void onFailure(Call<LiveResponse> call, Throwable t) {
                Log.e(TAG, "loadRooms failed", t);
                callback.onError();
            }
        });
    }

    // 接口数据映射成 LiveRoom
    private List<LiveRoom> mapToLiveRooms(LiveResponse response) {
        List<LiveRoom> rooms = new ArrayList<>();
        if (response == null || response.data == null) {
            return rooms;
        }

        for (LiveResponse.Room r : response.data) {
            String name = r.uname != null ? r.uname : "";
            String title = r.title != null ? r.title : "";
            // 这个接口的分区名是空的，给个默认值
            String area = (r.areaName != null && !r.areaName.isEmpty()) ? r.areaName : "直播";

            LiveRoom room = new LiveRoom(name, title, area, r.online);
            room.setCoverUrl(r.user_cover);
            rooms.add(room);
        }
        return rooms;
    }
}