package com.example.tradeup.data.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.UUID;
import java.util.*;

public class ProductUploadActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText editTitle, editPrice, editDesc, editCategory, editUsage;
    private Spinner spinnerCondition;
    private MultiAutoCompleteTextView editTags;
    private TextView locationText;
    private List<Uri> imageUri = new ArrayList<>();
    private FirebaseUser currentUser;
    private LocationManager locationManager;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData().getClipData()!= null){
                    int count = result.getData().getClipData().getItemCount();
                    imageUri.clear();
                    for (int i =0; i < Math.min(count, 10); i ++){
                        imageUri.add(result.getData().getClipData().getItemAt(i).getUri());
                    }
                    Glide.with(this).load(imageUri.get(0)).into(productImage);
                } else if (result.getData().getData() != null) {
                    imageUri = Collections.singletonList(result.getData().getData());
                    Glide.with(this).load(imageUri.get(0)).into(productImage);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        productImage = findViewById(R.id.productImage);
        editTitle = findViewById(R.id.editTitle);
        editPrice = findViewById(R.id.editPrice);
        editDesc = findViewById(R.id.editDesc);
        editCategory = findViewById(R.id.editCategory);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        editTags = findViewById(R.id.editTags);
        Button btnUpload = findViewById(R.id.btnUpload);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        productImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });

        productImage.setOnClickListener(v -> productImage.performClick());
        btnUpload.setOnClickListener(v -> uploadProduct());
    }
    private void setupConditionSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Mới", "Đã sử dụng - như mới", "Đã sử dụng - tốt", "Cũ"}
        );
        spinnerCondition.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void requestLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            locationText.setText("Lat: " + location.getLatitude() + ", Lng: " +location.getLongitude());
        } else {
            locationText.setText("Khong xac ding vi tri");
        }
    }
    private void uploadProduct() {
        String title = editTitle.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String condition = spinnerCondition.getSelectedItem().toString();
        String location = locationText.getText().toString();
        List<String> tags = Arrays.asList(editTags.getText().toString().split(","));

        if (TextUtils.isEmpty(title)
                || TextUtils.isEmpty(price) || TextUtils.isEmpty(desc)
                || TextUtils.isEmpty(category) || TextUtils.isEmpty(location) || imageUri.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải lên...");
        dialog.setCancelable(false);
        dialog.show();

        List<String> uploadedUrls = new ArrayList<>();
        for (Uri uri : imageUri) {
            String fileId = UUID.randomUUID().toString();
            StorageReference ref = FirebaseStorage.getInstance().getReference("product_images/" + fileId);
            ref.putFile(uri).continueWithTask(task -> ref.getDownloadUrl()).addOnSuccessListener(url -> {
                uploadedUrls.add(url.toString());
                if (uploadedUrls.size() == imageUri.size()) {
                    saveToDatabase(title, price, desc, category, condition, location, tags, uploadedUrls);
                    dialog.dismiss();
                }
            }).addOnSuccessListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveToDatabase (String title, String price, String desc, String category, String condition, String location, List<String> tags, List<String> urls) {
        String id = FirebaseDatabase.getInstance().getReference("products").push().getKey();
        Product product = new Product(id, title, price, urls, desc, category, condition, location, tags, currentUser.getUid());
        FirebaseDatabase.getInstance().getReference("products").child(id).setValue(product)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Tải lên thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show());
    }
}