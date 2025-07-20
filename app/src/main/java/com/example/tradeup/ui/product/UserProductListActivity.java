package com.example.tradeup.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.adapter.ProductAdapter;
import com.example.tradeup.data.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProductListActivity extends AppCompatActivity {

    private List<Product> productList;
    private ProductAdapter adapter;
    private DatabaseReference productRef;
    private String currentUserId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewUserProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, (Product product) -> {
            Intent intent = new Intent(UserProductListActivity.this, EditProductActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        productRef = FirebaseDatabase.getInstance().getReference("products");

        loadUserProducts();
    }

    private void loadUserProducts() {
        productRef.orderByChild("sellerId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product p = snap.getValue(Product.class);
                            if (p != null) productList.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserProductListActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
