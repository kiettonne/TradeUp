package com.example.tradeup.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.adapter.ProductAdapter;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.data.model.ProductUploadActivity;
import com.example.tradeup.ui.product.ProductDetailActivity;
import com.example.tradeup.ui.profile.UserProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;
    private DatabaseReference productRef;
    private EditText searchInput;
    private Spinner categorySpinner, conditionSpinner, sortSpinner;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        searchInput = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.spinnerCategory);
        conditionSpinner = findViewById(R.id.spinnerCondition);
        sortSpinner = findViewById(R.id.spinnerSort);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        adapter = new ProductAdapter(this, productList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        productRef = FirebaseDatabase.getInstance().getReference("products");

        setupSpinners();
        setupSearch();
        loadProductsFromFirebase();

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_upload) {
                startActivity(new Intent(this, ProductUploadActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "Electronics", "Clothing", "Furniture", "Books"}
        );
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All", "Mới", "Đã sử dụng - tốt", "Đã sử dụng - trung bình", "Cũ"}
        );
        conditionSpinner.setAdapter(conditionAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Relevance", "Newest", "Price: Low to High", "Price: High to Low"}
        );
        sortSpinner.setAdapter(sortAdapter);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = MainActivity.this::loadProductsFromFirebase;
                searchHandler.postDelayed(searchRunnable, 300);
            }
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProductsFromFirebase();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
        categorySpinner.setOnItemSelectedListener(listener);
        conditionSpinner.setOnItemSelectedListener(listener);
        sortSpinner.setOnItemSelectedListener(listener);
    }

    private void loadProductsFromFirebase() {
        String keyword = searchInput.getText().toString().trim().toLowerCase();
        String category = categorySpinner.getSelectedItem().toString();
        String condition = conditionSpinner.getSelectedItem().toString();
        String sort = sortSpinner.getSelectedItem().toString();

        Query query = productRef;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product == null) continue;

                    boolean match = true;

                    if (!keyword.isEmpty() && !product.getTitle().toLowerCase().contains(keyword)) {
                        match = false;
                    }
                    if (!category.equals("All") && !product.getCategory().equalsIgnoreCase(category)) {
                        match = false;
                    }
                    if (!condition.equals("All") && !product.getCondition().equalsIgnoreCase(condition)) {
                        match = false;
                    }

                    if (match) {
                        productList.add(product);
                    }
                }

                // Sắp xếp nếu cần
                if (sort.equals("Newest")) {
                    productList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                } else if (sort.equals("Price: Low to High")) {
                    productList.sort((a, b) -> Double.compare(parsePrice(a.getPrice()), parsePrice(b.getPrice())));
                } else if (sort.equals("Price: High to Low")) {
                    productList.sort((a, b) -> Double.compare(parsePrice(b.getPrice()), parsePrice(a.getPrice())));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
