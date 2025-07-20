package com.example.tradeup.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tradeup.ui.main.MainActivity;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;
    private TextView resendVerifyEmail;
    private FirebaseUser unverifiedUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.btnLogin);
        TextView registerText = findViewById(R.id.textRegister);
        progressBar = findViewById(R.id.progressBarLogin);
        resendVerifyEmail = findViewById(R.id.resendVerifyEmail);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());
        registerText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        resendVerifyEmail.setOnClickListener(v -> {
            if (unverifiedUser != null) {
                unverifiedUser.sendEmailVerification()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Email xác minh đã được gửi", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Email không hợp lệ");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    progressBar.setVisibility(View.GONE);
                    if (user != null && user.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        unverifiedUser = user;
                        resendVerifyEmail.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Email chưa được xác minh. Vui lòng xác minh hoặc gửi lại. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
