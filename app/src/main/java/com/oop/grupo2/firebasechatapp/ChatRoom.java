package com.oop.grupo2.firebasechatapp;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class ChatRoom {
    private String chat_id;
    private Message last_message;
    private UpdateMessage listener;

    ChatRoom(){

    }

    public ChatRoom(String id, Message msg) {
        chat_id = id;
        last_message = msg;

        //Escucha si el documento ha recibido una actualiza AKA si alguien envio un mensaje
        // Cuando se actualiza la abse de datos, aqui se recepcion y se actualiza el "last_message"
        // El cual hace un trigger al para que se actualice en la vista
        FirebaseFirestore.getInstance().collection("chat_publico")
                    .document(id)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                return ;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                Message newMessage = documentSnapshot.toObject(Message.class);
                                if(newMessage.notNull()){
                                    last_message = newMessage;
                                    listener.onMessageUpdated();
                                }


                            }
                        }
                    });
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }

    public Message getLast_message() {
        return last_message;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void addLastMessageUpdater(UpdateMessage listener){
        this.listener = listener;
    }
}
