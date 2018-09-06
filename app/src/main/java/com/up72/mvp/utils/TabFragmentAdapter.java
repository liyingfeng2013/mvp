package com.up72.mvp.utils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.List;

public class TabFragmentAdapter implements TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private FragmentManager fragmentManager;
    private List<Fragment> fragments;
    private int contentId;

    public TabFragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments, @IdRes int tabId, @IdRes int contentId) {
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
        this.fragments = fragments;
        this.contentId = contentId;
        this.tabLayout = (TabLayout) fragmentActivity.findViewById(tabId);
        this.tabLayout.addOnTabSelectedListener(this);
    }

    public TabFragmentAdapter(@NonNull FragmentActivity fragmentActivity, @IdRes int tabId, @IdRes int contentId) {
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
        this.contentId = contentId;
        this.tabLayout = (TabLayout) fragmentActivity.findViewById(tabId);
        this.tabLayout.addOnTabSelectedListener(this);
    }

    public int getItemSize() {
        return fragments == null ? 0 : fragments.size();
    }

    public void replaceAll(@NonNull List<Fragment> fragments, @NonNull List<String> titles) {
        int titlesSize = titles.size();
        int fragmentsSize = fragments.size();
        if (titlesSize > fragmentsSize) {
            titles = titles.subList(0, fragmentsSize);
        } else if (titlesSize < fragmentsSize) {
            fragments = fragments.subList(0, titlesSize);
        }
        tabLayout.removeAllTabs();
        int size = titles.size();
        this.fragments = fragments;
        for (int i = 0; i < size; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles.get(i)), i == 0);
        }
    }

    public void addAll() {
    }

    public void addTab(View view) {
        addTab(view, false);
    }

    public void addTab(View view, boolean selected) {
        tabLayout.addTab(tabLayout.newTab().setCustomView(view), selected);
    }

    public void addTab(CharSequence charSequence) {
        addTab(charSequence, false);
    }

    public void addTab(CharSequence charSequence, boolean selected) {
        tabLayout.addTab(tabLayout.newTab().setText(charSequence), selected);
    }

    public void selectTab(int index) {
        if (tabLayout != null && index > -1 && index < tabLayout.getTabCount()) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                tab.select();
            }
        }
    }

    public int getTabPosition() {
        return tabLayout.getSelectedTabPosition();
    }

    public Fragment getCurrentFragment() {
        return fragments.get(tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (fragments != null) {
            int position = tab.getPosition();
            if (position < fragments.size()) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = fragments.get(position);
                if (f.isAdded()) {
                    transaction.show(f);
                } else {
                    transaction.add(contentId, f);
                }
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (fragments != null) {
            int position = tab.getPosition();
            if (position < fragments.size()) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment f = fragments.get(position);
                transaction.hide(f);
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}