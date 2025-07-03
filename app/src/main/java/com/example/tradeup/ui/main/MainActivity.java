package com.example.tradeup.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup.R;
import com.example.tradeup.adapter.ProductAdapter;
import com.example.tradeup.data.model.Product;
import com.example.tradeup.ui.auth.LoginChoiceActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("1", "Jordan 1 Retro", "$100", Arrays.asList("")));
        productList.add(new Product("2", "Jordan 2 Nina", "$120", Arrays.asList("")));

        ProductAdapter adapter = new ProductAdapter(this, productList, product -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thích sản phẩm!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginChoiceActivity.class));
            } else {
                Toast.makeText(this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}