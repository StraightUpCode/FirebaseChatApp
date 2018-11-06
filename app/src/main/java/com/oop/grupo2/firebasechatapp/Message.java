package com.oop.grupo2.firebasechatapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {
    public String uID;
    public String message;
    public String datetime;
    public Message(){

    }
    public Message(String UID, String Content  ){
        datetime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        message = Content;
        uID = UID;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getMessage() {
        return message;
    }

    public String getuID() {
        return uID;
    }
}
