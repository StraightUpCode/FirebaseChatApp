package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

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
            chatRoonID = uri.getQueryParameter("id");
            Log.d("Nombre " , chatRoonID);
            // Como se supone que el link es para que la gente se una a una sala de chat
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query ref = db.collection("users")
                    .document(user.getUid())
                    .collection("chats_privados");

            ref
            .whereEqualTo("chatId", chatRoonID)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult() == null){


                                }
                                QuerySnapshot querySnapshot = task.getResult();
                                if(querySnapshot.isEmpty()){
                                    HashMap<String,Object> data = new HashMap<>();
                                    data.put("chatId", chatRoonID);
                                    db.collection("users")
                                            .document(user.getUid())
                                            .collection("chats_privados")
                                            .add(data);
                                }


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
                String url = "https://chatapp-test-4e669.firebaseapp.com/invite?chat="+Uri.encode(nombreChat)+"&id="+ chatRoonID;
                shareIntent.putExtra(Intent.EXTRA_TEXT , "Unete a mi chat privado en Firebase Chat App : "+ url );
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Chat Invite");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Invitar con: "));
                break;
            }
            case R.id.leaveChat : {
                DialogFragment newFragment = new ExitChatDialog();
                ((ExitChatDialog) newFragment).addChatId(chatRoonID)
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
