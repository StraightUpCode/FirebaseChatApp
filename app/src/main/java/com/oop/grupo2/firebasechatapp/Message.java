package com.oop.grupo2.firebasechatapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.google.firestore.v1beta1.DocumentTransform;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    public String uID;
    public String message;
    public Timestamp  datetime;
    public Message(){

    }
    public Message(String UID, String Content  ){
        datetime = new Timestamp(new Date());
        message = Content;
        uID = UID;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public String getMessage() {
        return message;
    }

    public String getuID() {
        return uID;
    }
}
