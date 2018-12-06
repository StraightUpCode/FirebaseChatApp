package com.oop.grupo2.firebasechatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ExitChatDialog extends DialogFragment {
    interface DeleteChatInterface {
        void OnDeleteChat();
    }
    private DeleteChatInterface Listener;
    private String chatId;
    private String userId;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Abandonar Chat?")
                .setPositiveButton("Abandonar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db
                                .collection("users")
                                .document(userId)
                                .collection("chats_privados")
                                .whereEqualTo("chatId" , chatId)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            String id  = documentSnapshot.getId();
                                            db.collection("users")
                                                    .document(userId)
                                                    .collection("chats_privados")
                                                    .document(id)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Listener.OnDeleteChat();
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }



    private void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public void addDeleteListerner(ExitChatDialog.DeleteChatInterface listener){
        Listener = listener;
    }
    private void setUserId(String userId) {
        this.userId = userId;
    }
    public ExitChatDialog addChatId(String chatId){
        setChatId(chatId);
        return this;
    }
    public ExitChatDialog addUserId(String userId){
        setUserId(userId);
        return this;
    }


}
