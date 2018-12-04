package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PrivateChatRoomActivity extends ChatRoomActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Uri uri = getIntent().getData();
        if(uri != null ){
            Log.d("URI",uri.toString());
            nombreChat= Uri.decode(uri.getQueryParameter("chat"));
            Log.d("ChatRoomName" , nombreChat);
            tipoChat="chat_privado";
            chatRoomName= uri.getQueryParameter("id");
            Log.d("Nombre " , chatRoomName);
            // Como se supone que el link es para que la gente se una a una sala de chat
            HashMap<String, Object> data = new HashMap<String,Object>();
            data.put("chatId",chatRoomName);
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid())
                    .collection("chats_privados")
                    .add(data);
        }
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.chat_room_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default: {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                //https://chatapp-test-4e669.firebaseapp.com/invite?chat=Los Pibes&id=yqg0mbyR2nUIpLMYqCds
                String url = "https://chatapp-test-4e669.firebaseapp.com/invite?chat="+Uri.encode(nombreChat)+"&id="+chatRoomName;
                shareIntent.putExtra(Intent.EXTRA_TEXT , "Unete a mi chat privado en Firebase Chat App : "+ url );
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Chat Invite");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Invitar con: "));

            }
        }
        return super.onOptionsItemSelected(item);
    }
}
