package com.oop.grupo2.firebasechatapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabManager extends FragmentPagerAdapter {

    TabManager(FragmentManager fm){
        super(fm);

    }

    @Override
    public Fragment getItem(int i) {
        return new ChatListFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return  0 == position ? "Chats Publicos" : "Chats Privados";
    }


}
