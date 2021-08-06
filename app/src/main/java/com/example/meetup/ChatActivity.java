package com.example.meetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetup.Utills.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputSms;
    ImageView btnSend;
    CircleImageView userProfileImageAppbar;
    TextView usernameAppbar,status;
    String otherUserId;
    DatabaseReference mUserRef,smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String OtherUsername,OtherUserProfileImageLink,OtherUserStatus;
    FirebaseRecyclerOptions<Chat>options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewHolder>adapter;
    String myProfileImageLink;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        otherUserId=getIntent().getStringExtra("otherUserId");

        recyclerView=findViewById(R.id.recyclerview);
        inputSms=findViewById(R.id.inputSms);
        btnSend=findViewById(R.id.btnSend);
        userProfileImageAppbar=findViewById(R.id.userProfileImageAppbar);
        usernameAppbar=findViewById(R.id.usernameAppbar);
        status=findViewById(R.id.status);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        smsRef= FirebaseDatabase.getInstance().getReference().child("Message");

        LoadOtherUser();
        LoadMyProfile();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSms();
            }
        });
        LoadSMS();

    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    myProfileImageLink=snapshot.child("profileImage").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void LoadSMS() {

        options=new FirebaseRecyclerOptions.Builder<Chat>().setQuery(smsRef.child(mUser.getUid()).child(otherUserId),Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ChatMyViewHolder holder, int position, @NonNull @NotNull Chat model) {

                if(model.getUserId().equals(mUser.getUid()))
                {
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getSms());
                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);
                }
                else
                {
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getSms());

                    Picasso.get().load(OtherUserProfileImageLink).into(holder.firstUserProfile);
                }

            }

            @NonNull
            @NotNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms,parent,false);

                return new ChatMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void SendSms() {
        String sms = inputSms.getText().toString();
        if(sms.isEmpty())
        {
            Toast.makeText(this, "Please Write Something!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("sms",sms);
            hashMap.put("status","unseen");
            hashMap.put("userId",mUser.getUid());
            smsRef.child(otherUserId).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful())
                    {
                        smsRef.child(mUser.getUid()).child(otherUserId).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task task) {
                                if(task.isSuccessful())
                                {
                                    inputSms.setText(null);
                                    Toast.makeText(ChatActivity.this, "Text sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
        }
    }

    private void LoadOtherUser() {

        mUserRef.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    OtherUsername=snapshot.child("username").getValue().toString();
                    OtherUserProfileImageLink=snapshot.child("profileImage").getValue().toString();
                    OtherUserStatus=snapshot.child("status").getValue().toString();
                    Picasso.get().load(OtherUserProfileImageLink).into(userProfileImageAppbar);
                    usernameAppbar.setText(OtherUsername);
                    status.setText(OtherUserStatus);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}