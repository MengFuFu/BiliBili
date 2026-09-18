package com.example.bilibili.ui.bangumi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.BangumiRepository;
import com.example.bilibili.model.bean.BangumiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 番剧 / 影视页 ViewModel
 */
public class BangumiViewModel extends ViewModel {

    private final BangumiRepository mRepository = new BangumiRepository();
    private final MutableLiveData<List<BangumiItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mError = new MutableLiveData<>(false);

    private int mSeasonType = 1; // 当前类型
    private int mPage = 1;       // 当前页码
    private boolean mLoadingMore = false;

    public LiveData<List<BangumiItem>> getItems() {
        return mItems;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public LiveData<Boolean> getError() {
        return mError;
    }

    public void consumeError() {
        mError.setValue(false);
    }

    // 刷新：回到第一页重新加载
    public void refresh(final int seasonType) {
        mSeasonType = seasonType;
        mPage = 1;
        mRefreshing.setValue(true);
        mRepository.loadBangumi(seasonType, 1, new BangumiRepository.LoadCallback() {
            @Override
            public void onResult(List<BangumiItem> items) {
                mItems.setValue(items);
                mRefreshing.setValue(false);
            }

            @Override
            public void onError() {
                mRefreshing.setValue(false);
                mError.setValue(true);
            }
        });
    }

    // 加载更多：追加下一页
    public void loadMore() {
        if (mLoadingMore) {
            return;
        }
        mLoadingMore = true;
        mPage++;
        mRepository.loadBangumi(mSeasonType, mPage, new BangumiRepository.LoadCallback() {
            @Override
            public void onResult(List<BangumiItem> items) {
                List<BangumiItem> current = mItems.getValue();
                if (current == null) {
                    current = new ArrayList<>();
                }
                current.addAll(items);
                mItems.setValue(current);
                mLoadingMore = false;
            }

            @Override
            public void onError() {
                mLoadingMore = false;
                mError.setValue(true);
            }
        });
    }
}