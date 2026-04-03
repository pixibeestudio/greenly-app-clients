package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.data.model.OrderResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersRepository {
    private ApiService apiService;

    public MyOrdersRepository(Context context) {
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
    }

    private static final String TAG = "MyOrdersRepository";

    public MutableLiveData<Resource<List<Order>>> getMyOrders() {
        MutableLiveData<Resource<List<Order>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getMyOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                Log.d(TAG, "HTTP Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "success=" + response.body().isSuccess() + ", message=" + response.body().getMessage());
                    if (response.body().isSuccess()) {
                        List<Order> orders = response.body().getData();
                        Log.d(TAG, "Số đơn hàng: " + (orders != null ? orders.size() : 0));
                        data.setValue(Resource.success(orders));
                    } else {
                        String msg = response.body().getMessage();
                        Log.w(TAG, "API trả về thất bại: " + msg);
                        data.setValue(Resource.error(msg != null ? msg : "Lỗi không xác định từ server", null));
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = e.getMessage();
                    }
                    Log.e(TAG, "HTTP Error " + response.code() + ": " + errorBody);
                    data.setValue(Resource.error("Lỗi kết nối hoặc phiên đăng nhập hết hạn (HTTP " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng: " + t.getMessage(), t);
                data.setValue(Resource.error("Lỗi mạng: " + t.getMessage(), null));
            }
        });

        return data;
    }
}
