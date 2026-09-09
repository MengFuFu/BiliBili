package com.example.bilibili.ui.live.liveplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bilibili.R;
import com.example.bilibili.ui.live.liveplay.fragment.LiveDanmuFragment;
import com.example.bilibili.widget.bottombar.PlaceholderFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 直播播放页骨架：
 * 从上一个页面接收标题、主播名、在线人数并显示
 */
public class LivePlayActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_ONLINE = "extra_online";

    private static final String TEST_URL =  "https://media.w3.org/2010/05/sintel/trailer.mp4";
    private ExoPlayer mPlayer;
    private boolean mIsFullscreen = false;
    private int mPlayerNormalHeight;

    //控制区
    private View mControlBar;
    private ProgressBar mLoading;
    private Runnable mHideControlRunnable;
    //进度条
    private SeekBar mSeekBar;
    private Runnable mProgressRunnable;
    private boolean mIsUserSeeking = false;
    //倍速和音量
    private SeekBar mVolumeSeekBar;
    private TextView mBtnSpeed;
    private final float[] mSpeeds = {1.0f, 1.25f, 1.5f, 2.0f};
    private int mSpeedIndex = 0;


    //外部统一用这个方法跳转进来
    public static void startActivity(Context context, String title, String userName, int online) {
        Intent intent = new Intent(context, LivePlayActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_USER_NAME, userName);
        intent.putExtra(EXTRA_ONLINE, online);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_live_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mPlayerNormalHeight = dp2px(200);

        //取出上个页面传递进来的数据
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        int online = getIntent().getIntExtra(EXTRA_ONLINE, 0);

        //显示数据
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvOnline = findViewById(R.id.tv_online);
        tvTitle.setText(title);
        tvUserName.setText(userName);
        tvOnline.setText("在线 " + online);

        //返回按钮
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTabs();
        initAttention();
        initPlayer();
        initControls();
    }

    //初始化底部四个Tab，先用占位页
    private void initTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        final String[] titles = getResources().getStringArray(R.array.live_play);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                //第一个Tab是弹幕列表，其他仍用占位页
                if(position == 0) {
                    return new LiveDanmuFragment();
                }
                return PlaceholderFragment.newInstance(titles[position]);
            }

            @Override
            public int getItemCount() {
                return titles.length;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                tab.setText(titles[i]);
            }
        }).attach();
    }

    private void initAttention() {
        findViewById(R.id.btn_attention).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                if("+关注".contentEquals(btn.getText())) {
                    btn.setText("已关注");
                }
                else {
                    btn.setText("+关注");
                }
            }
        });
    }

    private void initControls() {
        mControlBar = findViewById(R.id.control_bar);
        mLoading = findViewById(R.id.loading);

        //点播放器区域，显示/隐藏控制条
        findViewById(R.id.player_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlBar();
            }
        });
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });
        findViewById(R.id.btn_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });

        //监听播放状态：缓冲时显示加载图，播放、暂停时同步按钮文字
        mPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                mLoading.setVisibility(playbackState == Player.STATE_BUFFERING ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                ((TextView) findViewById(R.id.btn_play)).setText(isPlaying ? "暂停" : "播放");
            }
        });

        //一开始显示控制条，并开始倒计时隐藏
        showControlBar();

        //初始化进度条
        initSeekBar();
        //初始化音量和倍速
        initVolumeAndSpeed();
    }

    private void toggleControlBar() {
        if(mControlBar.getVisibility() == View.VISIBLE) {
            hideControlBarNow();
        }
        else {
            showControlBar();
        }
    }
    private void showControlBar() {
        mControlBar.setVisibility(View.VISIBLE);
        removeHideCallback();
        mHideControlRunnable = new Runnable() {
            @Override
            public void run() {
                hideControlBarNow();
            }
        };
        mControlBar.postDelayed(mHideControlRunnable, 3000);
    }
    private void hideControlBarNow() {
        mControlBar.setVisibility(View.GONE);
        removeHideCallback();
    }
    private void removeHideCallback() {
        if(mHideControlRunnable != null) {
            mControlBar.removeCallbacks(mHideControlRunnable);
            mHideControlRunnable = null;
        }
    }

    private void togglePlay() {
        if(mPlayer == null) {
            return;
        }
        if(mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        else {
            mPlayer.play();
        }
    }
    private void toggleFullscreen() {
        mIsFullscreen = !mIsFullscreen;
        View playerContainer = findViewById(R.id.player_container);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) playerContainer.getLayoutParams();

        if(mIsFullscreen) {
            setNormalViewsVisible(false);
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            ((TextView) findViewById(R.id.btn_fullscreen)).setText("退出全屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setNormalViewsVisible(true);
            lp.height = mPlayerNormalHeight;
            ((TextView) findViewById(R.id.btn_fullscreen)).setText("全屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        playerContainer.setLayoutParams(lp);
    }
    private void setNormalViewsVisible(boolean visible) {
        int v = visible ? View.VISIBLE : View.GONE;
        findViewById(R.id.top_bar).setVisibility(v);
        findViewById(R.id.tv_user_name).setVisibility(v);
        findViewById(R.id.tv_online).setVisibility(v);
        findViewById(R.id.tab_layout).setVisibility(v);
        findViewById(R.id.view_pager).setVisibility(v);
    }

    //初始化ExoPlayer并播放一个测试视频
    private void initPlayer() {
        PlayerView playerView = findViewById(R.id.player_view);

        mPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(mPlayer);

        mPlayer.setMediaItem(MediaItem.fromUri(TEST_URL));
        mPlayer.prepare();
        mPlayer.setPlayWhenReady(true);
    }

    //初始化进度条
    private void initSeekBar() {
        mSeekBar = findViewById(R.id.seek_bar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //拖动过程
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserSeeking = true; //用户开始拖动，暂停自动更新
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserSeeking = false;
                if(mPlayer != null) {
                    mPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        startProgressUpdates();
    }

    private void startProgressUpdates() {
        stopProgressUpdates();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer != null && !mIsUserSeeking && mPlayer.getDuration() > 0) {
                    mSeekBar.setMax((int) mPlayer.getDuration());
                    mSeekBar.setProgress((int) mPlayer.getCurrentPosition());
                }
                mSeekBar.postDelayed(this, 50);
            }
        };
        mSeekBar.postDelayed(mProgressRunnable, 50);
    }

    private void stopProgressUpdates() {
        if(mProgressRunnable != null) {
            mSeekBar.removeCallbacks(mProgressRunnable);
            mProgressRunnable = null;
        }
    }

    //初始化音量和倍速
    private void initVolumeAndSpeed() {
        mVolumeSeekBar = findViewById(R.id.volume_seek_bar);
        mVolumeSeekBar.setMax(100);
        mVolumeSeekBar.setProgress(100);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && mPlayer != null) {
                    mPlayer.setVolume(progress / 100f); // ExoPlayer 音量范围是 0~1
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnSpeed = findViewById(R.id.btn_speed);
        mBtnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //循环切换：1.0 -> 1.25 -> 1.5 -> 2.0 -> 1.0
                mSpeedIndex = (mSpeedIndex + 1) % mSpeeds.length;
                float speed = mSpeeds[mSpeedIndex];
                if(mPlayer != null) {
                    mPlayer.setPlaybackSpeed(speed);
                }
                mBtnSpeed.setText(speed + "x");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer != null) {
            mPlayer.setPlayWhenReady(false); //离开页面时暂停
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPlayer != null) {
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        if(mPlayer != null) {
            mPlayer.release(); //释放播放器
            mPlayer = null;
        }
        removeHideCallback();
        stopProgressUpdates();
        super.onDestroy();
    }

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
