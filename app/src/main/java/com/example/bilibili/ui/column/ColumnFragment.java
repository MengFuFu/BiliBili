package com.example.bilibili.ui.column;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.ArticleItem;
import com.example.bilibili.ui.column.adapter.ColumnAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 - 专栏页
 */
public class ColumnFragment extends Fragment {

    private ColumnAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_column, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);

        ColumnViewModel viewModel = new ViewModelProvider(this).get(ColumnViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ColumnAdapter(new ArrayList<ArticleItem>());
        recyclerView.setAdapter(mAdapter);

        // 滚动监听：快到底部时加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager == null) {
                    return;
                }
                int lastVisible = manager.findLastVisibleItemPosition();
                int total = mAdapter.getItemCount();
                if (lastVisible + 4 >= total) {
                    viewModel.loadMore();
                }
            }
        });

        // 观察文章列表
        viewModel.getItems().observe(getViewLifecycleOwner(), new Observer<List<ArticleItem>>() {
            @Override
            public void onChanged(List<ArticleItem> items) {
                mAdapter.resetItems(items);
            }
        });

        // 观察刷新状态
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        // 观察错误状态
        viewModel.getError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    Toast.makeText(getContext(), "加载失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    viewModel.consumeError();
                }
            }
        });

        // 下拉刷新
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }
        });

        // 初次加载
        viewModel.refresh();
    }
}