package com.example.tradeup.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.ui.auth.LoginChoiceActivity;
import com.example.tradeup.ui.profile.UpdateProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nameView, emailView, bioView;
    private ImageView avatarView;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        bioView = findViewById(R.id.bio);
        avatarView = findViewById(R.id.avatar);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginChoiceActivity.class));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        loadUserInfo();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginChoiceActivity.class));
            finish();
        });
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String bio = snapshot.child("bio").getValue(String.class);
                String avatar = snapshot.child("avatar").getValue(String.class);

                nameView.setText(name != null ? name : "N/A");
                emailView.setText(email != null ? email : "N/A");
                bioView.setText(bio != null ? bio : "N/A");

                if (avatar != null && !avatar.isEmpty()) {
                    Glide.with(UserProfileActivity.this).load(avatar).into(avatarView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmDeleteAccount() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAccount())
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void deleteAccount() {
        String uid = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid).removeValue()
                .addOnSuccessListener(unused -> {
                    currentUser.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Tài khoản đã bị xóa", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginChoiceActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xóa dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                });
    }

}