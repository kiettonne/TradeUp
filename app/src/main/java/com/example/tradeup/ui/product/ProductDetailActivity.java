package com.example.tradeup.ui.product;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.tradeup.R;
import com.example.tradeup.data.model.Product;
import com.google.firebase.database.*;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView title, price, desc, location, condition, tags;
    private ImageView imageView;
    private DatabaseReference productRef;
    private String productId;

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

        productId = getIntent().getStringExtra("product_id");
        productRef = FirebaseDatabase.getInstance().getReference("Products").child(productId);

        loadProduct();
    }

    private void loadProduct() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    title.setText(product.getTitle());
                    price.setText(product.getPrice() + "₫");
                    desc.setText(product.getDescription());
                    location.setText(product.getLocation());
                    condition.setText(product.getCondition());
                    tags.setText(product.getTags());

                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        Glide.with(ProductDetailActivity.this).load(product.getImages().get(0)).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
