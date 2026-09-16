package com.example.bilibili.ui.recommend;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.RecommendRepository;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐页的ViewModel：负责持有数据和处理刷新、加载更多的逻辑
 */
public class RecommendViewModel extends ViewModel {

    //推荐列表数据
    private final MutableLiveData<List<RecommendItem>> mItems = new MutableLiveData<>();
    //是否正在刷新
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);

    private final RecommendRepository mRepository = new RecommendRepository();

    private int mPage = 1; //当前页
    private boolean mLoadingMore = false; //是否正在加载更多

    public LiveData<List<RecommendItem>> getItems() {
        return mItems;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    //刷新：回到第一页重新加载
    public void refresh() {
        mPage = 1;
        mRefreshing.setValue(true);
        mRepository.loadRecommend(1, new RecommendRepository.LoadCallback() {
            @Override
            public void onResult(List<RecommendItem> items) {
                mItems.setValue(items);
                mRefreshing.setValue(false);
            }
        });
    }

    //加载更多：追加下一页
    public void loadMore() {
        if(mLoadingMore) {
            return;
        }
        mLoadingMore = true;
        mPage++;
        mRepository.loadRecommend(mPage, new RecommendRepository.LoadCallback() {
            @Override
            public void onResult(List<RecommendItem> items) {
                List<RecommendItem> current = mItems.getValue();
                if(current == null) {
                    current = new ArrayList<>();
                }
                current.addAll(items);
                mItems.setValue(current);
                mLoadingMore = false;
            }
        });
    }

}
