package com.example.bilibili.ui.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.recommend.adapter.RecommendAdapter;

import java.util.List;

/**
 * 首页 - 推荐页
 * 用两列网格展示推荐视频卡片
 */
public class RecommendFragment extends Fragment {

    private SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.layout_refresh);

        //两列网格
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        //假数据
        List<RecommendItem> items = RecommendItem.createMockData();
        RecommendAdapter adapter = new RecommendAdapter(items);
        recyclerView.setAdapter(adapter);

        //下拉刷新
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }
}
