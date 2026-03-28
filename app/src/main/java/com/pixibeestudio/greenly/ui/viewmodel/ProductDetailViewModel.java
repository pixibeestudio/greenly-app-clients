package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.repository.ProductRepository;

public class ProductDetailViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application.getApplicationContext());
    }

    public LiveData<Product> getProductDetail(int id) {
        return productRepository.getProductDetail(id);
    }
}
