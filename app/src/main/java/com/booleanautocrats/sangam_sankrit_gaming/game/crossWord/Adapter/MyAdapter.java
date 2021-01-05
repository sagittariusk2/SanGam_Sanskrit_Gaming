package com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.booleanautocrats.sangam_sankrit_gaming.R;
import com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.CrosswordGameActivity;
import com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.Model.ListItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Context context;
    private final List<ListItems> listItems;
    private final int levelNo;
    private RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");


    public MyAdapter(Context context, List listItems, int levelNo, RecyclerView recyclerView) {
        this.context = context;
        this.listItems = listItems;
        this.levelNo = levelNo;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        ListItems items = listItems.get(position);
        holder.name.setText(items.getName());
        if(position<levelNo) {
            holder.imageView.setImageResource(R.drawable.checked);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.text1);
            imageView = itemView.findViewById(R.id.checkText);
        }

        @Override
        public void onClick(View view) {
            //Get the position of the row clicked
            int position = getAdapterPosition();
            ListItems item = listItems.get(position);
            final int[] x = new int[1];
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if(Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(item.getEmailID())) {
                            if(item.getName().toLowerCase().contains("easy")) {
                                x[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("easyNo").getValue()).toString());
                            }
                            if(item.getName().toLowerCase().contains("medium")) {
                                x[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("mediumNo").getValue()).toString());
                            }
                            if(item.getName().toLowerCase().contains("hard")) {
                                x[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("hardNo").getValue()).toString());
                            }
                        }
                    }

                    if(position>x[0]) {
                        Toast.makeText(context, "Please Solve Above one.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(context, CrosswordGameActivity.class);
                        intent.putExtra("name", item.getName());
                        intent.putExtra("userEmail", item.getEmailID());
                        intent.putExtra("position", String.valueOf(position + 1));
                        recyclerView.removeAllViewsInLayout();
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}