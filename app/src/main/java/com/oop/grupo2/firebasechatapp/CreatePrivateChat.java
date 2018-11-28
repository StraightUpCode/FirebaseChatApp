package com.oop.grupo2.firebasechatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePrivateChat extends Fragment {

    private EditText nombre;
    private Button submit;
    private TextView errormsg;

    public FragmentPoper listener;
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
        final View _view = view;

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
                /*
                Copia el ID del chat al Clipboard para hacer copy paste
                android.content.ClipboardManager cm = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip =  android.content.ClipData
                        .newPlainText("Chat Id", ref.getId());
                cm.setPrimaryClip(clip);
                */

                    // Compartir por chat
                /*
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT , "Copia el Siguiente Valor para unirte a mi Chat Privado en FirebaseChatApp : "+ref.getId());
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Chat Invite");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Invitar con: "));*/

                    Intent chatRoomIntent = new Intent(getContext(),ChatRoomActivity.class);
                    chatRoomIntent.putExtra(ChatRoomActivity.NOMBRE_DEL_CHAT, chatName)
                            .putExtra(ChatRoomActivity.TIPO_CHAT_ROOM, "chat_privado")
                            .putExtra(ChatRoomActivity.CHAT_ROOM_NAME, ref.getId());

                    startActivity(chatRoomIntent);
                    listener.PopFragment();
                }else{
                    errormsg.setVisibility(View.VISIBLE);
                    nombre.setText("");
                }
            }
        });


    }

    public void setFragmentPopperListener(FragmentPoper popper){
        listener = popper;
    }
}
