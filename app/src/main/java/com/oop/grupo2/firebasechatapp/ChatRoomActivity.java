package com.oop.grupo2.firebasechatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
    public static String CHAT_ROOM_ID = "CHAT_ROOM_ID";
    public static String TIPO_CHAT_ROOM = "TIPO_CHAT";
    public static String NOMBRE_DEL_CHAT = "NOMBRE_DEL_CHAT";

    protected String chatRoonID;
    protected String tipoChat;
    protected String nombreChat;
    protected User myUser;
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
        if(chatRoonID == null && tipoChat == null && nombreChat == null){
            Bundle arg = getIntent().getExtras();
            chatRoonID = arg.getString(CHAT_ROOM_ID);
            tipoChat = arg.getString(TIPO_CHAT_ROOM);
            nombreChat =  arg.getString(NOMBRE_DEL_CHAT);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Log.d("Pre-Actionbar-Nombre",nombreChat);
        if(nombreChat != null) actionBar.setTitle(nombreChat);

        actionBar
                .setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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
                .document(chatRoonID)
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
                            .document(chatRoonID)
                            .collection("chat_msg")
                            .add(msg);
                    messageContent.setText("");
                }
            }
        });
    }

}
