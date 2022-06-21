package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText names,username,mobilenumber,password,cpassword;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    Button signupbtn,loginbtn;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        names = findViewById(R.id.regname);
        username = findViewById(R.id.regusername);
        mobilenumber = findViewById(R.id.regmobile);
        password = findViewById(R.id.regpass);
        cpassword = findViewById(R.id.regpassconf);
        signupbtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        loginbtn = findViewById(R.id.gtlogin);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,baseActivity.class));
            finish();
        }
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();
               String uname = names.getText().toString().trim();
               String mno = mobilenumber.getText().toString().trim();
                String psw = password.getText().toString();
                String cpsw = cpassword.getText().toString();
                if(TextUtils.isEmpty(uname))
                {
                    names.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(email))
                {
                    username.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(psw))
                {
                    password.setError("Password is Required!");
                    return;
                }
                if(TextUtils.isEmpty(mno))
                {
                    mobilenumber.setError("Mobile Number is Required!");
                    return;
                }
                if(psw.length()<7)
                {
                    password.setError("Password must be greater than 6!");
                    return;
                }
                if(!psw.equals(cpsw))
                {
                    cpassword.setError("Password Didn't Match!");
                    return;
                }
                if(mno.length()!=10)
                {
                    mobilenumber.setError("Invalid Mobile Number!");
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                       userid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                         DocumentReference documentReference = firestore.collection("appusers").document(userid);
                          Map<String,Object> user = new HashMap<>();
                          user.put("email",email);
                          user.put("name",uname);
                          user.put("mobileno",mno);
                          documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  Log.d("Checking","Added Successfully");
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Log.d("checking",e.getMessage());
                              }
                          });
                          startActivity(new Intent(MainActivity.this,baseActivity.class));
                          finish();
                      }
                      else
                      {
                          progressBar.setVisibility(View.GONE);
                          Toast.makeText(MainActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                          Log.d("checking",task.getException().getMessage().toString());
                      }
                    }
                });
            }
        });
loginbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
});
    }
}