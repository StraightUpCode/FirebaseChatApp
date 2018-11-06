package com.oop.grupo2.firebasechatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private String userId = "123456xD";
    Button submitButton;
    EditText messageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){

        }
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);
        submitButton = findViewById(R.id.submit);
        messageText = findViewById(R.id.message);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message(userId, messageText.getText().toString());
                db.collection("chat_publico").document("chat_general").collection("chat_msg").add(msg);

            }
        });


    }
}
