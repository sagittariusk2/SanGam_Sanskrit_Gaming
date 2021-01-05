package com.booleanautocrats.sangam_sankrit_gaming;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.booleanautocrats.sangam_sankrit_gaming.R;

import org.w3c.dom.Text;

public class RulesActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.rules_layout);
        TextView rulesEnglish=findViewById(R.id.ruleEng);
        String rulesH=getString(R.string.rulesHindi);
        String rulesE=getString(R.string.rules);

        CheckBox cb1=findViewById(R.id.Rhindi);
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rulesEnglish.setText(rulesH);
                }
                else{
                    rulesEnglish.setText(rulesE);
                }
            }
        });

    }
}
