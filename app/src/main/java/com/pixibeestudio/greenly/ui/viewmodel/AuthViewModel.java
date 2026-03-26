package com.pixibeestudio.greenly.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.RegisterRequest;
import com.pixibeestudio.greenly.data.repository.AuthRepository;
import com.pixibeestudio.greenly.utils.Resource;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
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
