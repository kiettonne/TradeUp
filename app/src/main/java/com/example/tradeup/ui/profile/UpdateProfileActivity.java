package com.example.tradeup.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editName, editBio;
    private ImageView avatarView;
    private Button btnSave;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editName = findViewById(R.id.editName);
        editBio = findViewById(R.id.editBio);
        avatarView = findViewById(R.id.avatar);
        btnSave = findViewById(R.id.btnSave);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        avatarView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String bio = editBio.getText().toString().trim();

        if (name.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang cập nhật...");
        dialog.setCancelable(false);
        dialog.show();

        if (avatarUri != null) {
            String avatarId = UUID.randomUUID().toString();
            StorageReference ref = FirebaseStorage.getInstance().getReference("avatars/" + avatarId);
            ref.putFile(avatarUri).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateUserInfo(name, bio, uri.toString(), dialog);
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            });
        } else {
            updateUserInfo(name, bio, null, dialog);
        }
    }

    private void updateUserInfo(String name, String bio, @Nullable String avatarUrl, ProgressDialog dialog) {
        String uid = currentUser.getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("name").setValue(name);
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("bio").setValue(bio);

        if (avatarUrl != null) {
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("avatar").setValue(avatarUrl);
        }

        dialog.dismiss();
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}
