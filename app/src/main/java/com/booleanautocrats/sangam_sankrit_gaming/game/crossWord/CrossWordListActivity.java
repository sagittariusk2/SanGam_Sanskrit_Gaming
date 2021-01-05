package com.booleanautocrats.sangam_sankrit_gaming.game.crossWord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booleanautocrats.sangam_sankrit_gaming.R;
import com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.Adapter.MyAdapter;
import com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.Model.ListItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrossWordListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItems> itemsList;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String emailID;
    String coming;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crossword_list);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        emailID = bundle.getString("userEmail");
        coming = bundle.getString("Level").toLowerCase();

        last(emailID, coming);
    }

    @Override
    protected void onResume() {
        super.onResume();
        last(emailID, coming);
    }

    public void last(String emailID, String coming) {

        final ProgressDialog progressDialog = new ProgressDialog(CrossWordListActivity.this);
        progressDialog.setTitle("Loading.... Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final int[] loadingValue = {0};
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    long x = snapshot.child(coming).getChildrenCount() / 2;
                    final int[] y = new int[1];
                    for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                        if(Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                            if(coming.toLowerCase().contains("easy")) {
                                y[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("easyNo").getValue()).toString());
                            }
                            if(coming.toLowerCase().contains("medium")) {
                                y[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("mediumNo").getValue()).toString());
                            }
                            if(coming.toLowerCase().contains("hard")) {
                                y[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("hardNo").getValue()).toString());
                            }
                        }
                    }

                    if (loadingValue[0] == 0) {
                        recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CrossWordListActivity.this));

                        itemsList = new ArrayList<>();
                        for (long i = 1; i <= x; i++) {
                            ListItems item = new ListItems(coming.toUpperCase() + " level " + i, emailID);
                            itemsList.add(item);
                        }

                        adapter = new MyAdapter(CrossWordListActivity.this, itemsList, y[0], recyclerView);
                        recyclerView.setAdapter(adapter);
                    }
                    progressDialog.dismiss();
                    loadingValue[0]++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CrossWordListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}