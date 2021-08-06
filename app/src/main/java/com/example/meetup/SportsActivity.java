package com.example.meetup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetup.Utills.Posts;
import com.example.meetup.Utills.SportsPosts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class SportsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE = 101 ;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef,postRef,LikeRef;
    String profileImageUrlV, userNameV;
    CircleImageView profileImageHeader;
    TextView usernameHeader;
    ImageView addImagePost , sendImagePost;
    EditText inputPostDesc;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    StorageReference postImageRef;
    FirebaseRecyclerAdapter<SportsPosts,SportsMyViewHolder>adapter;
    FirebaseRecyclerOptions<SportsPosts>options;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SPORTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        addImagePost=findViewById(R.id.addImagePost);
        sendImagePost=findViewById(R.id.send_post_imageView);
        inputPostDesc=findViewById(R.id.inputAddPost);
        mLoadingBar=new ProgressDialog(this);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("SportsPosts");
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postImageRef= FirebaseStorage.getInstance().getReference().child("PostImage");


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.sports_drawer_header);
        profileImageHeader = view.findViewById(R.id.profileImage_header);
        usernameHeader = view.findViewById(R.id.username_header);


        navigationView.setNavigationItemSelectedListener(this);
        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPost();
            }
        });
        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });
        LoadPost();

    }

    private void LoadPost() {
        options=new FirebaseRecyclerOptions.Builder<SportsPosts>().setQuery(postRef, SportsPosts.class).build();
        adapter = new FirebaseRecyclerAdapter<SportsPosts, SportsMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SportsMyViewHolder holder, int position, @NonNull SportsPosts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                String timeAgo = calculateTimeago(model.getDatePost());
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());
                holder.countLikes(postKey,mUser.getUid(),LikeRef);
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                Picasso.get().load(model.getUserProfileImageUrl()).into(holder.profileImage);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    LikeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                }
                                else
                                {
                                    LikeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    holder.likeImage.setColorFilter(Color.GREEN);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                Toast.makeText(SportsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

            }

            @NonNull
            @NotNull
            @Override
            public SportsMyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sports_single_view_post,parent,false);
                return new SportsMyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private String calculateTimeago(String datePost) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        try {
            long time = sdf.parse(datePost).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode == RESULT_OK && data!=null);
        {
            imageUri = data.getData();
            addImagePost.setImageURI(imageUri);

        }
    }

    private void AddPost() {
        String postDesc = inputPostDesc.getText().toString();
        if(postDesc.isEmpty() || postDesc.length()<3)
        {
            inputPostDesc.setError("Please write something in post description");
        }
        else if (imageUri == null)
        {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strDate = formatter.format(date);

            postImageRef.child(mUser.getUid()+strDate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        postImageRef.child(mUser.getUid()+strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {



                                HashMap hashMap = new HashMap();
                                hashMap.put("datePost",strDate);
                                hashMap.put("postImageUrl",uri.toString());
                                hashMap.put("postDesc",postDesc);
                                hashMap.put("username",userNameV);
                                hashMap.put("userProfileImageUrl",profileImageUrlV);
                                postRef.child(mUser.getUid()+strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task task) {
                                        if (task.isSuccessful())
                                        {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(SportsActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_add_post_image);
                                            inputPostDesc.setText("");
                                        }
                                        else
                                        {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(SportsActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }
                    else
                    {
                        mLoadingBar.dismiss();
                        Toast.makeText(SportsActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mUser == null)
        {
            sendUsertoLoginActivity();
        }
        else
        {
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())
                    {
                        profileImageUrlV = dataSnapshot.child("profileImage").getValue().toString();
                        userNameV = dataSnapshot.child("username").getValue().toString();
                        Picasso.get().load(profileImageUrlV).into(profileImageHeader);
                        usernameHeader.setText(userNameV);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    Toast.makeText(SportsActivity.this, "Sorry!!,Something went wrong", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void sendUsertoLoginActivity() {
        Intent intent = new Intent(SportsActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem Item) {
        switch (Item.getItemId())
        {
            case R.id.menuhome:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.profile:
                startActivity(new Intent(SportsActivity.this,ProfileActivity.class));
                break;

            case R.id.friend:
                startActivity(new Intent(SportsActivity.this,FriendActivity.class));
                break;

            case R.id.findfriend:
                startActivity(new Intent(SportsActivity.this,FindFriendActivity.class));
                break;

            case R.id.chat:
                startActivity(new Intent(SportsActivity.this,ChatUsersActivity.class));
                break;

            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}