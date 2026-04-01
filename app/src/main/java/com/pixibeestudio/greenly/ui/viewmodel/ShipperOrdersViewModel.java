package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.data.model.OrderResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.ui.utils.SingleLiveEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperOrdersViewModel extends AndroidViewModel {
    private final ApiService apiService;

    private final MutableLiveData<Resource<List<Order>>> pickupOrdersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Order>>> shippingOrdersLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<Resource<String>> actionStatusLiveData = new SingleLiveEvent<>();

    public ShipperOrdersViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getApiService(application);
    }

    public LiveData<Resource<List<Order>>> getPickupOrdersLiveData() {
        return pickupOrdersLiveData;
    }

    public LiveData<Resource<List<Order>>> getShippingOrdersLiveData() {
        return shippingOrdersLiveData;
    }

    public SingleLiveEvent<Resource<String>> getActionStatusLiveData() {
        return actionStatusLiveData;
    }

    public void fetchPickupOrders() {
        pickupOrdersLiveData.setValue(Resource.loading());
        apiService.getShipperPickupOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        pickupOrdersLiveData.setValue(Resource.success(response.body().getData()));
                    } else {
                        pickupOrdersLiveData.setValue(Resource.error(response.body().getMessage(), null));
                    }
                } else {
                    pickupOrdersLiveData.setValue(Resource.error("Lỗi lấy danh sách chờ lấy hàng", null));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                pickupOrdersLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void fetchShippingOrders() {
        shippingOrdersLiveData.setValue(Resource.loading());
        apiService.getShipperShippingOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        shippingOrdersLiveData.setValue(Resource.success(response.body().getData()));
                    } else {
                        shippingOrdersLiveData.setValue(Resource.error(response.body().getMessage(), null));
                    }
                } else {
                    shippingOrdersLiveData.setValue(Resource.error("Lỗi lấy danh sách đang giao", null));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                shippingOrdersLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void pickupOrder(int orderId) {
        actionStatusLiveData.setValue(Resource.loading());
        apiService.pickupOrder(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                handleActionResponse(response, "Đã lấy hàng thành công", "pickup");
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                actionStatusLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void completeOrder(int orderId) {
        actionStatusLiveData.setValue(Resource.loading());
        apiService.completeOrder(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                handleActionResponse(response, "Giao hàng thành công", "complete");
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                actionStatusLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void failOrder(int orderId) {
        actionStatusLiveData.setValue(Resource.loading());
        apiService.failOrder(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                handleActionResponse(response, "Đã hủy đơn hàng", "fail");
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                actionStatusLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    private void handleActionResponse(Response<JsonObject> response, String defaultSuccessMsg, String actionType) {
        if (response.isSuccessful() && response.body() != null) {
            JsonObject res = response.body();
            if (res.has("success") && res.get("success").getAsBoolean()) {
                String msg = res.has("message") ? res.get("message").getAsString() : defaultSuccessMsg;
                actionStatusLiveData.setValue(Resource.success(msg));
                // Refresh data based on action
                if ("pickup".equals(actionType)) {
                    fetchPickupOrders();
                    fetchShippingOrders();
                } else if ("complete".equals(actionType) || "fail".equals(actionType)) {
                    fetchShippingOrders();
                }
            } else {
                String msg = res.has("message") ? res.get("message").getAsString() : "Thao tác thất bại";
                actionStatusLiveData.setValue(Resource.error(msg, null));
            }
        } else {
            actionStatusLiveData.setValue(Resource.error("Lỗi xử lý thao tác", null));
        }
    }
}
