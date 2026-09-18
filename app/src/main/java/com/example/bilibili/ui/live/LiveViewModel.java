package com.example.bilibili.ui.live;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bilibili.data.repository.LiveRepository;
import com.example.bilibili.model.bean.LiveRoom;

import java.util.List;

/**
 * 直播页的 ViewModel
 */
public class LiveViewModel extends ViewModel {

    private final MutableLiveData<List<LiveRoom>> mRooms = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mError = new MutableLiveData<>(false);
    private final LiveRepository mRepository = new LiveRepository();

    public LiveData<List<LiveRoom>> getRooms() {
        return mRooms;
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
        mRepository.loadRooms(new LiveRepository.LoadCallback() {
            @Override
            public void onResult(List<LiveRoom> rooms) {
                mRooms.setValue(rooms);
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