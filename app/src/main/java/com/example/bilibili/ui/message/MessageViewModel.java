package com.example.bilibili.ui.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.MessageRepository;
import com.example.bilibili.model.bean.MessageItem;

import java.util.List;

/**
 * 消息页的 ViewModel
 */
public class MessageViewModel extends ViewModel {

    private final MutableLiveData<List<MessageItem>> mItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MessageRepository mRepository = new MessageRepository();

    public LiveData<List<MessageItem>> getItems() {
        return mItems;
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public void refresh() {
        mRefreshing.setValue(true);
        mRepository.loadMessages(new MessageRepository.LoadCallback() {
            @Override
            public void onResult(List<MessageItem> items) {
                mItems.setValue(items);
                mRefreshing.setValue(false);
            }
        });
    }
}