package com.example.bilibili.ui.column;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.ColumnRepository;
import com.example.bilibili.model.bean.ArticleItem;

import java.util.List;

/**
 * 专栏页的ViewModel
 */
public class ColumnViewModel extends ViewModel {

    private final MutableLiveData<List<ArticleItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mError = new MutableLiveData<>(false);
    private final ColumnRepository mRepository = new ColumnRepository();

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

    public void refresh() {
        mRefreshing.setValue(true);

        mRepository.loadArticles(new ColumnRepository.LoadCallback() {
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

}