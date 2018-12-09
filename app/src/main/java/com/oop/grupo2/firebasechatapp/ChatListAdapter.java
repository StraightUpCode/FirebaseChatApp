package com.oop.grupo2.firebasechatapp;

import android.content.Context;
import android.content.Intent;
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
    private Context context;
    ChatListAdapter(ArrayList<ChatRoom> data){
        dataset = data;
    }
    @NonNull
    @Override
    public ChatListAdapter.ChatRoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Genera la Vista del Elemento
        context = parent.getContext();
        return new ChatRoomHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.chat_room_element, parent , false));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomHolder holder, int position) {
        // Se agregan los textos (El nombre de la Sala de Chat) y El ultimo mensaje a la vista
        final Context context = holder.itemView.getContext();
        final ChatRoom room = dataset.get(position);
        holder.title.setText(room.getChatName());
        String msg = room.getLast_message().notNull()? room.getLast_message().getNickname()+": " + room.getLast_message().getMessage() : "Nuevo Chat" ;
        holder.lastMsg.setText(msg);

        if(room.getTipo_chat().equals("chat_privado")){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ChatRoomIntent = new Intent(context , PrivateChatRoomActivity.class );
                    ChatRoomIntent.putExtra(ChatRoomActivity.CHAT_ROOM_ID,room.getChat_id() );
                    ChatRoomIntent.putExtra(ChatRoomActivity.TIPO_CHAT_ROOM, room.getTipo_chat());
                    ChatRoomIntent.putExtra(ChatRoomActivity.NOMBRE_DEL_CHAT, room.getChatName());
                    context.startActivity(ChatRoomIntent);
                }
            });
        }else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ChatRoomIntent = new Intent(context , PublicChatRoomActivity.class );
                    ChatRoomIntent.putExtra(ChatRoomActivity.CHAT_ROOM_ID,room.getChat_id() );
                    ChatRoomIntent.putExtra(ChatRoomActivity.TIPO_CHAT_ROOM, room.getTipo_chat());
                    ChatRoomIntent.putExtra(ChatRoomActivity.NOMBRE_DEL_CHAT, room.getChatName());
                    context.startActivity(ChatRoomIntent);
                }
            });
        }

    }
}
