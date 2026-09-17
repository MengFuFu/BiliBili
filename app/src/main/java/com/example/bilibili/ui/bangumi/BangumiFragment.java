package com.example.bilibili.ui.bangumi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.BangumiItem;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.ui.bangumi.adapter.BangumiAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 - 番剧页 / 影视页
 * season_type 1:番剧 2：电影
 */
public class BangumiFragment extends Fragment {

    private static final String ARG_SEASON_TYPE = "season_type";

    private BangumiAdapter mAdapter;
    private int mSeasonType = 1; //默认番剧

    //工厂方法：创建指定类型的页面
    public static BangumiFragment newInstance(int seasonType) {
        BangumiFragment fragment = new BangumiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SEASON_TYPE, seasonType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bangumi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //从arguments读取season_type
        Bundle args = getArguments();
        if(args != null) {
            mSeasonType = args.getInt(ARG_SEASON_TYPE, 1);
        }

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);

        //拿到ViewModel
        BangumiViewModel viewModel = new ViewModelProvider(this).get(BangumiViewModel.class);

        // 数据Banner + 番剧卡片
        List<Banner> banners = Banner.createMockData();

        //单列列表
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BangumiAdapter(banners, new ArrayList<BangumiItem>());
        recyclerView.setAdapter(mAdapter);

        //观察番剧列表数据
        viewModel.getItems().observe(getViewLifecycleOwner(), new Observer<List<BangumiItem>>() {
            @Override
            public void onChanged(List<BangumiItem> items) {
                mAdapter.resetItems(items);
            }
        });

        //观察刷新状态，控制下拉转圈
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        //下拉刷新
        refreshLayout.setColorSchemeResources(R.color.theme_color_primary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(mSeasonType);
            }
        });

        //初次加载
        viewModel.refresh(mSeasonType);
    }
}
