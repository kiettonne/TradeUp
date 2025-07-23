package com.example.tradeup.ui.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText editTitle, editPrice, editDesc, editCategory, editLocation;
    private Spinner spinnerStatus, spinnerCondition;
    private Uri imageUri;
    private Product currentProduct;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(productImage);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        productImage = findViewById(R.id.productImage);
        editTitle = findViewById(R.id.editTitle);
        editPrice = findViewById(R.id.editPrice);
        editDesc = findViewById(R.id.editDesc);
        editCategory = findViewById(R.id.editCategory);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        editLocation = findViewById(R.id.editLocation);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnSave.setOnClickListener(v -> updateProduct());
        btnDelete.setOnClickListener(v -> deleteProduct());

        currentProduct = (Product) getIntent().getSerializableExtra("product");

        if (currentProduct != null) {
            editTitle.setText(currentProduct.getTitle());
            editPrice.setText(currentProduct.getPrice());
            editDesc.setText(currentProduct.getDescription());
            editCategory.setText(currentProduct.getCategory());
            editLocation.setText(currentProduct.getLocation());

            if (currentProduct.getImages() != null && !currentProduct.getImages().isEmpty()) {
                Glide.with(this).load(currentProduct.getImages().get(0)).into(productImage);
            }
        }

        productImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Đang bán", "Đã bán", "Tạm dừng"}
        );
        spinnerStatus.setAdapter(statusAdapter);

        if (currentProduct != null) {
            int statusPos = Arrays.asList("Đang bán", "Đã bán", "Tạm dừng").indexOf(currentProduct.getStatus());
            spinnerStatus.setSelection(statusPos != -1 ? statusPos : 0);
        }

    }

    private void updateProduct() {
        String title = editTitle.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String condition = spinnerCondition.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (title.isEmpty() || price.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang cập nhật...");
        dialog.setCancelable(false);
        dialog.show();

        if (imageUri != null) {
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference("product_images/" + UUID.randomUUID());
            ref.putFile(imageUri).addOnSuccessListener(task -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveChanges(title, price, desc, category, location, condition, status, uri.toString(), dialog);
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveChanges(title, price, desc, category, location, condition, status,
                    currentProduct.getImages().get(0), dialog);
        }
    }

    private void saveChanges(String title, String price, String desc, String category,
                             String location, String condition, String status,
                             String imageUrl, ProgressDialog dialog) {
        currentProduct.setTitle(title);
        currentProduct.setPrice(price);
        currentProduct.setDescription(desc);
        currentProduct.setCategory(category);
        currentProduct.setLocation(location);
        currentProduct.setCondition(condition);
        currentProduct.setStatus(status);
        currentProduct.setImages(Collections.singletonList(imageUrl));
        currentProduct.setStatus(spinnerStatus.getSelectedItem().toString());

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("products").child(currentProduct.getId());
        ref.setValue(currentProduct).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference("products")
                            .child(currentProduct.getId())
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}