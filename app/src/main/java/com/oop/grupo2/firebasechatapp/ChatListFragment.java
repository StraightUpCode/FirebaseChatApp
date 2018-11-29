package com.oop.grupo2.firebasechatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment{
    public static String TIPO_DE_CHAT = "TIPO_DE_CHAT";

    ChatListAdapter adapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.chatList);
        final ArrayList<ChatRoom> dataset = new ArrayList<ChatRoom>() ;
        Bundle args = getArguments();
        final String tipoChat = args.getString(TIPO_DE_CHAT);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        adapter = new ChatListAdapter(dataset);
        //Se buscan cuales son las salas de chat
       if(tipoChat.equals("chat_publico")){
                   db.collection("chat_publico")
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
                               ChatRoom chat_room = new ChatRoom(documentSnapshot.getId(),documentSnapshot.getData().get("chatName").toString(),tipoChat,msg);
                               // Como este ultimo mensaje puede cambiar tiene un listener actualizar los datos en caso de que se reciba un nuevo mensaje
                               chat_room.addLastMessageUpdater(new UpdateMessage() {
                                   @Override
                                   public void onMessageUpdated() {
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
       }else{
           FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
           db.collection("users")
                   .document(currentUser.getUid())
                   .collection("chats_privados")
                   .get()
                   .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                           for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                               Map<String, Object> data = (HashMap)documentSnapshot.getData();
                               db.collection("chat_privado")
                                       .document(data.get("chatId").toString())
                                       .get()
                                       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                                               Map<String , Object> data =  (HashMap)documentSnapshot.getData();
                                               Map<String,Object> last_message = (HashMap)data.get("last_message");
                                                Message msg = new Message();
                                               if(last_message != null){
                                                   msg.setUsername(last_message.get("nickname").toString());
                                                   msg.setMessage(last_message.get("message").toString());
                                               }
                                                ChatRoom chatRoom = new ChatRoom(documentSnapshot.getId(), data.get("chatName").toString(),tipoChat ,msg);

                                                chatRoom.addLastMessageUpdater(new UpdateMessage() {
                                                    @Override
                                                    public void onMessageUpdated() {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                               // Se agrega al dataset
                                               dataset.add(chatRoom);
                                               adapter.notifyDataSetChanged();

                                           }
                                       });

                           }


                       }

                   });
       }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext() , 1));

    }


}
