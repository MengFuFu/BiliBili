package com.example.bilibili.ui.message;

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

import com.example.bilibili.R;
import com.example.bilibili.model.bean.MessageItem;
import com.example.bilibili.ui.message.adapter.MessageAdapter;

/**
 * 消息页
 */
public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //工具栏标题设为“消息”
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.section_message);

        //三横线打开抽屉
        view.findViewById(R.id.ll_top_menu_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = requireActivity().findViewById(R.id.main_drawer_layout);
                if (drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        //消息列表
        RecyclerView recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MessageAdapter(MessageItem.createMockData()));
    }
}
