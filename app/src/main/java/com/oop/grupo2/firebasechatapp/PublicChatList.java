package com.oop.grupo2.firebasechatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PublicChatList extends ChatListFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("chat_publico")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Se obtienen todas las salas de chat
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            //Se obtiene el ultimo mensaje como un Map
                            Map<String,Object> map = (HashMap)documentSnapshot.getData().get("last_message");
                            Message msg = new Message(); // Se crea una instancia de un mensaje
                            //Se asigna el Nombre del Usuario que lo Mando y El Mensaje como tal
                            if(map != null){
                                msg.setUsername((String) map.get("nickname"));
                                msg.setMessage((String) map.get("message"));
                            }
                            // Se crea un objeto ChatRoom que contiene el "Id" del Chat y el Ultimo mensaje
                            ChatRoom chat_room = new ChatRoom(documentSnapshot.getId(),documentSnapshot.getData().get("chatName").toString(),"chat_publico",msg);
                            // Como este ultimo mensaje puede cambiar tiene un listener actualizar los datos en caso de que se reciba un nuevo mensaje
                            chat_room.addLastMessageUpdater(new UpdateMessage() {
                                @Override
                                public void onMessageUpdated(ChatRoom chatRoom) {
                                    int oldIndex  = dataset.indexOf(chatRoom);
                                    dataset.remove(oldIndex);
                                    dataset.add(0,chatRoom);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            // Se agrega al dataset
                            dataset.add(chat_room);
                        }
                        //Se le notifica al adapter que se modificaron los cambios
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
