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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserSignIn extends DialogFragment {

    private EditText correo;
    private EditText password;
    private FirebaseAuth mAuth;
    public UserSignIn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        correo = view.findViewById(R.id.sesion_correo);
        password = view.findViewById(R.id.sesion_contraseña);

        Button button =  view.findViewById(R.id.iniciar_sesion_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  correo.getText().toString();
                String contrasena = password.getText().toString();

                if(!(email.trim().isEmpty()
                && contrasena.trim().isEmpty())){
                    mAuth.signInWithEmailAndPassword(email, contrasena)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent goToMainActivity = new Intent(getActivity() , ChatRoomListActivity.class);
                                    startActivity(goToMainActivity);
                                    dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(),"Error al Inciar Sesion", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }
}
