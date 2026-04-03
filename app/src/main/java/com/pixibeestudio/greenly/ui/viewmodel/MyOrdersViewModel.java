package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.repository.MyOrdersRepository;

import java.util.List;

public class MyOrdersViewModel extends AndroidViewModel {
    private final MyOrdersRepository repository;
    private MutableLiveData<Resource<List<Order>>> ordersLiveData;

    public MyOrdersViewModel(@NonNull Application application) {
        super(application);
        repository = new MyOrdersRepository(application.getApplicationContext());
    }

    public MutableLiveData<Resource<List<Order>>> getOrdersLiveData() {
        if (ordersLiveData == null) {
            ordersLiveData = new MutableLiveData<>();
        }
        return ordersLiveData;
    }

    public void fetchMyOrders() {
        repository.getMyOrders().observeForever(resource -> {
            if (ordersLiveData != null) {
                ordersLiveData.setValue(resource);
            }
        });
    }
}
