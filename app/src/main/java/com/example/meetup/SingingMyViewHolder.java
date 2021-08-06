package com.example.meetup;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingingMyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    ImageView postImage,likeImage,commentsImage;
    TextView username,timeAgo,postDesc,likeCounter,commentsCounter;

    public SingingMyViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        profileImage=itemView.findViewById(R.id.CodeProfileImagePost);
        postImage=itemView.findViewById(R.id.postImage);
        username=itemView.findViewById(R.id.CodeProfileUsernamePost);
        timeAgo=itemView.findViewById(R.id.timeAgo);
        postDesc=itemView.findViewById(R.id.postDesc);
        likeImage=itemView.findViewById(R.id.likeImage);
        commentsImage=itemView.findViewById(R.id.commentsImage);
        likeCounter=itemView.findViewById(R.id.likeCounter);
        commentsCounter=itemView.findViewById(R.id.commentsCounter);

    }

    public void countLikes(String postKey, String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    int totalLikes = (int) snapshot.getChildrenCount();
                    likeCounter.setText(totalLikes+ "");
                }
                else
                {
                    likeCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists())
                {
                    likeImage.setColorFilter(Color.GREEN);
                }
                else
                {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
