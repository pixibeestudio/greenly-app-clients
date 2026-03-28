package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.Cart;
import com.pixibeestudio.greenly.data.model.CartRequest;
import com.pixibeestudio.greenly.data.model.CartResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static final String TAG = "CartRepository";
    private Context context;

    public CartRepository(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<Cart>> getCarts() {
        MutableLiveData<List<Cart>> data = new MutableLiveData<>();

        RetrofitClient.getApiService(context).getCarts().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "Lỗi API giỏ hàng: " + response.body().getMessage());
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi kết nối giỏ hàng: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng giỏ hàng: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public MutableLiveData<Boolean> addToCart(int productId, int quantity) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        CartRequest request = new CartRequest(productId, quantity);

        RetrofitClient.getApiService(context).addToCart(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().get("success").getAsBoolean();
                    result.setValue(success);
                } else {
                    Log.e(TAG, "Lỗi thêm giỏ hàng: " + response.code());
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng thêm giỏ hàng: " + t.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }

    public MutableLiveData<Boolean> updateCart(int cartId, int quantity) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        // Trong trường hợp update, ta có thể mượn CartRequest hoặc gửi theo cấu trúc yêu cầu
        // API updateCart thường cần product_id, nếu không cần có thể dùng request tùy chỉnh
        CartRequest request = new CartRequest(0, quantity); // product_id = 0 vì không đổi SP, chỉ đổi số lượng
        
        RetrofitClient.getApiService(context).updateCart(cartId, request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().get("success").getAsBoolean();
                    result.setValue(success);
                } else {
                    Log.e(TAG, "Lỗi cập nhật giỏ hàng: " + response.code());
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng cập nhật giỏ hàng: " + t.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }

    public MutableLiveData<Boolean> deleteCartItem(int cartId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        RetrofitClient.getApiService(context).deleteCartItem(cartId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().get("success").getAsBoolean();
                    result.setValue(success);
                } else {
                    Log.e(TAG, "Lỗi xóa SP giỏ hàng: " + response.code());
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng xóa SP giỏ hàng: " + t.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }

    public MutableLiveData<Boolean> clearCart() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        RetrofitClient.getApiService(context).clearCart().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().get("success").getAsBoolean();
                    result.setValue(success);
                } else {
                    Log.e(TAG, "Lỗi xóa toàn bộ giỏ hàng: " + response.code());
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng xóa toàn bộ giỏ hàng: " + t.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }
}
