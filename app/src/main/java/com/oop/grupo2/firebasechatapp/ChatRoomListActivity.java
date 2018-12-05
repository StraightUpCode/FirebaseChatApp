package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class ChatRoomListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        setContentView(R.layout.activity_chat_room_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager=  (ViewPager) findViewById(R.id.viewPage_id);

        TabManager tabManager = new TabManager(getSupportFragmentManager());
        viewPager.setAdapter(tabManager);
        tabLayout.setupWithViewPager(viewPager);

    }
}
