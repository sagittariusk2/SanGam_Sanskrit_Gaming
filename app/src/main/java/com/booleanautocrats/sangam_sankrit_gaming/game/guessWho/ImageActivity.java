package com.booleanautocrats.sangam_sankrit_gaming.game.guessWho;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.booleanautocrats.sangam_sankrit_gaming.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.Objects;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView, unlocking;
    DatabaseReference databaseReference;
    Button submitImage;
    TextView textView;
    EditText imageName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("चित्र प्रहेलिका: - Guess Who?");
        setContentView(R.layout.start_image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String emailID = extras.getString("userEmail");
        int x = extras.getInt("level", 1);

        imageView = findViewById(R.id.imageView);
        submitImage = findViewById(R.id.submitImageButton);
        textView = findViewById(R.id.levelText);
        imageName = findViewById(R.id.imageName);
        unlocking = findViewById(R.id.unlockStart);

        Glide.with(this).load(R.drawable.animated_lock).into(unlocking);

        textView.setText("Level "+ x);

        int[] danger = {0};
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = Objects.requireNonNull(snapshot.child("uploadsImage").child(String.valueOf(x)).child("url").getValue()).toString();
                String name = Objects.requireNonNull(snapshot.child("uploadsImage").child(String.valueOf(x)).child("name").getValue()).toString();

                int[] coin = {0};
                for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                    if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                        coin[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("coins").getValue()).toString());

                    }
                }

                if(danger[0]==0)
                    new DownloadImageFromInternet(imageView).execute(s);

                submitImage.setOnClickListener(view -> {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ImageActivity.this,R.style.AlertDialogStyle);
                    if(emailID.isEmpty()) {
                        dialog.setTitle("CAUTION!!");
                        dialog.setMessage("Your are a guest player.\nPlease register with us, to learn completely.");
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    else if(imageName.getText().toString().equals(name)) {
                        dialog.setTitle("CONGRATULATIONS!!!");
                        for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                            if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                                int y = Integer.parseInt(snapshot1.child("imageNo").getValue().toString());
                                int d = Integer.parseInt(snapshot1.child("coins").getValue().toString());
                                if(x<=y) {
                                    dialog.setMessage("Your Answer is correct.\nYou had already solved it");
                                }
                                else {
                                    dialog.setMessage("Your Answer is correct.\nYou have been awarded 2 coins");
                                    y++;
                                    d += 2;
                                    databaseReference.child("Registration").child(snapshot1.getKey()).child("coins").setValue(String.valueOf(d));
                                    databaseReference.child("Registration").child(snapshot1.getKey()).child("imageNo").setValue(String.valueOf(y));
                                }
                            }
                        }
                        dialog.setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                            if(snapshot.child("uploadsImage").getChildrenCount()==Integer.parseInt(String.valueOf(x))){
                                Intent back = new Intent(ImageActivity.this, GuessWhoListActivity.class);
                                back.putExtra("userEmail", emailID);
                                finish();
                                startActivity(back);
                            }
                            else{
                                Intent intent1 = getIntent();
                                intent1.putExtra("level",  x +1);
                                intent1.putExtra("userEmail", emailID);
                                finish();
                                startActivity(intent1);
                            }

                        });
                        dialog.setNegativeButton("BACK", (dialogInterface, i) -> {
                            Intent back = new Intent(ImageActivity.this, GuessWhoListActivity.class);
                            finish();
                            startActivity(back);
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    else {
                        dialog.setTitle("LOST!!!");
                        dialog.setMessage("Oops!! Your answer is wrong");
                        dialog.setPositiveButton("RETRY", (dialogInterface, i) -> {
                            Intent re = getIntent();
                            re.putExtra("userEmail", emailID);
                            finish();
                            startActivity(re);
                        });
                        dialog.setNegativeButton("BACK", (dialogInterface, i) -> {
                            Intent back = new Intent(ImageActivity.this, GuessWhoListActivity.class);
                            back.putExtra("userEmail", emailID);
                            finish();
                            startActivity(back);
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                });
                unlocking.setOnClickListener(view -> {
                     if(emailID.isEmpty()) {
                        Toast.makeText(ImageActivity.this, "Your are a guest player.\nPlease register with us, to learn completely.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        AlertDialog.Builder list = new AlertDialog.Builder(ImageActivity.this,R.style.AlertDialogStyle);
                        list.setTitle("Want some Hint");
                        list.setIcon(R.drawable.coin);
                        list.setMessage("Use '1' coin to get one answer from the following");

                        View view1 = getLayoutInflater().inflate(R.layout.linear_list_vertical, null, false);
                        LinearLayout layout = view1.findViewById(R.id.verticalID);
                        TextView t1 = view1.findViewById(R.id.coinID);

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                t1.setText("You Have " + coin[0] + " Coins");
                                h.postDelayed(this, 0);
                            }
                        }, 0);

                        String[] hint = {"पहला अक्षर : ","अंतिम अक्षर : ","अक्षरों कि संख्या : "};

                        for (int k = 0; k < 3; k++) {
                            @SuppressLint("InflateParams") View view2 = getLayoutInflater().inflate(R.layout.unlock_layout, null, false);
                            TextView unlockNumber = view2.findViewById(R.id.unlockNumber);
                            unlockNumber.setText(String.valueOf(k + 1));

                            TextView unlockWord = view2.findViewById(R.id.unlockWord);
                            unlockWord.setText(hint[k]);

                            ImageView unlockButton = view2.findViewById(R.id.unlockButton);
                            unlockButton.setImageDrawable(getDrawable(R.drawable.unlock));

                            int finalK = k;
                            unlockButton.setOnClickListener(view3 -> {
                                if (coin[0] < 1) {
                                    showExhaustCoinMessage();
                                } else {
                                    int l = name.length();
                                    unlockButton.setImageResource(R.drawable.checked);
                                    if(finalK==0) {
                                        unlockWord.setText(hint[finalK] + name.charAt(0));
                                    }
                                    if(finalK==1) {
                                        unlockWord.setText(hint[finalK] + name.charAt(l-1));
                                    }
                                    if(finalK==2) {
                                        unlockWord.setText(hint[finalK] + l);
                                    }
                                    coin[0] -= 1;
                                }
                                for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                                    if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                                        databaseReference.child("Registration").child(Objects.requireNonNull(snapshot1.getKey())).child("coins").setValue(String.valueOf(coin[0]));
                                    }
                                }

                            });
                            layout.addView(view2);
                        }
                        list.setView(view1);
                        AlertDialog alertDialog = list.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }


                });
                danger[0]++;
            }

            @SuppressLint("ShowToast")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImageActivity.this, error.toString(),Toast.LENGTH_LONG);
            }
        });
    }

    public void showExhaustCoinMessage() {
        AlertDialog.Builder error = new AlertDialog.Builder(this);
        error.setTitle("Exhausted");
        error.setMessage("You cannot unlock more words");
        AlertDialog alertDialog = error.create();
        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        final ProgressDialog progressDialog = new ProgressDialog(ImageActivity.this);

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            progressDialog.setTitle("Loading.... Please Wait!");
            progressDialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bImage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bImage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Toast.makeText(ImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return bImage;
        }

        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
            imageView.setImageBitmap(result);
        }
    }

}

