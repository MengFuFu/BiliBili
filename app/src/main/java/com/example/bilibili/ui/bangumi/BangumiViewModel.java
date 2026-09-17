package com.example.bilibili.ui.bangumi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.BangumiRepository;
import com.example.bilibili.model.bean.BangumiItem;

import java.util.List;

/**
 * 番剧页ViewModel
 */
public class BangumiViewModel extends ViewModel {

    private final BangumiRepository mRepository = new BangumiRepository();
    private final MutableLiveData<List<BangumiItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);

    public LiveData<List<BangumiItem>> getItems() {
        return mItems;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public void refresh(final int seasonType) {
        mRefreshing.setValue(true);
        mRepository.loadBangumi(seasonType, 1, new BangumiRepository.LoadCallback() {
            @Override
            public void onResult(List<BangumiItem> items) {
                mItems.setValue(items);
                mRefreshing.setValue(false);
            }
        });
    }
}
