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
    private final RegionRepository mRepository = new RegionRepository();

    public LiveData<List<RecommendItem>> getVideos() {
        return mVideos;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    //刷新
    public void refresh() {
        mRefreshing.setValue(true);
        mRepository.loadVideos(new RegionRepository.LoadCallback() {
            @Override
            public void onResult(List<RecommendItem> items) {
                mVideos.setValue(items);
                mRefreshing.setValue(false);
            }
        });
    }

}
