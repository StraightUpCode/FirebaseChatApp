package com.oop.grupo2.firebasechatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateChatListFragment extends Fragment {
    FragmentManager myFragmentManager;

    public PrivateChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myFragmentManager = this.getChildFragmentManager();
         ChatListFragment chatList = new ChatListFragment();
        Bundle args = new Bundle();
        args.putString(ChatListFragment.TIPO_DE_CHAT,"chat_privado");
        chatList.setArguments(args);
        myFragmentManager.beginTransaction()
                .add(R.id.frame_holder,chatList,"Chat List Fragment")
                .commit();


        FloatingActionButton button = view.findViewById(R.id.floatingActionButton2);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myFragmentManager.beginTransaction()
                        .add(R.id.frame_holder, new PopNewPrivateChat(),"popup")
                        .addToBackStack(null)
                        .commit();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragmentManager.popBackStack();
            }
        });
    }


}
