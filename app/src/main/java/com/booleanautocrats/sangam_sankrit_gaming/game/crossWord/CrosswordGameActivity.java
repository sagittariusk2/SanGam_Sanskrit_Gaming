package com.booleanautocrats.sangam_sankrit_gaming.game.crossWord;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Objects;

public class CrosswordGameActivity extends AppCompatActivity {

    //Initialising objects
    LinearLayout addingLayout;
    EditText focused;
    TextView hintText, levelText, timerText;
    Button submit;
    Bundle extras;
    Handler handler;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes;
    ImageView unlocking, timerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("शब्दरञ्जनी संग्रह: - Crossword");
        setContentView(R.layout.layout_crossword);

        //Connecting objects with layout
        addingLayout = findViewById(R.id.addinglayout);
        hintText = findViewById(R.id.hintText);
        levelText = findViewById(R.id.level);
        timerText = findViewById(R.id.timer);
        submit = findViewById(R.id.submitButton);
        unlocking = findViewById(R.id.unlockStart);
        timerImage = findViewById(R.id.timerIcon);

        Glide.with(this).load(R.drawable.animated_lock).into(unlocking);
        Glide.with(this).load(R.drawable.animated_clock).into(timerImage);

        //Getting data from intent
        Intent intent = getIntent();
        extras = intent.getExtras();
        String coming = extras.getString("name").toLowerCase();
        String position = extras.getString("position");
        String emailID = extras.getString("userEmail");
        final int[] coin = new int[1];

        //Setting header text
        levelText.setText(coming);



        //Initializing variables
        String level="easy";
        int demoSize=0, checkSize=0;
        if(coming.contains("easy")) {
            level = "easy";
            demoSize = 10;
            checkSize = 6;
        }
        if(coming.contains("medium")) {
            level = "medium";
            demoSize = 13;
            checkSize = 8;
        }
        if(coming.contains("hard")) {
            level = "hard";
            demoSize = 16;
            checkSize = 10;
        }

        //Initializing some arrays
        EditText[][] box = new EditText[checkSize][checkSize];
        String[][] demoData = new String[demoSize][4];
        int[][] checkClose = new int[checkSize][checkSize];
        String[][] fString = new String[checkSize][checkSize];
        String[] fString2 = new String[demoSize];

        //Initializing final variables
        int finalCheckSize = checkSize;
        int finalDemoSize = demoSize;
        String finalLevel = level;
        final int[] layoutCheck = {0};

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                //Inserting data from firebase to demoData[][]
                for (int i = 0; i < finalDemoSize; i++) {
                    demoData[i][0] = Objects.requireNonNull(snapshot.child(finalLevel).child("table" + position).child(String.valueOf(i)).child("Word").getValue()).toString();
                    demoData[i][1] = Objects.requireNonNull(snapshot.child(finalLevel).child("table" + position).child(String.valueOf(i)).child("Meaning").getValue()).toString();
                    demoData[i][2] = Objects.requireNonNull(snapshot.child(finalLevel).child("table" + position).child(String.valueOf(i)).child("direction").getValue()).toString();
                }

                //Inserting data from firebase to checkClose[][]
                for (int i = 0; i < finalCheckSize; i++) {
                    for (int j = 0; j < finalCheckSize; j++) {
                        String s = "field" + (j + 1);
                        checkClose[i][j] = Integer.parseInt(Objects.requireNonNull(snapshot.child(finalLevel).child("tableHint" + position).child(String.valueOf(i)).child(s).getValue()).toString());
                    }
                }

