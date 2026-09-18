package com.example.bilibili.ui.dynamic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.DynamicRepository;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.List;

/**
 * 动态页的 ViewModel
 */
public class DynamicViewModel extends ViewModel {

    private final MutableLiveData<List<RecommendItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final DynamicRepository mRepository = new DynamicRepository();

    public LiveData<List<RecommendItem>> getItems() {
        return mItems;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public void refresh() {
        mRefreshing.setValue(true);
        mRepository.loadDynamic(new DynamicRepository.LoadCallback() {
            @Override
            public void onResult(List<RecommendItem> items) {
                mItems.setValue(items);
                mRefreshing.setValue(false);
            }
        });
    }
}