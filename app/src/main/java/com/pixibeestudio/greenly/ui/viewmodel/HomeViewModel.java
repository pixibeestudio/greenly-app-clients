package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pixibeestudio.greenly.data.model.Category;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.repository.CategoryRepository;
import com.pixibeestudio.greenly.data.repository.ProductRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    
    private LiveData<List<Category>> categoriesLiveData;
    private LiveData<List<Product>> productsLiveData;
    private LiveData<List<Product>> discountedProductsLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application.getApplicationContext());
        productRepository = new ProductRepository(application.getApplicationContext());
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        if (categoriesLiveData == null) {
            categoriesLiveData = categoryRepository.getCategories();
        }
        return categoriesLiveData;
    }

    public LiveData<List<Product>> getProductsLiveData() {
        if (productsLiveData == null) {
            productsLiveData = productRepository.getProducts();
        }
        return productsLiveData;
    }

    public LiveData<List<Product>> getDiscountedProductsLiveData() {
        if (discountedProductsLiveData == null) {
            discountedProductsLiveData = productRepository.getDiscountedProducts();
        }
        return discountedProductsLiveData;
    }
}
