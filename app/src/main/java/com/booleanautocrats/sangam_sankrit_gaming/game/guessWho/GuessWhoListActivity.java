package com.booleanautocrats.sangam_sankrit_gaming.game.guessWho;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.booleanautocrats.sangam_sankrit_gaming.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GuessWhoListActivity extends AppCompatActivity {
    LinearLayout relativeLayout;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String finalEmailID;
    int sd=0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        relativeLayout = findViewById(R.id.startRelative);

        Intent inIntent = getIntent();
        String emailID = inIntent.getStringExtra("userEmail");
        finalEmailID = emailID;
        layout_creator(emailID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sd!=0) {
            relativeLayout.removeAllViewsInLayout();
            layout_creator(finalEmailID);
        }
        sd++;
    }


    public void layout_creator(String emailID) {
        final ProgressDialog progressDialog = new ProgressDialog(GuessWhoListActivity.this);
        progressDialog.setTitle("Loading.... Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = Integer.parseInt(String.valueOf(snapshot.child("uploadsImage").getChildrenCount()));
                int imageNo=0;
                for(DataSnapshot dataSnapshot : snapshot.child("Registration").getChildren()) {
                    if(dataSnapshot.child("emailID").getValue().toString().equals(emailID))
                        imageNo = Integer.parseInt(dataSnapshot.child("imageNo").getValue().toString());
                }

                progressDialog.dismiss();

                for(int i = 0; i< x; i++) {
                    @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.list_text,null,false);
                    ImageView imageView = view.findViewById(R.id.checkText);
                    if(i<imageNo)
                        imageView.setImageDrawable(getDrawable(R.drawable.checked));
                    TextView textView = view.findViewById(R.id.text1);
                    textView.setText("Level "+ (i + 1));

                    int finalI = i;
                    int finalImageNo = imageNo;
                    view.setOnClickListener(view1 -> {
                        if(finalI> finalImageNo) {
                            Toast.makeText(GuessWhoListActivity.this, "Please Solve above images", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(GuessWhoListActivity.this, ImageActivity.class);
                            intent.putExtra("level", finalI + 1);
                            intent.putExtra("userEmail", emailID);
                            relativeLayout.removeAllViewsInLayout();
                            startActivity(intent);
                        }
                    });
                    relativeLayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GuessWhoListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}