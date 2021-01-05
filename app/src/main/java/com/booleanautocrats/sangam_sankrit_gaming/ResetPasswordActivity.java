package com.booleanautocrats.sangam_sankrit_gaming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView resetStatus,signInTv;
    private EditText emailET;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.reset_password);

        Button resetBtn = findViewById(R.id.resetBtn);
        resetStatus=findViewById(R.id.resetStatus);
        signInTv=findViewById(R.id.signInTv);
        emailET=findViewById(R.id.emailET);

        firebaseAuth = FirebaseAuth.getInstance();
        resetBtn.setOnClickListener(view -> firebaseAuth.sendPasswordResetEmail(emailET.getText().toString()).addOnCompleteListener(task -> {
             if(task.isSuccessful()){
                 String email=emailET.getText().toString();
                 if(isValidEmail(email)) {
                     resetStatus.setText("Reset password mail has been sent to your mail ID");
                     signInTv.setVisibility(View.VISIBLE);
                 }else{
                     Toast.makeText(ResetPasswordActivity.this,"Please enter valid email ID", Toast.LENGTH_SHORT).show();
                 }
             }
             else{
                 resetStatus.setText("Oops! . Couldn't send the mail or You are not registered. Please try again");
             }
        }));
        signInTv.setOnClickListener(view -> {
            Intent in=new Intent(ResetPasswordActivity.this,MainActivity.class);
            ResetPasswordActivity.this.finish();
            startActivity(in);
        });


    }

    private boolean isValidEmail(CharSequence t){
        return(!TextUtils.isEmpty(t) && Patterns.EMAIL_ADDRESS.matcher(t).matches());
    }
}
