package com.pixibeestudio.greenly.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Category;
import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private ApiService apiService;

    public CategoryRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public MutableLiveData<List<Category>> getCategories() {
        MutableLiveData<List<Category>> data = new MutableLiveData<>();
        
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "API trả về success = false");
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi khi gọi API: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng hoặc API: " + t.getMessage());
                data.setValue(null);
            }
        });
        
        return data;
    }
}
