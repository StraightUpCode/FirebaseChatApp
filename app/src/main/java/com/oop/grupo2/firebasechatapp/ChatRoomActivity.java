package com.oop.grupo2.firebasechatapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
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

    private String chatRoomName;
    private String tipoChat;
    private User myUser;
    private Button submitButton;
    private EditText messageContent;
    private RecyclerView messageRecyclerView;
    private FirebaseFirestore db ;
    private FirestoreRecyclerAdapter<Message , MessageViewHolder > FirestoreRecycler ;

    // Cuando se crea la actividad

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.chat_room_layout);
        Bundle arg = getIntent().getExtras();
        chatRoomName = arg.getString("CHAT_ROOM_NAME");
        tipoChat = arg.getString("TIPO_CHAT");
        submitButton = findViewById(R.id.submit);
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
        setUpRecyclerView();
        //Envio de Mensajes
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Message msg = new Message(userId,"lol", messageContent.getText().toString());
                db.collection("chat_publico")
                        .document("chat_general")
                        .collection("chat_msg")
                        .add(msg);
                messageContent.setText("");*/
                Message msg = new Message(myUser.userId, myUser.userName,messageContent.getText().toString());
                db.collection(tipoChat)
                        .document(chatRoomName)
                        .collection("chat_msg")
                        .add(msg);
                messageContent.setText("");

            }
        });
    }

    /*
    Una ves construida la vista, se procepe a capturar los elementos de la vista
    para agregar los Event Listeners
    y el Set Up para el Recycler View donde se veran los mensajes recibidos y enviados
     */


    public void setUpRecyclerView(){
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(mLayoutManager);

        Query query = this.db.collection(tipoChat)
                .document(chatRoomName)
                .collection("chat_msg")
                .orderBy("datetime",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query,Message.class)
                .build();
        FirestoreRecycler = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull ChatRoomActivity.MessageViewHolder holder, int position, @NonNull Message model) {
                if(model.getMessage() != null) holder.message.setText(model.getMessage());
                if(model.getDatetime() != null)holder.timestamp.setText(model.getDatetime().toString());
                if( model.getuID() != null) holder.nickname.setText(model.returnUsername());
                holder.nickname.setVisibility(TextView.VISIBLE);
                holder.timestamp.setVisibility(TextView.VISIBLE);
                holder.message.setVisibility(TextView.VISIBLE);
            }

            @NonNull
            @Override
            public ChatRoomActivity.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messate_item,parent,false);
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
        messageRecyclerView.setAdapter(FirestoreRecycler);


    }


}
