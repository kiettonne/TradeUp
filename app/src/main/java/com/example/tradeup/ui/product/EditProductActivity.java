package com.example.tradeup.ui.product;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText editTitle, editPrice, editDesc, editCategory, editLocation, editCondition, editTags;
    private Uri imageUri;
    private Product currentProduct;
    private Spinner spinnerStatus;

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
        editLocation = findViewById(R.id.editLocation);
        editCondition = findViewById(R.id.editCondition);
        editTags = findViewById(R.id.editTags);
        Button btnUpdate = findViewById(R.id.btnSave);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        currentProduct = (Product) getIntent().getSerializableExtra("product");

        if (currentProduct != null) {
            editTitle.setText(currentProduct.getTitle());
            editPrice.setText(currentProduct.getPrice());
            editDesc.setText(currentProduct.getDescription());
            editCategory.setText(currentProduct.getCategory());
            editLocation.setText(currentProduct.getLocation());
            editCondition.setText(currentProduct.getCondition());
            editTags.setText(currentProduct.getTags());

            if (currentProduct.getImages() != null && !currentProduct.getImages().isEmpty()) {
                Glide.with(this).load(currentProduct.getImages().get(0)).into(productImage);
            }
        }

        productImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void updateProduct() {
        String title = editTitle.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String condition = editCondition.getText().toString().trim();
        String tags = editTags.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty() || desc.isEmpty() || category.isEmpty() || location.isEmpty() || condition.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang cập nhật sản phẩm...");
        dialog.show();

        if (imageUri != null) {
            StorageReference imgRef = FirebaseStorage.getInstance().getReference("product_images/" + UUID.randomUUID());
            imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveUpdatedProduct(title, price, desc, category, location, condition, tags, uri.toString(), dialog);
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveUpdatedProduct(title, price, desc, category, location, condition, tags,
                    currentProduct.getImages().get(0), dialog);
        }

        int pos = Arrays.asList(getResources().getStringArray(R.array.status_options))
                .indexOf(currentProduct.getStatus());
        spinnerStatus.setSelection(pos);

    }

    private void saveUpdatedProduct(String title, String price, String desc, String category,
                                    String location, String condition, String tags, String imageUrl, ProgressDialog dialog) {

        currentProduct.setTitle(title);
        currentProduct.setPrice(price);
        currentProduct.setDescription(desc);
        currentProduct.setCategory(category);
        currentProduct.setLocation(location);
        currentProduct.setCondition(condition);
        currentProduct.setTags(Collections.singletonList(tags));
        currentProduct.setImages(Collections.singletonList(imageUrl));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(currentProduct.getId());
        ref.setValue(currentProduct).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
            }
        });
        currentProduct.setStatus(spinnerStatus.getSelectedItem().toString());
    }
}
