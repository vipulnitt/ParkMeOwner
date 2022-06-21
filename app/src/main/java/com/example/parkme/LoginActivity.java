package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailtxt,passtxt;
    Button loginbtn,registerbtn;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailtxt = findViewById(R.id.email);
        passtxt = findViewById(R.id.passinp);
        loginbtn = findViewById(R.id.loginbtn);
        registerbtn = findViewById(R.id.gtregister);
        progressBar = findViewById(R.id.progressBar);
        registerbtn = findViewById(R.id.gtregister);
        firebaseAuth= FirebaseAuth.getInstance();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtxt.getText().toString().trim();
                String password = passtxt.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    emailtxt.setError("Email is Required!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    passtxt.setError("Password is Required!");
                    return;
                }
                if(password.length()<7)
                {
                    passtxt.setError("Password must be greater than 6!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), baseActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}