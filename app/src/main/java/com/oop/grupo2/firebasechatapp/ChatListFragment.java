package com.oop.grupo2.firebasechatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment{

    ChatListAdapter adapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.chatList);
        final ArrayList<ChatRoom> dataset = new ArrayList<ChatRoom>() ;

        adapter = new ChatListAdapter(dataset);

        FirebaseFirestore.getInstance()
                .collection("chat_publico")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Map<String,Object> map = (HashMap)documentSnapshot.getData().get("last_message");
                            Message msg = new Message();
                            msg.setUsername((String) map.get("nickname"));
                            msg.setMessage((String) map.get("message"));
                            ChatRoom chat_room = new ChatRoom(documentSnapshot.getId(),msg);
                            chat_room.addLastMessageUpdater(new UpdateMessage() {
                                @Override
                                public void onMessageUpdated() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            dataset.add(chat_room);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);

    }


}
