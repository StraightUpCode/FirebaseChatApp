package com.oop.grupo2.firebasechatapp;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    public String uID;
    public String message;
    public Date datetime;
    public String nickname;
    private MessageListener listener;
    public Message(){

    }
    public Message(String uID, String message ){
        this(uID , "" , message);
    }
    public Message(String uID, String username, String message){
        this.nickname = username;
        this.uID = uID;
        this.message = message;
    }


    @ServerTimestamp
    public Date getDatetime() { return datetime; }
    public void setDatetime(Date mDatetime) { datetime = mDatetime; }
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
        return  this.nickname;
    }
    public String getNickname(){ return this.nickname; }
    public void setUsername(String username) {
        this.nickname = username;
    }
    public void addOnUserNameLoaded(MessageListener listener){
        this.listener = listener;
    }
    public void setMessage(String content){ this.message = content;}
}
