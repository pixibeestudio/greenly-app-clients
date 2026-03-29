package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.CheckoutRequest;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutRepository {
    private ApiService apiService;

    public CheckoutRepository(Context context) {
        apiService = RetrofitClient.getApiService(context);
    }

    public MutableLiveData<Resource<Boolean>> placeOrder(CheckoutRequest request) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.placeOrder(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(true));
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        android.util.Log.e("CheckoutRepository", "Lỗi đặt hàng (HTTP " + response.code() + "): " + errorBody);
                        result.setValue(Resource.error("Lỗi đặt hàng: " + response.message(), false));
                    } catch (Exception e) {
                        android.util.Log.e("CheckoutRepository", "Lỗi phân tích errorBody", e);
                        result.setValue(Resource.error("Lỗi đặt hàng", false));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                android.util.Log.e("CheckoutRepository", "Lỗi mạng đặt hàng: " + t.getMessage());
                result.setValue(Resource.error("Không thể kết nối máy chủ", false));
            }
        });

        return result;
    }
}
