package com.oop.grupo2.firebasechatapp;


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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class JoinChatFragment extends Fragment {

    Button joinButton ;
    EditText chatRoomId;
    TextView errorMsg;
    FragmentPoper listener;
    public JoinChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatRoomId = view.findViewById(R.id.joinId);
        joinButton = view.findViewById(R.id.joinButton);
        errorMsg = view.findViewById(R.id.join_error);


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = chatRoomId.getText().toString().trim();
                if(id.isEmpty()){
                    chatRoomId.setText("");
                    errorMsg.setVisibility(View.VISIBLE);

                }else{
                    if(errorMsg.getVisibility() == View.VISIBLE) errorMsg.setVisibility(View.GONE);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("chatId", id);
                    db.collection("users")
                            .document(user.getUid())
                            .collection("chats_privados")
                            .add(data);

                    db.collection("chat_privado")
                            .document(id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> chatData = documentSnapshot.getData();
                                    String chatName = chatData.get("chatName").toString();
                                    Intent chatRoomIntent = new Intent(getActivity().getApplicationContext() , ChatRoomActivity.class );
                                    chatRoomIntent.putExtra(ChatRoomActivity.TIPO_CHAT_ROOM, "chat_privado")
                                            .putExtra(ChatRoomActivity.NOMBRE_DEL_CHAT, chatName)
                                            .putExtra(ChatRoomActivity.CHAT_ROOM_NAME,id);

                                    startActivity(chatRoomIntent);

                                    listener.PopFragment();


                                }
                            });

                }


            }
        });
    }
    public void setFragmentPopperListener(FragmentPoper popper){
        listener = popper;
    }
}
