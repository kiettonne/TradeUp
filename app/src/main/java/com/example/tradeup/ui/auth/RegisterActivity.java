package com.example.tradeup.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tradeup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvLogin = findViewById(R.id.tvLogin);
        String text = "Already have an account? Sign In";

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        };
        ss.setSpan(clickableSpan, text.indexOf("Sign In"), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(ss);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvLogin.setHighlightColor(getResources().getColor(android.R.color.transparent));
    }

    private void registerUser() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            etPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", uid);
                            userMap.put("email", email);
                            userMap.put("name", name);
                            userMap.put("avatar", "");
                            userMap.put("bio", "");

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(uid)
                                    .setValue(userMap)
                                    .addOnCompleteListener(unused -> {
                                        user.sendEmailVerification().addOnSuccessListener(unused1 -> {
                                            Toast.makeText(this, "Đăng ký thành công, hãy xác minh email!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        });
                                    });
                        }
                }) .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
