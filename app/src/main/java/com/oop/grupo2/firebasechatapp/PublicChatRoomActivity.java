package com.oop.grupo2.firebasechatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class PublicChatRoomActivity extends ChatRoomActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle arg = getIntent().getExtras();
        chatRoomName = arg.getString(CHAT_ROOM_NAME);
        tipoChat = arg.getString(TIPO_CHAT_ROOM);
        nombreChat =  arg.getString(NOMBRE_DEL_CHAT);
        super.onCreate(savedInstanceState);
    }
}
