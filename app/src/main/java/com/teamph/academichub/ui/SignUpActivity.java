package com.teamph.academichub.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamph.academichub.R;
import com.teamph.academichub.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding signUpBind;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_up);
        signUpBind = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        //Firebace Instance
        mAuth = FirebaseAuth.getInstance();

        //Setup Firebase listener to check if the user is logged in or not
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    Log.d("Login:- ", "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    //Button to register a new user
    public void signUp(View v){
        String email = signUpBind.sEmail.getText().toString().trim();
        final String password = signUpBind.password.getText().toString();
        final String confpassword = signUpBind.confPassword.getText().toString();

        //Validate the user inputs begins
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpBind.sEmail.setError("Enter a valid email address");
            return;
        }

        if(TextUtils.isEmpty(password)){
            signUpBind.password.setError("Enter password to continue");
            return;
        } else if (password.length() < 6){
            signUpBind.password.setError(getResources().getString(R.string.minimum_password));
        }

        if(!password.equals(confpassword)){
            signUpBind.confPassword.setError("Password does not match");
            return;
        }
        //Validation of inputs ends

        //Creating of the user begins
        //Toast.makeText(this, "Everything is set and good to go", Toast.LENGTH_SHORT).show();
        signUpBind.sProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        signUpBind.sProgressBar.setVisibility(View.GONE);

                        if(!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Authentication Failed:" + task.getException(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    @Override
    public void onResume(){
        super.onResume();
        signUpBind.sProgressBar.setVisibility(View.GONE);
    }
    //Firebase Listener to know if the user is logged in or not
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    //Remove the listener when the activity stops
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
