package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.RegisterRequest;
import com.pixibeestudio.greenly.data.repository.AuthRepository;
import com.pixibeestudio.greenly.utils.Resource;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application.getApplicationContext());
    }

    public LiveData<Resource<JsonObject>> register(String fullname, String email, String password, String passwordConfirmation) {
        RegisterRequest request = new RegisterRequest(fullname, email, password, passwordConfirmation);
        return authRepository.register(request);
    }

    public LiveData<Resource<JsonObject>> login(String email, String password) {
        com.pixibeestudio.greenly.data.model.LoginRequest request = new com.pixibeestudio.greenly.data.model.LoginRequest(email, password);
        return authRepository.login(request);
    }
}
