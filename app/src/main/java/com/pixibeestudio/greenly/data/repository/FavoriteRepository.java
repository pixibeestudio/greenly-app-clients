package com.pixibeestudio.greenly.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.data.model.WishlistResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository xử lý API Wishlist (Yêu thích).
 * Theo pattern CartRepository: dùng Context + RetrofitClient.getApiService(context).
 */
public class FavoriteRepository {
    private static final String TAG = "FavoriteRepository";
    private final Context context;

    public FavoriteRepository(Context context) {
        this.context = context;
    }

    /**
     * Lấy danh sách sản phẩm yêu thích từ API
     * GET /api/wishlist
     */
    public MutableLiveData<List<WishlistItem>> getWishlists() {
        MutableLiveData<List<WishlistItem>> data = new MutableLiveData<>();

        RetrofitClient.getApiService(context).getWishlists().enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        data.setValue(response.body().getData());
                    } else {
                        Log.e(TAG, "API yêu thích trả về thất bại");
                        data.setValue(null);
                    }
                } else {
                    Log.e(TAG, "Lỗi kết nối danh sách yêu thích: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng danh sách yêu thích: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Toggle yêu thích sản phẩm (thêm nếu chưa có, xóa nếu đã có)
     * POST /api/wishlist/toggle { product_id: ... }
     */
    public MutableLiveData<JsonObject> toggleFavorite(int productId) {
        MutableLiveData<JsonObject> result = new MutableLiveData<>();

        JsonObject body = new JsonObject();
        body.addProperty("product_id", productId);

        RetrofitClient.getApiService(context).toggleFavorite(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    Log.e(TAG, "Lỗi toggle yêu thích: " + response.code());
                    // Trả về JSON lỗi để ViewModel xử lý
                    JsonObject error = new JsonObject();
                    error.addProperty("success", false);
                    error.addProperty("message", "Lỗi toggle yêu thích: " + response.code());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng toggle yêu thích: " + t.getMessage());
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("message", "Lỗi mạng: " + t.getMessage());
                result.setValue(error);
            }
        });

        return result;
    }

    /**
     * Xóa toàn bộ danh sách yêu thích
     * DELETE /api/wishlist/clear
     */
    public MutableLiveData<Boolean> clearWishlists() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        RetrofitClient.getApiService(context).clearWishlists().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().has("success")
                            && response.body().get("success").getAsBoolean();
                    result.setValue(success);
                } else {
                    Log.e(TAG, "Lỗi xóa toàn bộ yêu thích: " + response.code());
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng xóa toàn bộ yêu thích: " + t.getMessage());
                result.setValue(false);
            }
        });

        return result;
    }
}
