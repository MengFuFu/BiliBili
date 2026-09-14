package com.example.bilibili.ui.region;

import android.graphics.Rect;
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
import com.example.bilibili.model.bean.RegionItem;
import com.example.bilibili.ui.region.adapter.RegionAdapter;

import java.util.List;

/**
 * 分区页：分区入口 + 两列视频卡片
 */
public class RegionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_region, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);

        //数据
        final List<RegionItem> partitions = RegionItem.createMockData();
        List<RecommendItem> videos = RecommendItem.createMockData(3);

        // 两列网格：分区入口占满两列，视频卡片各占一列
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position < partitions.size() ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        // 统一间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.margin_small);
        recyclerView.addItemDecoration(new RegionSpacingItemDecoration(spacing, partitions.size()));

        recyclerView.setAdapter(new RegionAdapter(partitions, videos));

        // 下拉刷新先简单收尾
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    // 网格间距：分区入口整行留边距，视频卡片左右对称
    static class RegionSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spacing;
        private final int partitionCount;

        RegionSpacingItemDecoration(int spacing, int partitionCount) {
            this.spacing = spacing;
            this.partitionCount = partitionCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (position < partitionCount) {
                // 分区入口占满整行
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = spacing;
                outRect.bottom = spacing;
                return;
            }

            // 视频卡片
            int column = (position - partitionCount) % 2;
            outRect.left = spacing - column * spacing / 2;
            outRect.right = (column + 1) * spacing / 2;
            outRect.top = 0;
            outRect.bottom = spacing;
        }
    }
}
