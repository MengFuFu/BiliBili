package com.example.bilibili.ui.dynamic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.dynamic.adapter.DynamicAdapter;

import java.util.List;

/**
 * 动态页
 */
public class DynamicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dynamic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //工具栏标题设为“动态”
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.section_dynamic);

        //三横线打开抽屉
        view.findViewById(R.id.ll_top_menu_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = requireActivity().findViewById(R.id.main_drawer_layout);
                if(drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);

        //单列动态列表
        List<RecommendItem> items = RecommendItem.createMockData(4);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DynamicAdapter(items));

        refreshLayout.setColorSchemeResources(R.color.theme_color_primary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
