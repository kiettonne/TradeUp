package com.example.tradeup.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editName, editBio;
    private ImageView avatarView;
    private Uri avatarUri;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    avatarUri = result.getData().getData();
                    Glide.with(this).load(avatarUri).into(avatarView);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editName = findViewById(R.id.editName);
        editBio = findViewById(R.id.editBio);
        avatarView = findViewById(R.id.avatar);
        Button btnSave = findViewById(R.id.btnSave);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserInfo();

        avatarView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid())
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        editName.setText(snapshot.child("name").getValue(String.class));
                        editBio.setText(snapshot.child("bio").getValue(String.class));
                        String avatar = snapshot.child("avatar").getValue(String.class);
                        if (avatar != null && !avatar.isEmpty()) {
                            Glide.with(this).load(Uri.parse(avatar)).into(avatarView);
                        }
                    }
                });
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bio)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang cập nhật...");
        dialog.setCancelable(false);
        dialog.show();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("bio", bio);
        if (avatarUri != null) {
            userMap.put("avatar", avatarUri.toString());
        }

        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .updateChildren(userMap)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
