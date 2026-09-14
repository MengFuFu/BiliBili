package com.example.bilibili;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bilibili.ui.main.MainFragment;
import com.example.bilibili.ui.region.RegionFragment;
import com.example.bilibili.widget.bottombar.BottomBar;
import com.example.bilibili.widget.bottombar.CustomTabEntity;
import com.example.bilibili.widget.bottombar.PlaceholderFragment;
import com.example.bilibili.widget.bottombar.TabEntity;

import java.util.ArrayList;
import com.example.bilibili.ui.main.MainFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * 主页Activity
 * 用 ViewPager2 承载四个一级页面，底栏和 ViewPager2 双向同步：
 *  点底栏 -> ViewPager2 滑到对应页0l.]]
 *
 *
 *
 *    /
 *
 *  手动左右滑 -> 底栏高亮跟着变
 */
public class MainActivity extends AppCompatActivity {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private Fragment[] mFragments = new Fragment[4]; //四个一级标题
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>(); //底栏数据
    private String[] mTitles; //四个标题
    private int[] mIconSelectIds = { //选中图标
            R.drawable.ic_home_selected, R.drawable.ic_category_selected,
            R.drawable.ic_dynamic_selected, R.drawable.ic_communicate_selected
    };
    private int[] mIconUnSelectIds = { //选中图标
            R.drawable.ic_home_unselected, R.drawable.ic_category_unselected,
            R.drawable.ic_dynamic_unselected, R.drawable.ic_communicate_unselected
    };
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initDrawer();
        initFragments();
        initBottomBar(); //初始化底栏
    }

    //初始化抽屉：菜单项被点击时关闭抽屉
    private void initDrawer() {
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mNavigationView = findViewById(R.id.main_nav_view);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //抽屉开着时，按返回键先关抽屉，而不是直接退出
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    //创建四个一级页面，全部add到main_container，先只显示第一个
    private void initFragments() {
        mFragments[FIRST] = new MainFragment();
        mFragments[SECOND] = new RegionFragment();
        mFragments[THIRD] = PlaceholderFragment.newInstance(getString(R.string.section_dynamic));
        mFragments[FOURTH] = PlaceholderFragment.newInstance(getString(R.string.section_message));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.length; i++) {
            ft.add(R.id.main_container, mFragments[i]);
            if(i != FIRST) {
                ft.hide(mFragments[i]);
            }
        }
        ft.commit();
    }

    //初始化底栏：组装四个Tab数据
    private void initBottomBar() {
        mBottomBar = findViewById(R.id.entrance_bar);
        mTitles = getResources().getStringArray(R.array.main_sections);

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        mBottomBar.setTabEntities(mTabEntities);

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                switchFragment(position, prePosition);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    //切换页面：显示选中的，隐藏上一个
    private void switchFragment(int showPosition, int hidePosition) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(mFragments[showPosition]);
        ft.hide(mFragments[hidePosition]);
        ft.commit();
    }
}