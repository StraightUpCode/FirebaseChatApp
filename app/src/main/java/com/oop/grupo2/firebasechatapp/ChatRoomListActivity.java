package com.oop.grupo2.firebasechatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.support.v4.content.ContextCompat.getSystemService;

public class ChatRoomListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.createNotificationChannel(this);
        if( user == null) {
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

    public static void createNotificationChannel( Context ctx){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FirebaseChatApp";
            String description = "Nuevo Mensaje";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("firebase_notificacion",name,importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService( NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
