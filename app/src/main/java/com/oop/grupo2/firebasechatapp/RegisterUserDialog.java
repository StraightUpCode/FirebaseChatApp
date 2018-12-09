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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUserDialog extends DialogFragment {
    private EditText nicknameField;
    private EditText emailField;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseAuth mAuth;

    public RegisterUserDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_user_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        nicknameField = view.findViewById(R.id.nicknameField);
        emailField = view.findViewById(R.id.emailField);
        password = view.findViewById(R.id.passwordField);
        confirmPassword = view.findViewById(R.id.confirmPasswordField);
        Button button = view.findViewById(R.id.registrarUsuario);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation().trim().isEmpty()){
                            mAuth
                            .createUserWithEmailAndPassword(emailField.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("nickname", nicknameField.getText().toString());
                                        FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(user.getUid())
                                                .set(data);
                                        Intent goToMainActivity = new Intent(getActivity() , ChatRoomListActivity.class);
                                        startActivity(goToMainActivity);
                                        dismiss();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }

            }
        });
    }

    public String validation(){
        String mensaje = "";
        String nickname  = nicknameField.getText().toString();
        String correo  = emailField.getText().toString();
        String passwordVal  = password.getText().toString();
        String confirmPasswordVal  = confirmPassword.getText().toString();
        if( nickname.trim().isEmpty() || correo.trim().isEmpty() || passwordVal.trim().isEmpty() || confirmPasswordVal.trim().isEmpty()){
            mensaje =  "Falta un Campo";
        }
        if(!passwordVal.equals(confirmPasswordVal)){
            mensaje =  "Las contraseñas no concuerdan";
        }
        if(passwordVal.length() > 8){
            mensaje =  "La contraseña debe tener al menos 8 caracteres";
        }

        if(passwordVal.matches("\\d+")){
            mensaje = "La contraseña debere al menos un numero";
        }
        return mensaje;
    }
}
