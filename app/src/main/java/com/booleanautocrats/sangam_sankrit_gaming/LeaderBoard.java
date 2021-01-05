package com.booleanautocrats.sangam_sankrit_gaming;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.booleanautocrats.sangam_sankrit_gaming.game.guessWho.GuessWhoListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LeaderBoard extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("LeaderBoard");
        setContentView(R.layout.activity_start);

        LinearLayout startRelative = findViewById(R.id.startRelative);

        final ProgressDialog progressDialog = new ProgressDialog(LeaderBoard.this);
        progressDialog.setTitle("Loading.... Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long x = snapshot.child("Registration").getChildrenCount();
                int i=0;
                String[] user = new String[(int) x];
                int[] points = new int[(int) x];
                for(DataSnapshot dataSnapshot : snapshot.child("Registration").getChildren()) {
                    user[i] = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                    int easy = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("easyNo").getValue()).toString());
                    int medium = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("mediumNo").getValue()).toString());
                    int hard = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("hardNo").getValue()).toString());
                    points[i] = (easy*50)+(medium*100)+(hard*200);
                    i++;
                }

                //Sorting
                for(int j=0; j<x; j++) {
                    int k = j-1;
                    int f = points[j];
                    String fg = user[j];
                    while(k>=0 && f<points[k]) {
                        points[k+1] = points[k];
                        user[k+1] = user[k];
                        k--;
                    }
                    points[k+1] = f;
                    user[k+1] = fg;
                }

                progressDialog.dismiss();

                @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.leaderlayout, null, false);
                TextView unlockNumber1 = view1.findViewById(R.id.leaderNumber);
                TextView unlockWord1 = view1.findViewById(R.id.leaderWord);
                TextView leaderN1 = view1.findViewById(R.id.leaderButton);

                unlockNumber1.setText("S.N.");
                unlockWord1.setText("USER");
                leaderN1.setText("POINTS");
                unlockNumber1.setTextSize(25);
                unlockWord1.setTextSize(25);
                leaderN1.setTextSize(20);

                startRelative.addView(view1);

                for(i=(int)x-1; i>=0; i--) {
                    @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.leaderlayout, null, false);
                    TextView unlockNumber = view.findViewById(R.id.leaderNumber);
                    TextView unlockWord = view.findViewById(R.id.leaderWord);
                    TextView leaderN = view.findViewById(R.id.leaderButton);

                    unlockNumber.setText(String.valueOf(x-i));
                    unlockWord.setText(user[i]);
                    leaderN.setText(String.valueOf(points[i]));
                    startRelative.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeaderBoard.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}