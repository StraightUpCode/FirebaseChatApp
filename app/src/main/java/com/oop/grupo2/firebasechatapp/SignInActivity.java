package com.oop.grupo2.firebasechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import io.opencensus.tags.Tag;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 123;
    FirebaseAuth mAuth ;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        googleSignInPreparation();
        findViewById(R.id.googleSignIn).setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.signInButton).setOnClickListener(this);
    }



    public void googleSignInPreparation() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_google_api_token))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                                    db
                                    .collection("users")
                                    .document(user.getUid() )
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot doc = task.getResult();
                                                if(doc.exists()){
                                                    if(doc.getData() != null){
                                                        if(doc.getData().get("nickname")==null){
                                                            DialogFragment signInFragment = new PostSignInFragment();
                                                            signInFragment.show(getSupportFragmentManager(),"Add Nick Name");
                                                        }else{
                                                            Intent goToMainActivity = new Intent(SignInActivity.this, ChatRoomListActivity.class);
                                                            startActivity(goToMainActivity);

                                                        }

                                                    }

                                                }else{

                                                    DialogFragment signInFragment = new PostSignInFragment();
                                                    signInFragment.show(getSupportFragmentManager(),"Add Nick Name");
                                                }

                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.sign_in_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.googleSignIn : {
                Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(googleSignInIntent,RC_SIGN_IN);
                break;
            }

            case R.id.registerButton: {
                DialogFragment fragment = new RegisterUserDialog();
                fragment.show(getSupportFragmentManager(),"Register");

                break;
            }

            case R.id.signInButton : {
                DialogFragment fragment = new UserSignIn();
                fragment.show(getSupportFragmentManager(),"Sign IN");
                break;
            }

        }
    }


    /*




     */

    /*
    Conseguir el Nick Name
     FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId )
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot doc = task.getResult();
                                        if(doc.exists()){
                                            Log.d("Sign In Activity", "EL usuario tiene esta data" + doc.getData().get("username"));
                                        }else{
                                            PostSignInFragment signInFragment = new PostSignInFragment();
                                            signInFragment.setListener(new FragmentPoper() {
                                                @Override
                                                public void PopFragment() {
                                                    getSupportFragmentManager()
                                                            .popBackStack();
                                                }
                                            });
                                            getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.fragment_layout,signInFragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }

                                    }
                                }
                            });
     */
}
