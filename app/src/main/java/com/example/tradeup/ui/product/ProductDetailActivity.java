package com.example.tradeup.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView title, price, desc, location, condition, tags;
    private ImageView imageView;
    private DatabaseReference productRef;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        title = findViewById(R.id.detailTitle);
        price = findViewById(R.id.detailPrice);
        desc = findViewById(R.id.detailDesc);
        location = findViewById(R.id.detailLocation);
        condition = findViewById(R.id.detailCondition);
        tags = findViewById(R.id.detailTags);
        imageView = findViewById(R.id.detailImage);
        Button btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, EditProductActivity.class);
            intent.putExtra("product", currentProduct);
            startActivity(intent);
        });

        String productId = getIntent().getStringExtra("product_id");
        if (productId == null || productId.isEmpty()) {
            finish();
            return;
        }

        productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);

        loadProduct();
    }

    private void loadProduct() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    product.setId(snapshot.getKey());

                    title.setText(product.getTitle() != null ? product.getTitle() : "");
                    price.setText(product.getPrice() != null ? product.getPrice() + "₫" : "0₫");
                    desc.setText(product.getDescription());
                    location.setText(product.getLocation());
                    condition.setText(product.getCondition());

                    List<String> tagList = product.getTags();
                    if (tagList != null && !tagList.isEmpty()) {
                        tags.setText(String.join(", ", tagList));
                    } else {
                        tags.setText("Không có thẻ");
                    }

                    List<String> images = product.getImages();
                    if (images != null && !images.isEmpty()) {
                        Glide.with(ProductDetailActivity.this)
                                .load(images.get(0))
                                .placeholder(R.drawable.input_field_bg)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.input_field_bg);
                    }
                    currentProduct = product;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this,
                        "Không thể tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
                android.util.Log.e("FirebaseError", "Lỗi truy xuất sản phẩm: ", error.toException());
                finish();
            }
        });
    }
}
