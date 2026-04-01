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
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperDashboardViewModel extends AndroidViewModel {

    private static final String TAG = "ShipperDashboardVM";
    private final ApiService apiService;
    private final MutableLiveData<Resource<List<Order>>> newOrdersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<String>> acceptOrderLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<String>> rejectOrderLiveData = new MutableLiveData<>();

    public ShipperDashboardViewModel(@NonNull Application application) {
        super(application);
        // Truyền context để RetrofitClient gắn được Auth Token
        apiService = RetrofitClient.getApiService(application.getApplicationContext());
    }

    public LiveData<Resource<List<Order>>> getNewOrdersLiveData() {
        return newOrdersLiveData;
    }

    public LiveData<Resource<String>> getAcceptOrderLiveData() {
        return acceptOrderLiveData;
    }

    public LiveData<Resource<String>> getRejectOrderLiveData() {
        return rejectOrderLiveData;
    }

    public void fetchNewOrders() {
        newOrdersLiveData.setValue(Resource.loading());
        Log.d(TAG, "fetchNewOrders: Đang gọi API...");
        apiService.getShipperNewOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                Log.d(TAG, "fetchNewOrders: HTTP code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchNewOrders: success=" + response.body().isSuccess()
                            + ", dataSize=" + (response.body().getData() != null ? response.body().getData().size() : "null"));
                    if (response.body().isSuccess()) {
                        newOrdersLiveData.setValue(Resource.success(response.body().getData()));
                    } else {
                        newOrdersLiveData.setValue(Resource.error(response.body().getMessage(), null));
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
                    Log.e(TAG, "fetchNewOrders: Lỗi HTTP " + response.code() + " - " + errorBody);
                    newOrdersLiveData.setValue(Resource.error("Lỗi lấy danh sách đơn hàng (HTTP " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "fetchNewOrders: onFailure - " + t.getMessage(), t);
                newOrdersLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void acceptOrder(int orderId) {
        acceptOrderLiveData.setValue(Resource.loading());
        apiService.acceptOrder(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("success") && body.get("success").getAsBoolean()) {
                        acceptOrderLiveData.setValue(Resource.success("Nhận đơn thành công"));
                    } else {
                        String msg = body.has("message") ? body.get("message").getAsString() : "Lỗi không xác định";
                        acceptOrderLiveData.setValue(Resource.error(msg, null));
                    }
                } else {
                    acceptOrderLiveData.setValue(Resource.error("Lỗi xác nhận đơn hàng", null));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                acceptOrderLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void rejectOrder(int orderId) {
        rejectOrderLiveData.setValue(Resource.loading());
        apiService.rejectOrder(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("success") && body.get("success").getAsBoolean()) {
                        rejectOrderLiveData.setValue(Resource.success("Từ chối đơn thành công"));
                        // Load lại danh sách sau khi từ chối thành công
                        fetchNewOrders();
                    } else {
                        String msg = body.has("message") ? body.get("message").getAsString() : "Lỗi không xác định";
                        rejectOrderLiveData.setValue(Resource.error(msg, null));
                    }
                } else {
                    rejectOrderLiveData.setValue(Resource.error("Lỗi từ chối đơn hàng", null));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                rejectOrderLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }
}
