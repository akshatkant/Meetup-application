package com.example.meetup;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendMyViewHolder  extends RecyclerView.ViewHolder {
    CircleImageView profileImageUrl;
    TextView username,profession;

    public FriendMyViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        profileImageUrl = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
        profession = itemView.findViewById(R.id.profession);
    }
}
