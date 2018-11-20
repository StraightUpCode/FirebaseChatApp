package com.oop.grupo2.firebasechatapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatRoomHolder> {
    public static class ChatRoomHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView lastMsg;
        public ChatRoomHolder(View v){
            super(v);
             title = v.findViewById(R.id.chatRoomTitle);
             lastMsg = v.findViewById(R.id.chatRoomLastMsg);

        }
    }

    ArrayList<ChatRoom> dataset;
    ChatListAdapter(ArrayList<ChatRoom> data){
        dataset = data;
    }
    @NonNull
    @Override
    public ChatListAdapter.ChatRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Genera la Vista del Elemento
        return new ChatRoomHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_room_element, parent , false));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomHolder holder, int position) {
        // Se agregan los textos (El nombre de la Sala de Chat) y El ultimo mensaje a la vista
        ChatRoom room = dataset.get(position);
        holder.title.setText(room.getChat_id());
        String msg = room.getLast_message().getNickname()+": " + room.getLast_message().getMessage();
        holder.lastMsg.setText(msg);

    }
}
