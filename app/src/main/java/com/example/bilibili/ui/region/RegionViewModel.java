package com.example.bilibili.ui.region;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.RegionRepository;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.List;

/**
 * 分区页的ViewModel
 */
public class RegionViewModel extends ViewModel {

    private final MutableLiveData<List<RecommendItem>> mVideos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mError = new MutableLiveData<>(false);
    private final RegionRepository mRepository = new RegionRepository();

    public LiveData<List<RecommendItem>> getVideos() {
        return mVideos;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public LiveData<Boolean> getError() {
        return mError;
    }

    //提示完错误消息就重置，避免重复弹
    public void consumeError() {
        mError.setValue(false);
    }

    //刷新：加载指定分区的排行榜
    public void refresh(final int rid) {
        mRefreshing.setValue(true);
        mRepository.loadVideos(rid, new RegionRepository.LoadCallback() {
            @Override
            public void onResult(List<RecommendItem> items) {
                mVideos.setValue(items);
                mRefreshing.setValue(false);
            }

            @Override
            public void onError() {
                mRefreshing.setValue(false);
                mError.setValue(true);
            }
        });
    }

}
