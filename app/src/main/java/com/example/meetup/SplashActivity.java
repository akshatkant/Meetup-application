package com.example.meetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                if(mUser != null)
                {
                    mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot datasnapshot) {

                            if (datasnapshot.exists())
                            {
                                Intent intent = new Intent(SplashActivity.this,InterestActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(SplashActivity.this,SetupActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                }


            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable,3000);



    }
}