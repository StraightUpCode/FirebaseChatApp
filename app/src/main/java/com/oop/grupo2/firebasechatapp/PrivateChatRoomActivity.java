package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PrivateChatRoomActivity extends ChatRoomActivity implements ExitChatDialog.DeleteChatInterface{
    FirebaseUser user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Uri uri = getIntent().getData();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(uri != null ){
            Log.d("URI",uri.toString());
            nombreChat= Uri.decode(uri.getQueryParameter("chat"));
            Log.d("ChatRoomName" , nombreChat);
            tipoChat="chat_privado";
            chatRoomName= uri.getQueryParameter("id");
            Log.d("Nombre " , chatRoomName);
            // Como se supone que el link es para que la gente se una a una sala de chat
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(user.getUid())
                    .collection("chats_privados")
                    .document(chatRoomName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> response = documentSnapshot.getData();
                            if(response == null && response.get("chatId") == null ) return;
                            if(! response.get("chatId").toString().equals(chatRoomName)){
                                HashMap<String, Object> data = new HashMap<String,Object>();
                                data.put("chatId",chatRoomName);
                                db
                                .collection("users")
                                .document(user.getUid())
                                .collection("chats_privados")
                                .add(data);
                            }
                        }
                    });


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
            case R.id.share_chat_room: {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                //https://chatapp-test-4e669.firebaseapp.com/invite?chat=Los Pibes&id=yqg0mbyR2nUIpLMYqCds
                String url = "https://chatapp-test-4e669.firebaseapp.com/invite?chat="+Uri.encode(nombreChat)+"&id="+chatRoomName;
                shareIntent.putExtra(Intent.EXTRA_TEXT , "Unete a mi chat privado en Firebase Chat App : "+ url );
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Chat Invite");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Invitar con: "));
                break;
            }
            case R.id.leaveChat : {
                DialogFragment newFragment = new ExitChatDialog();
                ((ExitChatDialog) newFragment).addChatId(chatRoomName)
                        .addUserId(user.getUid())
                        .addDeleteListerner(this);
                newFragment.show(getSupportFragmentManager(),"Exit");
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnDeleteChat() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
          //Si Viene de otra App, lo regresa a esa app
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            //Regresa al Home
            NavUtils.navigateUpTo(this, upIntent);
        }

    }
}
