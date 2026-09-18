package com.example.bilibili.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bilibili.R;
import com.example.bilibili.ui.bangumi.BangumiFragment;
import com.example.bilibili.ui.column.ColumnFragment;
import com.example.bilibili.ui.live.LiveFragment;
import com.example.bilibili.ui.recommend.RecommendFragment;
import com.example.bilibili.widget.bottombar.PlaceholderFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 首页Fragment
 * 顶部是 ToolBar + TabLayout，下面用ViewPager2承载五个页面
 * 五个子Tab：直播 / 推荐 / 追番 / 影视 / 专栏
 */
public class MainFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private String[] mTitles; // 五个子Tab的标题

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //加载fragment_main.xml
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    //onViewCreated：onCreateView执行完、拿到 rootView 之后立刻回调。
    //此时：布局 View 已经创建完毕，可以安全执行 findViewById、设置适配器、绑定 TabLayoutMediator，初始化控件逻辑。
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //拿到五个标题：直播 / 推荐 / 追番 / 影视 / 专栏
        mTitles = getResources().getStringArray(R.array.home_sections);

        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.viewpager);
        setupDrawerToggle(view);

        //给ViewPager2设置adapter
        mViewPager.setAdapter(new HomePagerAdapter(this));

        //用TabLayoutMediator把TabLayout和ViewPager2绑定起来
        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                //给每个Tab设置标题
                tab.setText(mTitles[i]);
            }
        }).attach();

        mViewPager.setCurrentItem(1, false);
    }

    //首页子页面的适配器：按位置返回对应的子页面
    private class HomePagerAdapter extends FragmentStateAdapter {

        public HomePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            //第一个子Tab 是直播列表页，其余仍用占位页
            if(position == 0) {
                return new LiveFragment();
            }
            else if (position == 1) { //第二个子Tab时 推荐
                return new RecommendFragment();
            }
            else if(position == 2) {
                return BangumiFragment.newInstance(1);//追番
            } else if (position == 3) {
                return BangumiFragment.newInstance(2); //影视
            } else if (position == 4) {
                return new ColumnFragment(); // 专栏
            }

            return PlaceholderFragment.newInstance(mTitles[position]);//专栏
        }

        @Override
        public int getItemCount() {
            return mTitles.length;
        }
    }

    //让顶部的“三横线”按钮打开左侧抽屉
    private void setupDrawerToggle(View view) {
        View drawerToggle = view.findViewById(R.id.ll_top_menu_nav);
        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DrawerLayout在MainActivity里，所以通过activity拿
                DrawerLayout drawer = requireActivity().findViewById(R.id.main_drawer_layout);
                if(drawer != null) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }
}
