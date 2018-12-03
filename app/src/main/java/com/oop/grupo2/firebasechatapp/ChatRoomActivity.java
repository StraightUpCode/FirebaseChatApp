package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.net.URI;
import java.util.HashMap;

public class ChatRoomActivity extends AppCompatActivity {
    // Constructor de la Vista de los Mensajes
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView nickname;
        TextView timestamp;

        public MessageViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.messageContent);
            nickname = (TextView) v.findViewById(R.id.userName);
            timestamp = (TextView) v.findViewById(R.id.timestamp);
        }
    }

    // Inicio de la Clase ChatRoomFragment
    public static String CHAT_ROOM_NAME = "CHAT_ROOM_NAME";
    public static String TIPO_CHAT_ROOM = "TIPO_CHAT";
    public static String NOMBRE_DEL_CHAT = "NOMBRE_DEL_CHAT";

    private String chatRoomName;
    private String tipoChat;
    private String nombreChat;
    private User myUser;
    private Button submitButton;
    private EditText messageContent;
    private RecyclerView messageRecyclerView;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Message, MessageViewHolder> FirestoreRecycler;

    // Cuando se crea la actividad

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_layout);
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

        }else{
            Bundle arg = getIntent().getExtras();
            chatRoomName = arg.getString(CHAT_ROOM_NAME);
            tipoChat = arg.getString(TIPO_CHAT_ROOM);
            nombreChat =  arg.getString(NOMBRE_DEL_CHAT);
        }

        ActionBar actionBar = getSupportActionBar();
        Log.d("Pre-Actionbar-Nombre",nombreChat);
        if(nombreChat != null) actionBar.setTitle(nombreChat);

        getSupportActionBar()
                .setDefaultDisplayHomeAsUpEnabled(true);
        submitButton = findViewById(R.id.submitMessage);
        submitButton.setEnabled(false);
        messageContent = findViewById(R.id.messageEditText);
        messageRecyclerView = findViewById(R.id.chatRoomRecycler);
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myUser = new User(user.getUid());
        myUser.fetchUsername(new FetchUsernameListener() {
            @Override
            public void onFetched() {
                submitButton.setEnabled(true);
            }
        });

        //Hace el Setup para el Recycler View
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(mLayoutManager);

        Query query = this.db.collection(tipoChat)
                .document(chatRoomName)
                .collection("chat_msg")
                .orderBy("datetime", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        FirestoreRecycler = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatRoomActivity.MessageViewHolder holder, int position, @NonNull Message model) {
                if (model.getMessage() != null) holder.message.setText(model.getMessage());
                if (model.getDatetime() != null)
                    holder.timestamp.setText(model.getDatetime().toString());
                if (model.getuID() != null) holder.nickname.setText(model.returnUsername());
                holder.nickname.setVisibility(TextView.VISIBLE);
                holder.timestamp.setVisibility(TextView.VISIBLE);
                holder.message.setVisibility(TextView.VISIBLE);


            }

            @NonNull
            @Override
            public ChatRoomActivity.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messate_item, parent, false);
                return new ChatRoomActivity.MessageViewHolder(view);
            }
        };


        FirestoreRecycler.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = FirestoreRecycler.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        FirestoreRecycler.notifyDataSetChanged();
        FirestoreRecycler.startListening();
        messageRecyclerView.setAdapter(FirestoreRecycler);
        messageRecyclerView.addItemDecoration(new DividerItemDecoration(messageRecyclerView.getContext() , 1));
        //Envio de Mensajes
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_message =  messageContent.getText().toString().trim();
                if(! msg_message.isEmpty()){
                    Message msg = new Message(myUser.userId, myUser.userName, msg_message);
                    db.collection(tipoChat)
                            .document(chatRoomName)
                            .collection("chat_msg")
                            .add(msg);
                    messageContent.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(tipoChat.equals("chat_privado")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.chat_room_menu, menu);
        }
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
