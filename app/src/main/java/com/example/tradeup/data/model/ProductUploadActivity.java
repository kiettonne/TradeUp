package com.example.tradeup.data.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProductUploadActivity extends AppCompatActivity {

    private EditText editTitle, editPrice, editDesc, editCategory, editUsage, editLocation;
    private MultiAutoCompleteTextView editTags;
    private Spinner spinnerCondition;
    private ImageView productImage;

    private List<Uri> imageUris = new ArrayList<>();
    private FirebaseUser currentUser;
    private static final int IMAGE_PICK_CODE = 1000;
    private String base64Image = null;

    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("products");

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        imageUris.clear();
                        for (int i = 0; i < Math.min(count, 10); i++) {
                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                            imageUris.add(uri);
                        }
                        Glide.with(this).load(imageUris.get(0)).into(productImage);
                    } else if (result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        imageUris = Collections.singletonList(uri);
                        Glide.with(this).load(uri).into(productImage);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        editTitle = findViewById(R.id.editTitle);
        editPrice = findViewById(R.id.editPrice);
        editDesc = findViewById(R.id.editDesc);
        editCategory = findViewById(R.id.editCategory);
        editUsage = findViewById(R.id.editUsage);
        editLocation = findViewById(R.id.editLocation);
        editTags = findViewById(R.id.editTags);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        productImage = findViewById(R.id.productImage);
        Button btnUpload = findViewById(R.id.btnUpload);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setupConditionSpinner();
        getLocationFromGPS();

        productImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });

        btnUpload.setOnClickListener(v -> uploadProduct());
    }

    private void setupConditionSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Mới", "Đã sử dụng - tốt", "Đã sử dụng - trung bình", "Cũ"});
        spinnerCondition.setAdapter(adapter);
    }

    @SuppressLint("MissingPermission")
    private void getLocationFromGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            editLocation.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
        }
    }

    private void uploadProduct() {
        String title = editTitle.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String usage = editUsage.getText().toString().trim();
        String condition = spinnerCondition.getSelectedItem().toString();
        String location = editLocation.getText().toString().trim();
        List<String> tags = Arrays.asList(editTags.getText().toString().trim().split(","));

        if (title.isEmpty() || price.isEmpty() || desc.isEmpty() || category.isEmpty()
                || condition.isEmpty() || location.isEmpty() || imageUris.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang đăng sản phẩm...");
        dialog.setCancelable(false);
        dialog.show();

        List<String> base64Images = new ArrayList<>();
        try {
            for (Uri uri : imageUris) {
                base64Images.add(encodeImageToBase64(uri));
            }
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(this, "Lỗi chuyển ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        saveToRealtimeDatabase(title, price, desc, category, condition, location, tags, base64Images, dialog);
    }
    private String encodeImageToBase64(Uri imageUri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void saveToRealtimeDatabase(String title, String price, String desc,
                                        String category, String condition, String location,
                                        List<String> tags, List<String> imageUrls, ProgressDialog dialog) {

        FirebaseDatabase realtimeDb = FirebaseDatabase.getInstance();
        String id = realtimeDb.getReference("products").push().getKey();

        Product product = new Product(id, title, price, imageUrls, desc, category,
                condition, location, tags, FirebaseAuth.getInstance().getCurrentUser().getUid());

        realtimeDb.getReference("products").child(id).setValue(product)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Đăng sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Lỗi đăng sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
