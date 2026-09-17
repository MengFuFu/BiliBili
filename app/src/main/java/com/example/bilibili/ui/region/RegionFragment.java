package com.example.bilibili.ui.region;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.model.bean.RegionItem;
import com.example.bilibili.ui.recommend.RecommendViewModel;
import com.example.bilibili.ui.region.adapter.RegionAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分区页：分区入口 + 两列视频卡片
 */
public class RegionFragment extends Fragment {

    private RegionAdapter mAdapter;

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

        //设置工具栏标题为“分区”
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.section_region);

        //左侧三横线按钮：打开侧边抽屉
        view.findViewById(R.id.ll_top_menu_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = requireActivity().findViewById(R.id.main_drawer_layout);
                if(drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        //数据
        final List<RegionItem> partitions = RegionItem.createMockData();

        //拿ViewModel
        RegionViewModel viewModel = new ViewModelProvider(this).get(RegionViewModel.class);

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

        mAdapter = new RegionAdapter(partitions, new ArrayList<RecommendItem>());

        //点分去入口时，切换到对应分区
        mAdapter.setOnPartitionClickListener(new RegionAdapter.OnPartitionClickListener() {
            @Override
            public void onPartitionClick(RegionItem item) {
                viewModel.refresh(item.getRid());
            }
        });

        recyclerView.setAdapter(mAdapter);

        //观察视频列表，变化时刷新
        viewModel.getVideos().observe(getViewLifecycleOwner(), new Observer<List<RecommendItem>>() {
            @Override
            public void onChanged(List<RecommendItem> items) {
                mAdapter.resetVideos(items);
            }
        });
        //观察刷新状态，控制下拉转圈
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });
        //观察错误状态
        viewModel.getError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    Toast.makeText(getContext(), "加载失败，请假查网络设置", Toast.LENGTH_SHORT).show();
                    viewModel.consumeError(); //提示完消费错误
                }
            }
        });

        // 下拉刷新
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(partitions.get(0).getRid());
            }
        });

        //初次刷新
        viewModel.refresh(partitions.get(0).getRid());
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
