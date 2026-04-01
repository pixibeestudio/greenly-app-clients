package com.pixibeestudio.greenly.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.OrderResponse;
import com.pixibeestudio.greenly.data.model.ShipperStatsResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperRepository {

    private final ApiService apiService;

    public ShipperRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<OrderResponse>> getNewOrders() {
        MutableLiveData<Resource<OrderResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getShipperNewOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(Resource.success(response.body()));
                    } else {
                        data.setValue(Resource.error(response.body().getMessage(), null));
                    }
                } else {
                    data.setValue(Resource.error("Lỗi lấy danh sách đơn hàng", null));
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return data;
    }

    public LiveData<Resource<ShipperStatsResponse>> getStats() {
        MutableLiveData<Resource<ShipperStatsResponse>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getShipperStats().enqueue(new Callback<ShipperStatsResponse>() {
            @Override
            public void onResponse(Call<ShipperStatsResponse> call, Response<ShipperStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(Resource.success(response.body()));
                    } else {
                        data.setValue(Resource.error(response.body().getMessage(), null));
                    }
                } else {
                    data.setValue(Resource.error("Lỗi tải thống kê", null));
                }
            }

            @Override
            public void onFailure(Call<ShipperStatsResponse> call, Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return data;
    }

    public LiveData<Resource<String>> updateWorkStatus(String status) {
        MutableLiveData<Resource<String>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        JsonObject body = new JsonObject();
        body.addProperty("work_status", status);

        apiService.updateWorkStatus(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject res = response.body();
                    if (res.has("success") && res.get("success").getAsBoolean()) {
                        data.setValue(Resource.success(res.has("message") ? res.get("message").getAsString() : "Thành công"));
                    } else {
                        data.setValue(Resource.error(res.has("message") ? res.get("message").getAsString() : "Lỗi không xác định", null));
                    }
                } else {
                    data.setValue(Resource.error("Lỗi cập nhật trạng thái", null));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                data.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return data;
    }
}