                //Inserting data from firebase to coin[]
                for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                    if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID))
                        coin[0] = Integer.parseInt(Objects.requireNonNull(snapshot1.child("coins").getValue()).toString());
                }

                //Creating layout and boxes on the gameScreen
                if (layoutCheck[0] == 0) {
                    for (int i = 0; i < finalCheckSize; i++) {
                        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.linear_list, null, false);
                        LinearLayout p = view.findViewById(R.id.parent);
                        for (int j = 0; j < finalCheckSize; j++) {
                            addView(i, j, p, checkClose, demoData, fString, box, finalCheckSize);
                        }
                        addingLayout.addView(view);
                    }
                    layoutCheck[0]++;
                }
                //Setting timer stopwatch
        handler = new Handler();
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                UpdateTime = TimeBuff + MillisecondTime;
                Seconds = (int) (UpdateTime/1000);
                Minutes = Seconds / 60;
                Seconds = Seconds %60;

                timerText.setText(""+String.format("%02d",Minutes)+":"+String.format("%02d",Seconds));
                handler.postDelayed(this,0);
            }
        }, 0);

                //Submitting the game
                submit.setOnClickListener(view -> {
                    //Creating string from boxes
                    for (int i1 = 0; i1 < finalCheckSize; i1++) {
                        for (int j1 = 0; j1 < finalCheckSize; j1++) {
                            if (checkClose[i1][j1] > 0) {
                                createString(i1, j1, checkClose, fString2, fString, demoData, finalCheckSize);
                            }
                        }
                    }

                    //Matching each string
                    int won = 0;
                    for (int k = 0; k < finalDemoSize; k++) {
                        if (!String.valueOf(fString2[k]).isEmpty()) {
                            if (String.valueOf(fString2[k]).contains(demoData[k][0]))
                                won++;
                        }
                    }

                    AlertDialog.Builder dialog = new AlertDialog.Builder(CrosswordGameActivity.this, R.style.AlertDialogStyle);
                    if(emailID.isEmpty()) {
                        dialog.setTitle("CAUTION!!");
                        dialog.setMessage("Your are a guest player.\nPlease register with us, to learn completely.");
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    else if (won == finalDemoSize) {
                        //Award to user
                        int awardCoin = 0;
                        int awardPoints = 0;
                        if (finalCheckSize == 6) {
                            awardCoin = 30;
                            awardPoints = 50;
                        }
                        if (finalCheckSize == 8) {
                            awardCoin = 40;
                            awardPoints = 100;
                        }
                        if (finalCheckSize == 10) {
                            awardCoin = 50;
                            awardPoints = 200;
                        }

                        //Updating user coin value and solved puzzle.
                        String[] s = {"easyNo", "mediumNo", "hardNo"};
                        int y, z;
                        for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                            if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                                y = Integer.parseInt(Objects.requireNonNull(snapshot1.child("coins").getValue()).toString());
                                z = Integer.parseInt(Objects.requireNonNull(snapshot1.child(s[(awardCoin / 10) - 3]).getValue()).toString());
                                if (Integer.parseInt(position) <= z) {
                                    dialog.setMessage("Time taken " + timerText.getText() + " minutes\n" + "You have already solved it.");
                                } else {
                                    dialog.setMessage("Time taken " + timerText.getText() + " minutes\n" + "You have been Awarded " + awardCoin + " coins and " + awardPoints + " points.");
                                    z++;
                                    y += awardCoin;
                                    coin[0] = y;
                                    reference.child("Registration").child(Objects.requireNonNull(snapshot1.getKey())).child("coins").setValue(String.valueOf(y));
                                    reference.child("Registration").child(snapshot1.getKey()).child(s[(awardCoin / 10) - 3]).setValue(String.valueOf(z));
                                }
                            }
                        }

                        dialog.setTitle("CONGRATULATIONS!!!");
                        dialog.setIcon(R.drawable.checked);

                        dialog.setPositiveButton("CONTINUE", (dialogInterface, i) -> {
                            if (snapshot.child(finalLevel).getChildrenCount() / 2 == Integer.parseInt(position)) {
                                Intent back = new Intent(CrosswordGameActivity.this, MainCrossword.class);
                                back.putExtra("userEmail", emailID);
                                finish();
                                startActivity(back);
                            } else {
                                Intent intent1 = getIntent();
                                intent1.putExtra("name", finalLevel);
                                intent1.putExtra("position", String.valueOf(Integer.parseInt(position) + 1));
                                intent1.putExtra("userEmail", emailID);
                                finish();
                                startActivity(intent1);
                            }
                        });
                        dialog.setNegativeButton("BACK", (dialogInterface, i) -> {
                            Intent back = new Intent(CrosswordGameActivity.this, MainCrossword.class);
                            back.putExtra("userEmail", emailID);
                            finish();
                            startActivity(back);
                        });
                        dialog.setCancelable(false);
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        dialog.setTitle("LOST!!!");
                        dialog.setMessage("You have not solved this cross word in " + timerText.getText());
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("RETRY", (dialogInterface, i) -> {
                            Intent re = getIntent();
                            re.putExtra("name", coming);
                            re.putExtra("position", position);
                            re.putExtra("userEmail", emailID);
                            finish();
                            startActivity(re);
                        });
                        dialog.setNegativeButton("BACK", (dialogInterface, i) -> {
                            Intent back = new Intent(CrosswordGameActivity.this, MainCrossword.class);
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

                Boolean[] unlockCheck = new Boolean[finalDemoSize];
                for (int i = 0; i < finalDemoSize; i++) {
                    unlockCheck[i] = true;
                }

                //Unlock Word during game.
                unlocking.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onClick(View view) {
                        if(emailID.isEmpty()) {
                            Toast.makeText(CrosswordGameActivity.this, "Your are a guest player.\nPlease register with us, to learn completely.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Developing Dialoge box
                            AlertDialog.Builder list = new AlertDialog.Builder(CrosswordGameActivity.this,R.style.AlertDialogStyle);
                            list.setTitle("Unlock The Word");
                            list.setIcon(R.drawable.coin);
                            list.setMessage("Use Your 10 Coins To Unlock A Word");

                            View view1 = getLayoutInflater().inflate(R.layout.linear_list_vertical, null, false);
                            LinearLayout layout = view1.findViewById(R.id.verticalID);
                            TextView t1 = view1.findViewById(R.id.coinID);

                            //Updating coin every time
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    t1.setText("You Have " + coin[0] + " Coins");
                                    h.postDelayed(this, 0);
                                }
                            }, 0);

                            for (int k = 0; k < finalDemoSize; k++) {
                                //Setting view in each row
                                @SuppressLint("InflateParams") View view2 = getLayoutInflater().inflate(R.layout.unlock_layout, null, false);
                                TextView unlockNumber = view2.findViewById(R.id.unlockNumber);
                                unlockNumber.setText(String.valueOf(k + 1));

                                ImageView unlockButton = view2.findViewById(R.id.unlockButton);

                                //Checking whether it is unlocked by user or not
                                TextView unlockWord = view2.findViewById(R.id.unlockWord);
                                if (unlockCheck[k]) {
                                    unlockWord.setText(demoData[k][1]);
                                    unlockButton.setImageDrawable(getDrawable(R.drawable.unlock));
                                } else {
                                    unlockWord.setText(demoData[k][0]);
                                    unlockButton.setImageDrawable(getDrawable(R.drawable.checked));
                                }

                                //Unlocking a word
                                int finalK = k;
                                unlockButton.setOnClickListener(view3 -> {
                                    if (coin[0] < 10) {
                                        showExhaustCoinMessage();
                                    } else if (unlockCheck[finalK]) {
                                        unlockCheck[finalK] = false;
                                        unlockButton.setImageResource(R.drawable.checked);
                                        unlockWord.setText(demoData[finalK][0]);
                                        coin[0] -= 10;
                                    }
                                    //Updating coin value in Realtime database
                                    for (DataSnapshot snapshot1 : snapshot.child("Registration").getChildren()) {
                                        if (Objects.requireNonNull(snapshot1.child("emailID").getValue()).toString().equals(emailID)) {
                                            reference.child("Registration").child(Objects.requireNonNull(snapshot1.getKey())).child("coins").setValue(String.valueOf(coin[0]));
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


                    }
                });
            }

        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void showExhaustCoinMessage() {
        AlertDialog.Builder error = new AlertDialog.Builder(CrosswordGameActivity.this,R.style.AlertDialogStyle);
        error.setTitle("Exhausted");
        error.setMessage("You cannot unlock more words");
        AlertDialog alertDialog = error.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }


    private void createString(int i1, int j1, int[][] checkClose, String[] fString2, String[][] fString, String[][] demoData, int finalCheckSize){
        int x = checkClose[i1][j1]-1;
        fString2[x]=fString[i1][j1];
        if(demoData[x][2].contains("1")){
            while(j1+1<finalCheckSize && checkClose[i1][j1+1]>=0){
                j1++;
                fString2[x] = fString2[x] + fString[i1][j1];
            }
        }
        else{
            while(i1+1<finalCheckSize && checkClose[i1+1][j1]>=0){
                i1++;
                fString2[x] = fString2[x] + fString[i1][j1];
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private void addView(int i, int j, LinearLayout layout, int[][] checkClose, String[][] demoData, String[][] fString, EditText[][] box, int finalCheckSize) {
        @SuppressLint("InflateParams") View boxView = getLayoutInflater().inflate(R.layout.content_box, null, false);
        EditText editBox = boxView.findViewById(R.id.editBox);
        TextView numberView = boxView.findViewById(R.id.numberWord);

        box[i][j] = editBox;

        if(checkClose[i][j]==-1) {
            editBox.setEnabled(false);
            editBox.setBackgroundResource(R.drawable.closed_box);
        }

        if(checkClose[i][j]!=-1 && checkClose[i][j]!=0){
            numberView.setText(String.valueOf(checkClose[i][j]));
        }

        editBox.setOnTouchListener((view, motionEvent) -> {
            int x=0;
            if(!String.valueOf(numberView.getText()).isEmpty()){
                x = Integer.parseInt(String.valueOf(numberView.getText()));
                hintText.setText(demoData[x-1][1]);
            }
            if(focused != null)
                focused.setBackgroundResource(R.drawable.round_deactivate2);
            for(int k=0; k<finalCheckSize; k++) {
                for(int l=0; l<finalCheckSize; l++) {
                    if (checkClose[k][l]!=-1) {
                        box[k][l].setBackgroundResource(R.drawable.round_deactivate2);
                    }
                }
            }

            x--;
            if(x!=-1) {
                int i1=i, j1=j;
                if (demoData[x][2].contains("1")) {
                    while (j1 + 1 < finalCheckSize && checkClose[i1][j1 + 1] >= 0) {
                        j1++;
                        box[i1][j1].setBackgroundResource(R.drawable.round_wait);
                    }
                } else {
                    while (i1 + 1 < finalCheckSize && checkClose[i1 + 1][j1] >= 0) {
                        i1++;
                        box[i1][j1].setBackgroundResource(R.drawable.round_wait);
                    }
                }
            }

            editBox.setBackgroundResource(R.drawable.round_active);
            editBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i12, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i12, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    fString[i][j] = String.valueOf(editBox.getText());
                }
            });
            focused = editBox;
            return false;
        });
        layout.addView(boxView);
    }
}
