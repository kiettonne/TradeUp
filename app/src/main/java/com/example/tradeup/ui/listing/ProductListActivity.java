package com.example.tradeup.ui.listing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.adapter.ProductManageAdapter;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.ui.product.EditProductActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductManageAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ProductManageAdapter adapter;
    private List<Product> userProducts;
    private DatabaseReference productsRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recyclerViewUserProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userProducts = new ArrayList<>();
        adapter = new ProductManageAdapter(userProducts, this);
        recyclerView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productsRef = FirebaseDatabase.getInstance().getReference("Products");

        loadUserProducts();
    }

    private void loadUserProducts() {
        productsRef.orderByChild("sellerId").equalTo(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userProducts.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product p = snap.getValue(Product.class);
                            userProducts.add(p);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProductListActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Product product) {
        productsRef.child(product.getId()).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMarkSoldClick(Product product) {

    }
}
