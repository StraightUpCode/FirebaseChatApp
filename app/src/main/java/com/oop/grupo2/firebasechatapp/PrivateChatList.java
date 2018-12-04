package com.oop.grupo2.firebasechatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PrivateChatList extends ChatListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void getData() {
       final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query reference =  db.collection("users")
                .document(currentUser.getUid())
                .collection("chats_privados");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e == null) return;

                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Map<String, Object> map = (HashMap) documentSnapshot.getData();
                    if(map == null && map.get("chatId") == null) return;
                    fetchChatRoom(db,map.get("chatId").toString());

                }
            }
        });

        reference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            Map<String, Object> data = (HashMap)documentSnapshot.getData();
                            String id = data.get("chatId").toString();
                            fetchChatRoom(db,id);

                        }


                    }

                });
    }

    public void fetchChatRoom(FirebaseFirestore db , String id){
        db.collection("chat_privado")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Map<String , Object> data =  (HashMap)documentSnapshot.getData();
                        Map<String,Object> last_message = (HashMap)data.get("last_message");
                        Message msg = new Message();
                        if(last_message != null){
                            msg.setUsername(last_message.get("nickname").toString());
                            msg.setMessage(last_message.get("message").toString());
                        }
                        ChatRoom chatRoom = new ChatRoom(documentSnapshot.getId(), data.get("chatName").toString(),"chat_privado" ,msg);

                        chatRoom.addLastMessageUpdater(new UpdateMessage() {
                            @Override
                            public void onMessageUpdated(ChatRoom chatRoom) {
                                int oldIndex  = dataset.indexOf(chatRoom);
                                dataset.remove(oldIndex);
                                dataset.add(0,chatRoom);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        // Se agrega al dataset
                        dataset.add(chatRoom);
                        adapter.notifyDataSetChanged();

                    }
                });
    }
}
