package com.booleanautocrats.sangam_sankrit_gaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.booleanautocrats.sangam_sankrit_gaming.Dashboard.LogIn;

public class MainActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt;
    private ProgressDialog progressDialog;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference ref;
    private String email, password;
    private int flag = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        Button signInBtn = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        TextView signUpTv = findViewById(R.id.signUpTv);
        TextView fp = findViewById(R.id.forgetPass);
        TextView guest = findViewById(R.id.guestP);

        sharedPreferences = getSharedPreferences("RegisterDB", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!LogIn) {
            editor.putBoolean("LoggedIn", false);
            editor.apply();
        }

        if (sharedPreferences.getBoolean("LoggedIn", false)) {
            Intent in = new Intent(com.booleanautocrats.sangam_sankrit_gaming.MainActivity.this, Dashboard.class);
            in.putExtra("emailID", sharedPreferences.getString("Email", ""));
            in.putExtra("Username", sharedPreferences.getString("Username", ""));
            in.putExtra("Coins", sharedPreferences.getString("Coins", ""));
            in.putExtra("LoggedIn", sharedPreferences.getBoolean("LoggedIn", false));
            this.finish();
            startActivity(in);
        }

        ref = FirebaseDatabase.getInstance().getReference().child("Registration");
        signInBtn.setOnClickListener(view ->{
            if(!isConnected(this)){
                showCustomDialog();

            }

            else Login();
                });
        fp.setOnClickListener(view -> {
            if(!isConnected(this)){
                showCustomDialog();

            }
                else {
                emailEt.getText().toString();
                Intent in = new Intent(MainActivity.this, ResetPasswordActivity.class);

                startActivity(in);
                //this.finish();
            /*if(emailEt.getText().toString().equals("")) {
               Toast.makeText(MainActivity.this,"Please type your email ID first", Toast.LENGTH_SHORT).show();
            }*/
            }
        });
        signUpTv.setOnClickListener(view -> {
            if(!isConnected(this)){
                showCustomDialog();

            }else {
                Intent in = new Intent(MainActivity.this, SignUpActivity.class);
                this.finish();
                startActivity(in);
            }

        });

        guest.setOnClickListener(v -> {
            if(!isConnected(this)){
                showCustomDialog();

            }
            else {
                Intent in = new Intent(MainActivity.this, Dashboard.class);
                in.putExtra("emailID", "");
                in.putExtra("Username", "guest123");
                in.putExtra("Coins", "0");
                in.putExtra("LoggedIn", false);
                this.finish();
                startActivity(in);
            }
        });

    }

    private void Login() {

        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter an email Id");
            return;
        }

        progressDialog.setMessage("Please wait");
        progressDialog.show();

        //final int[] danger = {0};

        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user = snapshot1.getValue(User.class);
                            assert user != null;
                            if (email.equals((user.getEmailID())) /*&& danger[0]==0*/) {
                                flag = 1;
                                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                                    Toast.makeText(MainActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                    editor.putString("Email", email);
                                    editor.putString("Password", password);
                                    editor.putString("Coins", user.getCoins());
                                    editor.putString("Username", user.getUsername());
                                    editor.putBoolean("LoggedIn", true);
                                    editor.commit();
                                    Intent in = new Intent(MainActivity.this, Dashboard.class);
                                    in.putExtra("emailID", email);
                                    in.putExtra("Username", user.getUsername());
                                    in.putExtra("Coins", user.getCoins());
                                    //danger[0]++;
                                    MainActivity.this.finish();
                                    startActivity(in);
                                } else {
                                    snapshot1.getRef().removeValue();
                                    firebaseAuth.getCurrentUser().delete();
                                    Toast.makeText(MainActivity.this, "You didn't verify the Email ID. Please SignUp Again and then verify your email ID", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            /*else if(danger[0]==1) {
                                finish();
                            }*/
                        }

                        if (flag == 0)
                            Toast.makeText(MainActivity.this, "Incorrect Email ID or Password", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            progressDialog.dismiss();
        });
    }

    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager=(ConnectivityManager)mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi!=null && wifi.isConnected()) ||(mobile!=null && mobile.isConnected())){
            return true;
        }
    else{
        return false;
        }
    }
    private void showCustomDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);

        builder.setMessage("Please connect to the internet to proceed further").setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

    }

}