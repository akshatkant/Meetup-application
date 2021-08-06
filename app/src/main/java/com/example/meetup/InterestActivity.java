package com.example.meetup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InterestActivity extends AppCompatActivity {

    // Define a string key for SharedPreference value
    private static final String SP_INTEREST = "interest";

    // Define integer codes for each interest
    private static final int INTEREST_NONE = 0;
    private static final int INTEREST_GAME = 1;
    private static final int INTEREST_SPORTS = 2;
    private static final int INTEREST_SING = 3;
    private static final int INTEREST_CODE = 4;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    CardView cardGame;
    CardView cardSports;
    CardView cardSing;
    CardView cardCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get and init SharedPreferences and editor
        SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // INTEREST_NONE is the default value if there no registry.
        int savedInterest = sp.getInt(SP_INTEREST, INTEREST_NONE);

        if(INTEREST_GAME == savedInterest) {
            // Start game activity using intent
            Intent intent = new Intent(InterestActivity.this,GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if(INTEREST_SPORTS == savedInterest) {
            // Start sports activity using intent
            Intent intent = new Intent(InterestActivity.this,SportsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if(INTEREST_SING == savedInterest) {
            // Start sing activity using intent
            Intent intent = new Intent(InterestActivity.this,SingingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if(INTEREST_CODE == savedInterest) {
            // Start code activity using intent
            Intent intent = new Intent(InterestActivity.this,CodeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_interest);

        cardGame = findViewById(R.id.cardGame);
        cardCode = findViewById(R.id.cardCode);
        cardSing = findViewById(R.id.cardSing);
        cardSports = findViewById(R.id.cardSports);

        cardGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(InterestActivity.this, "Gaming", Toast.LENGTH_SHORT).show();

                // Here first save the choosen interest
                editor.putInt(SP_INTEREST, INTEREST_GAME);
                editor.apply();

                Intent intent = new Intent(InterestActivity.this,GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        cardSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InterestActivity.this, "Sports", Toast.LENGTH_SHORT).show();

                // Here first save the choosen interest
                editor.putInt(SP_INTEREST, INTEREST_SPORTS);
                editor.apply();

                Intent intent = new Intent(InterestActivity.this,SportsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        cardSing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InterestActivity.this, "Singing", Toast.LENGTH_SHORT).show();

                // Here first save the choosen interest
                editor.putInt(SP_INTEREST, INTEREST_SING);
                editor.apply();

                Intent intent = new Intent(InterestActivity.this,SingingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        cardCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InterestActivity.this, "Coding", Toast.LENGTH_SHORT).show();

                // Here first save the choosen interest
                editor.putInt(SP_INTEREST, INTEREST_CODE);
                editor.apply();

                Intent intent = new Intent(InterestActivity.this,CodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}