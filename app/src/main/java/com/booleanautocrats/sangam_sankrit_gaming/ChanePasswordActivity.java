package com.booleanautocrats.sangam_sankrit_gaming;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChanePasswordActivity extends AppCompatActivity {
    private EditText currentPass,newPass1,newPass2;
    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.change_password);
        Button changeBtn = findViewById(R.id.changeBtn);
        currentPass=findViewById(R.id.currentPass);
        newPass1=findViewById(R.id.newPass1);
        newPass2=findViewById(R.id.newPass2);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        fuser= firebaseAuth.getCurrentUser();

        changeBtn.setOnClickListener(view -> updatePassword());
    }

    private void updatePassword() {
        String cp = currentPass.getText().toString();
        String np1 = newPass1.getText().toString();
        String np2 = newPass2.getText().toString();
        if (!np1.equals(np2)) {
            newPass2.setError("Different Passwords");
        } else if (cp.equals(np2)) {
            Toast.makeText(com.booleanautocrats.sangam_sankrit_gaming.ChanePasswordActivity.this, "Same old password. Please add a new one", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cp)) {
            currentPass.setError("Please enter your current password");
        } else if (np2.length() < 4) {
            newPass2.setError("your Password length should be >4");
        } else {
            AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(fuser.getEmail()), cp);
            fuser.reauthenticate(authCredential).addOnSuccessListener(aVoid -> fuser.updatePassword(np2).addOnSuccessListener(aVoid1 -> Toast.makeText(ChanePasswordActivity.this, "Password updated successfully!",
                    Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ChanePasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(ChanePasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    }
}