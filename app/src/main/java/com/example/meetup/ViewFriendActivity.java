package com.example.meetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef,requestRef,friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl,username,city,country;
    String myProfileImageUrl,myUsername,myCity,myCountry;

    CircleImageView profileImage;
    TextView Username,address;
    Button btnPerform,btnDecline;
    String CurrentState="nothing_happen";
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        userId = getIntent().getStringExtra("userKey");
        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();

        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef= FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef= FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        profileImage=findViewById(R.id.profileImage);
        Username=findViewById(R.id.username);
        address=findViewById(R.id.address);
        btnPerform=findViewById(R.id.btnPerform);
        btnDecline=findViewById(R.id.btnDecline);

        LoadUser();
        LoadMyProfile();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(userId);
            }
        });
        CheckUserExistence(userId);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unfriend(userId);
            }
        });
    }

    private void Unfriend(String userId) {
        if (CurrentState.equals("friend")) {
            friendRef.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        friendRef.child(userId).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewFriendActivity.this, "You are Not friends anymore", Toast.LENGTH_SHORT).show();
                                    CurrentState = "nothing_happen";
                                    btnPerform.setText("Become Friend");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }

        if(CurrentState.equals("he_sent_pending"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(userId).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "You have Declined Friend", Toast.LENGTH_SHORT).show();
                        CurrentState="he_sent_decline";
                        btnPerform.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);

                    }
                }
            });
        }
    }

    private void CheckUserExistence(String userId) {

        friendRef.child(mUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    CurrentState="friend";
                    btnPerform.setText("Send Text");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        friendRef.child(userId).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    CurrentState="friend";
                    btnPerform.setText("Send Text");
                    btnDecline.setText("Unfriend");
                    btnDecline.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        requestRef.child(mUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurrentState="I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline"))
                    {
                        CurrentState="I_sent_decline";
                        btnPerform.setText("Cancel Friend Request");
                        btnDecline.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        requestRef.child(userId).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("status").getValue().toString().equals("pending"))
                    {
                        CurrentState="he_sent_pending";
                        btnPerform.setText("Accept Friend Request");
                        btnDecline.setText("Decline Friend Request");
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        if(CurrentState.equals("nothing_happen"))
        {
            CurrentState="nothing_happen";
            btnPerform.setText("Become Friend");
            btnDecline.setVisibility(View.GONE);
        }
    }

    private void PerformAction(String userId) {

        if(CurrentState.equals("nothing_happen"))
        {
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "You have Sent Friend Request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurrentState="I_sent_pending";
                        btnPerform.setText("Cancel Friend Request");

                    }
                    
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if( CurrentState.equals("I_sent_pending")  || CurrentState.equals("I_sent_decline") )
        {
            requestRef.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ViewFriendActivity.this, "You Have Cancelled Friend Request", Toast.LENGTH_SHORT).show();

                        CurrentState="nothing_happen";
                        btnPerform.setText("Become Friend");
                        btnDecline.setVisibility(View.GONE);

                    }
                    else
                    {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(CurrentState.equals("he_sent_pending"));
        {
            requestRef.child(mUser.getUid()).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        HashMap hashMap = new HashMap();
                        hashMap.put("status" , "friend");
                        hashMap.put("username",username);
                        hashMap.put("profileImageUrl" , profileImageUrl);

                        //updated

                        HashMap hashMap1 = new HashMap();
                        hashMap1.put("status" , "friend");
                        hashMap1.put("username",username);
                        hashMap1.put("profileImageUrl" , profileImageUrl);

                        friendRef.child(mUser.getUid()).child(userId).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task task) {

                                if(task.isSuccessful())
                                {
                                    friendRef.child(mUser.getUid()).child(userId).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task task) {

                                            Toast.makeText(ViewFriendActivity.this, "You added Friend", Toast.LENGTH_SHORT).show();
                                            CurrentState="friend";
                                            btnPerform.setText("SEND TEXT");
                                            btnDecline.setText("UnFriend");
                                            btnDecline.setVisibility(View.VISIBLE);

                                        }
                                    });
                                }
                            }
                        });

                    }


                }
            });
        }
        if(CurrentState.equals("friend"))
        {
            Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
            intent.putExtra("otherUserId",userId);
            startActivity(intent);


        }



    }

    private void LoadUser() {

        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    profileImageUrl=snapshot.child("profileImage").getValue().toString();
                    username=snapshot.child("username").getValue().toString();
                    city=snapshot.child("city").getValue().toString();
                    country=snapshot.child("country").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Username.setText(username);
                    address.setText(city+","+country);

                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void LoadMyProfile() {

        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    myProfileImageUrl=snapshot.child("profileImage").getValue().toString();
                    myUsername=snapshot.child("username").getValue().toString();
                    myCity=snapshot.child("city").getValue().toString();
                    myCountry=snapshot.child("country").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    Username.setText(username);
                    address.setText(city+","+country);

                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}