package com.example.bilibili.widget.bottombar;

/**
 * CustomTabEntity的默认实现
 * 构造时传入标题、选中图标、未选中图标，三个字段直接存起来
 */
public class TabEntity implements CustomTabEntity{

    public String title; // Tab 文字
    public int selectedIcon; // 选中图标资源Id
    public int unSelectedIcon; // 未选中图标资源Id

    public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }
    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnSelectedIcon() {
        return unSelectedIcon;
    }
}
