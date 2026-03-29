package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.CheckoutRequest;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.repository.CheckoutRepository;

public class CheckoutViewModel extends AndroidViewModel {
    private CheckoutRepository repository;

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        repository = new CheckoutRepository(application.getApplicationContext());
    }

    public MutableLiveData<Resource<Boolean>> placeOrder(CheckoutRequest request) {
        return repository.placeOrder(request);
    }
}
