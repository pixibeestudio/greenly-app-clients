package com.pixibeestudio.greenly.data.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = "ProductRepository";

    public MutableLiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();

        RetrofitClient.getApiService().getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "Lỗi API trả về success = false");
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi kết nối hoặc dữ liệu rỗng: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng hoặc parse dữ liệu: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }
}
