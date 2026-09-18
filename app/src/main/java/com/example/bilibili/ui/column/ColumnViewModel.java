package com.example.bilibili.ui.column;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.ColumnRepository;
import com.example.bilibili.model.bean.ArticleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 专栏页的 ViewModel
 */
public class ColumnViewModel extends ViewModel {

    private final MutableLiveData<List<ArticleItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mError = new MutableLiveData<>(false);
    private final ColumnRepository mRepository = new ColumnRepository();

    private int mPage = 1;
    private boolean mLoadingMore = false;

    public LiveData<List<ArticleItem>> getItems() {
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

    // 刷新：回到第一页
    public void refresh() {
        mPage = 1;
        mRefreshing.setValue(true);
        mRepository.loadArticles(1, new ColumnRepository.LoadCallback() {
            @Override
            public void onResult(List<ArticleItem> items) {
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
        mRepository.loadArticles(mPage, new ColumnRepository.LoadCallback() {
            @Override
            public void onResult(List<ArticleItem> items) {
                List<ArticleItem> current = mItems.getValue();
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