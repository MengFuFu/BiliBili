package com.example.bilibili.ui.bangumi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.ui.bangumi.adapter.EpisodeAdapter;

/**
 * 番剧详细页骨架
 */
public class BangumiDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, BangumiDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_detail);

        String title = getIntent().getStringExtra(EXTRA_TITLE);

        //顶部标题栏
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //番剧标题
        TextView tvBangumiTitle = findViewById(R.id.tv_bangumi_title);
        tvBangumiTitle.setText(title);

        //追番人数
        TextView tvFollowCount = findViewById(R.id.tv_follow_count);
        tvFollowCount.setText("128.6万人追番");

        //简介
        TextView tvDesc = findViewById(R.id.tv_desc);
        tvDesc.setText("这是番剧简介，用来占位。这里会介绍番剧的剧情、声优、制作团队等信息。");

        //追番按钮

        // 追番按钮：在“追番”和“已追番”之间切换
        findViewById(R.id.btn_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                if ("追番".contentEquals(btn.getText())) {
                    btn.setText("已追番");
                } else {
                    btn.setText("追番");
                }
            }
        });

        //集数列表：横向滚动
        RecyclerView rvEpisode = findViewById(R.id.rv_episode);
        rvEpisode.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvEpisode.setAdapter(new EpisodeAdapter(12, title));
    }
}
