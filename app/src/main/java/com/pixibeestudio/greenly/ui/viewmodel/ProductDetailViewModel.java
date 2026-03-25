package com.pixibeestudio.greenly.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.repository.ProductRepository;

public class ProductDetailViewModel extends ViewModel {
    private final ProductRepository productRepository;

    public ProductDetailViewModel() {
        productRepository = new ProductRepository();
    }

    public LiveData<Product> getProductDetail(int id) {
        return productRepository.getProductDetail(id);
    }
}
