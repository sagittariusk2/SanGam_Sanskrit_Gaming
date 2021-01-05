package com.booleanautocrats.sangam_sankrit_gaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.booleanautocrats.sangam_sankrit_gaming.game.crossWord.MainCrossword;
import com.booleanautocrats.sangam_sankrit_gaming.game.guessWho.GuessWhoListActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    private DatabaseReference ref;
    private NavigationView navigationView;

    private String emailID,Username,Coins;
    public static boolean LogIn=true;
    public Button game1Btn, game2Btn;
    TextView linkContainer;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dashboard);
       setTitle("संGam:संस्कृत Gaming");
        Bundle extras;
        Intent intent=getIntent();
        extras = intent.getExtras();
        Username = extras.getString("Username");
        emailID = extras.getString("emailID");
        Coins = extras.getString("Coins");

        game1Btn = findViewById(R.id.game1Btn);
        game2Btn = findViewById(R.id.game2Btn);
        linkContainer = findViewById(R.id.linkContainer);


        sharedPreferences = getSharedPreferences("RegisterDB",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ref= FirebaseDatabase.getInstance().getReference();

        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navlayout);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        displayUserData();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id=item.getItemId();

            if(id==R.id.logout) {
                LogIn=false;
                editor.clear();
                editor.apply();
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = headerView.findViewById(R.id.username);
                String n=(String)navUsername.getText();
                if(n.equals("guest123")){
                   showCustomDialog();
                }
                else{

                FirebaseAuth.getInstance().signOut();
                    ProgressDialog progressDialog = new ProgressDialog(Dashboard.this);
                    progressDialog.setTitle("Logging Out....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Intent in =new Intent(Dashboard.this,MainActivity.class);
                            finish();
                            startActivity(in);
                        }
                    }, 2000);

                   }
            }
            if(id==R.id.changePass) {
                View headerView = navigationView.getHeaderView(0);
                TextView navUsername = headerView.findViewById(R.id.username);
                String n= (String) navUsername.getText();
                if(n.equals("guest123")) {
                    Toast.makeText(Dashboard.this,"Sorry , you are a guest player!. You cannot access this",Toast.LENGTH_SHORT).show();
                }
                else {
                Intent in =new Intent(Dashboard.this,ChanePasswordActivity.class);
                startActivity(in);}

            }
            if(id==R.id.feedback) {
                Intent in =new Intent(Dashboard.this,FeedbackActivity.class);
                startActivity(in);
            }
            if(id==R.id.contactUs) {
                Intent in =new Intent(Dashboard.this,contactActivity.class);
                startActivity(in);
            }
            if(id == R.id.leaderBoard) {
                Intent leaderIntent = new Intent(Dashboard.this, LeaderBoard.class);
                startActivity(leaderIntent);
            }
            if(id == R.id.share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String message = linkContainer.getText().toString();
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            }
            if(id==R.id.coinRules){
                Intent in =new Intent(Dashboard.this,RulesActivity.class);
                startActivity(in);

            }
            return false;
        });

        game1Btn.setOnClickListener(view -> {
            Intent crossIntent = new Intent(Dashboard.this, MainCrossword.class);
            crossIntent.putExtra("userEmail", emailID);
            startActivity(crossIntent);
        });

        game2Btn.setOnClickListener(view -> {
            Intent crossIntent = new Intent(Dashboard.this, GuessWhoListActivity.class);
            crossIntent.putExtra("userEmail", emailID);
            startActivity(crossIntent);
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_menu, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        displayUserData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id=item.getItemId();
        if(id==R.id.qmBtn){
            Intent in=new Intent(Dashboard.this,howToPlayActivity.class);
            startActivity(in);

        }
        return super.onOptionsItemSelected(item);
    }

   private void displayUserData() {
        View headerView = navigationView.getHeaderView(0);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.child("Registration").getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    assert user != null;
                    if(emailID.equals(user.getEmailID())) {
                        TextView navUsername = headerView.findViewById(R.id.username);
                        navUsername.setText(Username);
                        TextView navCoins = headerView.findViewById(R.id.coins);
                        navCoins.setText(user.getCoins());
                    }
                }
                String link = Objects.requireNonNull(snapshot.child("ShareLink").child("Link").getValue()).toString();
                linkContainer.setText(link);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TextView navUsername = headerView.findViewById(R.id.username);
        navUsername.setText(Username);
        TextView navCoins = headerView.findViewById(R.id.coins);
        navCoins.setText(Coins);
    }
    private void showCustomDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(Dashboard.this, R.style.AlertDialogStyle);

        builder.setMessage("We hope you were having fun. Do you wish to be a registered player?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Dashboard.this,SignUpActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

    }
}