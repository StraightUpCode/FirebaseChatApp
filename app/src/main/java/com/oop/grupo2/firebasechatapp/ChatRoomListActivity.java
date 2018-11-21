package com.oop.grupo2.firebasechatapp;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatRoomListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager=  (ViewPager) findViewById(R.id.viewPage_id);

        TabManager tabManager = new TabManager(getSupportFragmentManager());
        viewPager.setAdapter(tabManager);
        tabLayout.setupWithViewPager(viewPager);

    }
}
