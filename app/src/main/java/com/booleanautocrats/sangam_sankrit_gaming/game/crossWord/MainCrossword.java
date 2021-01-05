package com.booleanautocrats.sangam_sankrit_gaming.game.crossWord;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.booleanautocrats.sangam_sankrit_gaming.R;

public class MainCrossword extends AppCompatActivity {
    String[] game = new String[3];
    LinearLayout relativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("शब्दरञ्जनी संग्रह: - Crossword");
        setContentView(R.layout.activity_start);

        relativeLayout = findViewById(R.id.startRelative);

        game[0] = "easy";
        game[1] = "Medium";
        game[2] = "Hard";

        Intent inIntent = getIntent();
        String emailID = inIntent.getStringExtra("userEmail");

        for(int i=0; i<3; i++) {
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.list_text,null,false);
            TextView textView = view.findViewById(R.id.text1);
            textView.setText(game[i]);

            int finalI = i;
            view.setOnClickListener(view1 -> {
                Intent intent1 = new Intent(MainCrossword.this, CrossWordListActivity.class);
                intent1.putExtra("Level", game[finalI]);
                intent1.putExtra("userEmail", emailID);
                startActivity(intent1);
            });
            relativeLayout.addView(view);
        }
    }
}
