package com.oop.grupo2.firebasechatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;



public class CreatePrivateChat extends DialogFragment {

    private EditText nombre;
    private Button submit;
    private TextView errormsg;

    public CreatePrivateChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_private_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.chatCreate);
        nombre = view.findViewById(R.id.chatNameInput);
        errormsg = view.findViewById(R.id.private_chat_erro);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatName = nombre.getText().toString().trim();
                if(!chatName.isEmpty()){
                    if(errormsg.getVisibility() == View.VISIBLE) errormsg.setVisibility(View.GONE);
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("chatName",chatName);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference ref = db
                            .collection("chat_privado")
                            .document();
                    ref.set(data);
                    HashMap<String,Object> userChat = new HashMap<>();
                    userChat.put("chatId",ref.getId());
                    db.collection("users")
                            .document(user.getUid())
                            .collection("chats_privados")
                            .add(userChat);

                    Intent chatRoomIntent = new Intent(getContext(),PrivateChatRoomActivity.class);
                    chatRoomIntent.putExtra(ChatRoomActivity.NOMBRE_DEL_CHAT, chatName)
                            .putExtra(ChatRoomActivity.TIPO_CHAT_ROOM, "chat_privado")
                            .putExtra(ChatRoomActivity.CHAT_ROOM_NAME, ref.getId());
                    dismiss();
                    startActivity(chatRoomIntent);
                }else{
                    errormsg.setVisibility(View.VISIBLE);
                    nombre.setText("");
                }
            }
        });


    }

}
