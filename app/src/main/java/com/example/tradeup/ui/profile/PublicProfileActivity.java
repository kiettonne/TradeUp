package com.example.tradeup.ui.profile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PublicProfileActivity extends AppCompatActivity {
    private TextView nameView, emailView, bioView;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        bioView = findViewById(R.id.bio);
        avatar = findViewById(R.id.avatar);

        String userId = getIntent().getStringExtra("userId");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                nameView.setText(snapshot.child("name").getValue(String.class));
                emailView.setText(snapshot.child("email").getValue(String.class));
                bioView.setText(snapshot.child("bio").getValue(String.class));
                Glide.with(PublicProfileActivity.this)
                        .load(snapshot.child("avatar").getValue(String.class))
                        .into(avatar);
            }

            public void onCancelled(DatabaseError error) {}
        });
    }
}
