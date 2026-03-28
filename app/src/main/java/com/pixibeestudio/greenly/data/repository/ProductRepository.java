package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.ProductDetailResponse;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = "ProductRepository";
    private Context context;

    public ProductRepository(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();

        RetrofitClient.getApiService(context).getProducts().enqueue(new Callback<ProductResponse>() {
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

    public MutableLiveData<List<Product>> getDiscountedProducts() {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();

        RetrofitClient.getApiService(context).getDiscountedProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "Lỗi API giảm giá trả về success = false");
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi kết nối hoặc dữ liệu giảm giá rỗng: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng hoặc parse dữ liệu giảm giá: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public MutableLiveData<Product> getProductDetail(int id) {
        MutableLiveData<Product> data = new MutableLiveData<>();

        RetrofitClient.getApiService(context).getProductDetail(id).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "Lỗi API chi tiết sản phẩm trả về success = false");
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi kết nối hoặc dữ liệu chi tiết sản phẩm rỗng: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng hoặc parse dữ liệu chi tiết sản phẩm: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }
}
