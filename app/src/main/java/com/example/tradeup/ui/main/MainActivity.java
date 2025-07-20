package com.example.tradeup.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.adapter.ProductAdapter;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.data.model.ProductUploadActivity;
import com.example.tradeup.ui.auth.LoginChoiceActivity;
import com.example.tradeup.ui.profile.UserProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference productRef;
    private BottomNavigationView bottomNavigation;
    private ProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        adapter = new ProductAdapter(this, productList, product -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thích sản phẩm!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginChoiceActivity.class));
            } else {
                Toast.makeText(this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        productRef = FirebaseDatabase.getInstance().getReference("Products");
        loadProductsFromFirebase();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_upload) {
                startActivity(new Intent(MainActivity.this, ProductUploadActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadProductsFromFirebase() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}