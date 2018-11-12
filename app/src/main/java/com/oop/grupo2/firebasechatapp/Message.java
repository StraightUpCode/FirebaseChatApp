package com.oop.grupo2.firebasechatapp;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Message {
    public String uID;
    public String message;
    public Date datetime;
    private String username;
    private MessageListener listener;
    private int posicion;
    public Message(){

    }
    public Message(String uID, String message, Date datetime){
        this.uID = uID;
        this.message = message;
        this.datetime = datetime;
    }


    public Date getDatetime() {
        return datetime;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public String getuID() {
        return uID != null ? uID : "";
    }
    public void fetchUserName(){
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String username = documentSnapshot.getData().get("nickname").toString();
                                setUsername(username);
                                listener.OnUserNameLoaded();
                            }

                    }
                });

    }
    public String returnUsername(){
        return  this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void newPosition(int position){
        this.posicion = position;
    }
    public void addOnUserNameLoaded(MessageListener listener){
        this.listener = listener;
    }
}
