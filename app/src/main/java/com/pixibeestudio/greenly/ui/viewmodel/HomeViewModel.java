package com.pixibeestudio.greenly.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pixibeestudio.greenly.data.model.Category;
import com.pixibeestudio.greenly.data.repository.CategoryRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> categoriesLiveData;

    public HomeViewModel() {
        categoryRepository = new CategoryRepository();
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        if (categoriesLiveData == null) {
            categoriesLiveData = categoryRepository.getCategories();
        }
        return categoriesLiveData;
    }
}
