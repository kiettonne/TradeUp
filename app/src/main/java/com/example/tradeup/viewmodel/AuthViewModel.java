package com.example.tradeup.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tradeup.data.repository.AuthRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepo = new AuthRepository();

    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        authRepo.register(email, password, listener);
    }

    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        authRepo.login(email, password, listener);
    }

    public void sendVerification(OnCompleteListener<Void> listener) {
        authRepo.sendEmailVerification(listener);
    }

    public void forgotPassword(String email, OnCompleteListener<Void> listener) {
        authRepo.forgotPassword(email, listener);
    }

    public void logout() {
        authRepo.logout();
    }

    public FirebaseUser getCurrentUser() {
        return authRepo.getCurrentUser();
    }
}
