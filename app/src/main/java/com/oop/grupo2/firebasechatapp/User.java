package com.oop.grupo2.firebasechatapp;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    public String userId;
    public String userName;
    public User(String uid){
        userId = uid;
    }

    public void fetchUsername(final FetchUsernameListener listener){
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(this.userId)
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String nickname = documentSnapshot.getData().get("nickname").toString();
                    setUserName(nickname);
                    listener.onFetched();
                }
            })  ;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
