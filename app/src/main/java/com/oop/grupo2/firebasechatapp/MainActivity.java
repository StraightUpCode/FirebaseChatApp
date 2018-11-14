package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class MainActivity extends AppCompatActivity implements MessageListener {

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
    private String userId = "";
    public Button submitButton;
    public  EditText messageText;
    private Query msgReferences;
    private FirestoreRecyclerAdapter<Message,MessageViewHolder> mFirestoreAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null){
            startActivity(new Intent(this, SignInActivity.class));

            finish();
        }else{

            //AQUI ESTA LA BASE DE DATOS DOG!
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            userId = user.getUid();
            final RecyclerView mRecyclerView = findViewById(R.id.messageRecyclerView);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLayoutManager);


            Query msgReferences = db.collection("chat_publico")
                    .document("chat_general")
                    .collection("chat_msg")
                    .orderBy("datetime",Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                    .setQuery(msgReferences,Message.class)
                    .build();
            mFirestoreAdapter = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options){
                @Override
                protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
                    model.addOnUserNameLoaded(MainActivity.this);
                    model.fetchUserName();
                    if(model.getMessage() != null) holder.message.setText(model.getMessage());
                    if(model.getDatetime() != null)holder.timestamp.setText(model.getDatetime().toString());
                    if( model.getuID() != null) holder.nickname.setText(model.returnUsername());
                    holder.nickname.setVisibility(TextView.VISIBLE);
                    holder.timestamp.setVisibility(TextView.VISIBLE);
                    holder.message.setVisibility(TextView.VISIBLE);
                }

                @NonNull
                @Override
                public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messate_item,parent,false);
                    return new MessageViewHolder(view);
                }
            };
            mFirestoreAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFirestoreAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLayoutManager.findLastVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });
            mFirestoreAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mFirestoreAdapter);
            submitButton = findViewById(R.id.submit);
            messageText = findViewById(R.id.message);


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sign_out){
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userId = "";
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirestoreAdapter.startListening();
    }

    @Override
    public void OnUserNameLoaded() {
        mFirestoreAdapter.notifyDataSetChanged();
    }
}
