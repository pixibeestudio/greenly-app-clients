package com.pixibeestudio.greenly.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.NotificationItem;
import com.pixibeestudio.greenly.data.model.NotificationResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository quản lý dữ liệu thông báo từ API.
 */
public class NotificationRepository {

    private final ApiService apiService;

    public NotificationRepository(Context context) {
        apiService = RetrofitClient.getClient(context).create(ApiService.class);
    }

    /**
     * Lấy danh sách thông báo của user.
     */
    public MutableLiveData<Resource<List<NotificationItem>>> getNotifications() {
        MutableLiveData<Resource<List<NotificationItem>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getNotifications().enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.setValue(Resource.success(response.body().getData()));
                } else {
                    liveData.setValue(Resource.error("Không thể tải thông báo", null));
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                liveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return liveData;
    }

    /**
     * Lấy số thông báo chưa đọc (badge count).
     */
    public MutableLiveData<Resource<Integer>> getUnreadCount() {
        MutableLiveData<Resource<Integer>> liveData = new MutableLiveData<>();

        apiService.getUnreadNotificationCount().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().get("unread_count").getAsInt();
                    liveData.setValue(Resource.success(count));
                } else {
                    liveData.setValue(Resource.error("Lỗi", 0));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), 0));
            }
        });

        return liveData;
    }

    /**
     * Đánh dấu 1 thông báo đã đọc.
     */
    public void markAsRead(int notificationId) {
        apiService.markNotificationRead(notificationId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Không cần xử lý gì thêm
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Log nếu cần
            }
        });
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc.
     */
    public MutableLiveData<Resource<Boolean>> markAllAsRead() {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();

        apiService.markAllNotificationsRead().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(Resource.success(true));
                } else {
                    liveData.setValue(Resource.error("Lỗi", false));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), false));
            }
        });

        return liveData;
    }
}
