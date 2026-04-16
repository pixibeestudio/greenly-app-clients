package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.CheckoutRequest;
import com.pixibeestudio.greenly.data.model.CheckoutResult;
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

    public MutableLiveData<Resource<CheckoutResult>> placeOrder(CheckoutRequest request) {
        MutableLiveData<Resource<CheckoutResult>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.placeOrder(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject body = response.body();
                        JsonObject data = body.getAsJsonObject("data");

                        int orderId = data.get("order_id").getAsInt();
                        int grandTotal = data.get("total_money").getAsInt();
                        String orderCode = data.has("order_code") ? data.get("order_code").getAsString() : "";
                        String paymentUrl = data.has("payment_url") ? data.get("payment_url").getAsString() : "";

                        android.util.Log.d("CheckoutRepository", "Đặt hàng OK - orderId=" + orderId + ", grandTotal=" + grandTotal + ", paymentUrl=" + paymentUrl);
                        CheckoutResult checkoutResult = new CheckoutResult(orderId, grandTotal, orderCode, paymentUrl);
                        result.setValue(Resource.success(checkoutResult));
                    } catch (Exception e) {
                        android.util.Log.e("CheckoutRepository", "Lỗi parse response đặt hàng", e);
                        result.setValue(Resource.error("Đặt hàng thành công nhưng lỗi đọc dữ liệu", null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        android.util.Log.e("CheckoutRepository", "Lỗi đặt hàng (HTTP " + response.code() + "): " + errorBody);
                        result.setValue(Resource.error("Lỗi đặt hàng: " + response.message(), null));
                    } catch (Exception e) {
                        android.util.Log.e("CheckoutRepository", "Lỗi phân tích errorBody", e);
                        result.setValue(Resource.error("Lỗi đặt hàng", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                android.util.Log.e("CheckoutRepository", "Lỗi mạng đặt hàng: " + t.getMessage());
                result.setValue(Resource.error("Không thể kết nối máy chủ", null));
            }
        });

        return result;
    }

    // Xac nhan thanh toan chuyen khoan cho don hang
    public MutableLiveData<Resource<JsonObject>> confirmPayment(int orderId) {
        MutableLiveData<Resource<JsonObject>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.confirmPayment(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("CheckoutRepository", "Xác nhận thanh toán OK - orderId=" + orderId);
                    result.setValue(Resource.success(response.body()));
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        android.util.Log.e("CheckoutRepository", "Lỗi xác nhận thanh toán (HTTP " + response.code() + "): " + errorBody);
                        result.setValue(Resource.error("Lỗi xác nhận thanh toán: " + response.message(), null));
                    } catch (Exception e) {
                        android.util.Log.e("CheckoutRepository", "Lỗi phân tích errorBody", e);
                        result.setValue(Resource.error("Lỗi xác nhận thanh toán", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                android.util.Log.e("CheckoutRepository", "Lỗi mạng xác nhận thanh toán: " + t.getMessage());
                result.setValue(Resource.error("Không thể kết nối máy chủ", null));
            }
        });

        return result;
    }
}
