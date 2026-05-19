package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.repository.ProductRepository;

import java.util.List;

/**
 * ViewModel cho man hinh ket qua tim kiem.
 * Goi API tim kiem san pham va expose ket qua qua LiveData.
 */
public class SearchViewModel extends AndroidViewModel {

    private final ProductRepository repository;
    private MutableLiveData<List<Product>> searchResultsLiveData;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application.getApplicationContext());
    }

    /**
     * Goi API tim kiem san pham theo tu khoa va sap xep.
     * @param keyword Tu khoa tim kiem
     * @param sortBy Kieu sap xep (all, price_asc, newest, top_sales)
     */
    public void searchProducts(String keyword, String sortBy) {
        searchResultsLiveData = repository.searchProducts(keyword, sortBy);
    }

    /**
     * Lay LiveData ket qua tim kiem de observe trong Fragment.
     */
    public LiveData<List<Product>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }
}
