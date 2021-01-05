package com.booleanautocrats.sangam_sankrit_gaming;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt,passwordEt1,username;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference ref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.signup);
        firebaseAuth=FirebaseAuth.getInstance();
        emailEt=findViewById(R.id.email);
        passwordEt1=findViewById(R.id.password1);
        username=findViewById(R.id.name);
        Button signUpBtn = findViewById(R.id.register);
        progressDialog=new ProgressDialog(this);
        TextView signInTv = findViewById(R.id.signInTv);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sharedPreferences=getSharedPreferences("RegisterDB",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        ref= database.getReference().child("Registration");

        signUpBtn.setOnClickListener(view -> Register());
        signInTv.setOnClickListener(view -> {
            Intent in=new Intent(SignUpActivity.this,MainActivity.class);
            SignUpActivity.this.finish();
            startActivity(in);
        });
    }
    private void Register(){
        String email=emailEt.getText().toString();
        String password1=passwordEt1.getText().toString();
        String uname=username.getText().toString();
        String coins="30";
        String easyNo="0";
        String mediumNo="0";
        String hardNo="0";
        String imageNo="0";


        if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter an emailId");
            return;
        }
        else if(TextUtils.isEmpty(password1)){
            passwordEt1.setError("Confirm your password");
            return;
        }

        else if(password1.length()<4){
            passwordEt1.setError("Length should be >4");
            return;
        }
        else if(!isValidEmail(email)){
            emailEt.setError("Invalid email");
        }

        progressDialog.setMessage("Please wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                   if(task1.isSuccessful()){
                       User user=new User(uname,email,coins,easyNo,mediumNo,hardNo,imageNo);
                       String uid=ref.push().getKey();
                       assert uid != null;
                       ref.child(uid).setValue(user);

                       Toast.makeText(SignUpActivity.this,"Verification email sent.Please verify your email ID", Toast.LENGTH_SHORT).show();
                       emailEt.setText("");
                       passwordEt1.setText("");
                       username.setText("");
                   }
                   else{
                       Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                   }
                });
            }
            else{
                Toast.makeText(SignUpActivity.this,"Registration Failed. Email ID has already been used", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });
    }

    private boolean isValidEmail(CharSequence t){
        return(!TextUtils.isEmpty(t) && Patterns.EMAIL_ADDRESS.matcher(t).matches());
    }
}