package com.example.tradeup.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tradeup.ui.main.MainActivity;
import com.example.tradeup.R;
import com.example.tradeup.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.input_username);
        passwordEditText = findViewById(R.id.input_password);
        Button loginButton = findViewById(R.id.btn_accept_login);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || pass.isEmpty()) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(email, pass, task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
