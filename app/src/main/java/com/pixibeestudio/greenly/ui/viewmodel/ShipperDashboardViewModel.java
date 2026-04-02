package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.data.model.OrderResponse;
import com.pixibeestudio.greenly.data.model.ShipperStatsResponse;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.repository.ShipperRepository;
import com.pixibeestudio.greenly.ui.utils.SingleLiveEvent;

import java.util.List;

public class ShipperDashboardViewModel extends AndroidViewModel {

    private static final String TAG = "ShipperDashboardVM";
    private static final int POLLING_INTERVAL = 10000; // 10 giây

    private final ShipperRepository repository;

    // Dữ liệu cho UI
    private final MediatorLiveData<Resource<List<Order>>> newOrdersLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<ShipperStatsResponse>> statsLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Resource<com.pixibeestudio.greenly.data.model.WalletProfileResponse>> walletProfileLiveData = new MutableLiveData<>();
    
    // Các event dùng SingleLiveEvent để tránh Double Toast
    private final SingleLiveEvent<Resource<String>> acceptOrderLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<String>> rejectOrderLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<String>> updateStatusLiveData = new SingleLiveEvent<>();

    // Real-time Polling
    private final Handler pollingHandler;
    private final Runnable pollingRunnable;
    private boolean isPolling = false;

    public ShipperDashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new ShipperRepository(RetrofitClient.getApiService(application.getApplicationContext()));
        
        pollingHandler = new Handler(Looper.getMainLooper());
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                fetchNewOrders();
                fetchStats();
                if (isPolling) {
                    pollingHandler.postDelayed(this, POLLING_INTERVAL);
                }
            }
        };
    }

    public LiveData<Resource<List<Order>>> getNewOrdersLiveData() {
        return newOrdersLiveData;
    }

    public LiveData<Resource<ShipperStatsResponse>> getStatsLiveData() {
        return statsLiveData;
    }
    public LiveData<Resource<com.pixibeestudio.greenly.data.model.WalletProfileResponse>> getWalletProfileLiveData() {
        return walletProfileLiveData;
    }

    public SingleLiveEvent<Resource<String>> getAcceptOrderLiveData() {
        return acceptOrderLiveData;
    }

    public SingleLiveEvent<Resource<String>> getRejectOrderLiveData() {
        return rejectOrderLiveData;
    }

    public SingleLiveEvent<Resource<String>> getUpdateStatusLiveData() {
        return updateStatusLiveData;
    }

    public void startPolling() {
        if (!isPolling) {
            isPolling = true;
            pollingRunnable.run(); // Bắt đầu ngay lập tức
        }
    }

    public void stopPolling() {
        isPolling = false;
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    public void fetchNewOrders() {
        LiveData<Resource<OrderResponse>> source = repository.getNewOrders();
        newOrdersLiveData.addSource(source, responseResource -> {
            switch (responseResource.status) {
                case LOADING:
                    // Chỉ emit loading nếu list đang trống, tránh giật UI khi polling
                    if (newOrdersLiveData.getValue() == null || newOrdersLiveData.getValue().data == null) {
                        newOrdersLiveData.setValue(Resource.loading());
                    }
                    break;
                case SUCCESS:
                    if (responseResource.data != null) {
                        newOrdersLiveData.setValue(Resource.success(responseResource.data.getData()));
                    }
                    newOrdersLiveData.removeSource(source);
                    break;
                case ERROR:
                    newOrdersLiveData.setValue(Resource.error(responseResource.message, null));
                    newOrdersLiveData.removeSource(source);
                    break;
            }
        });
    }

    public void fetchStats() {
        LiveData<Resource<ShipperStatsResponse>> source = repository.getStats();
        statsLiveData.addSource(source, responseResource -> {
            statsLiveData.setValue(responseResource);
            if (responseResource.status != Resource.Status.LOADING) {
                statsLiveData.removeSource(source);
            }
        });
    }

    public void fetchWalletProfile() {
        walletProfileLiveData.setValue(Resource.loading());
        RetrofitClient.getApiService(getApplication()).getWalletProfile().enqueue(new retrofit2.Callback<com.pixibeestudio.greenly.data.model.WalletProfileResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.pixibeestudio.greenly.data.model.WalletProfileResponse> call, retrofit2.Response<com.pixibeestudio.greenly.data.model.WalletProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    walletProfileLiveData.setValue(Resource.success(response.body()));
                } else {
                    walletProfileLiveData.setValue(Resource.error("Lỗi lấy thông tin ví & hồ sơ", null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.pixibeestudio.greenly.data.model.WalletProfileResponse> call, Throwable t) {
                walletProfileLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void acceptOrder(int orderId) {
        // ... Logic cũ gọi API accept -> do tôi mới tách sang Repository nên tôi sẽ implement trong file này tạm để gọn hoặc dùng Repo nếu Repo đã có (tôi chưa viết acceptOrder trong Repo)
        // Vì nãy tôi quên viết accept/reject trong Repo, nên tôi sẽ giữ lại logic gọi API bằng Retrofit ở đây cho đơn giản, hoặc gọi qua API service.
        acceptOrderLiveData.setValue(Resource.loading());
        RetrofitClient.getApiService(getApplication()).acceptOrder(orderId).enqueue(new retrofit2.Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<com.google.gson.JsonObject> call, retrofit2.Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.google.gson.JsonObject body = response.body();
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
            public void onFailure(retrofit2.Call<com.google.gson.JsonObject> call, Throwable t) {
                acceptOrderLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void rejectOrder(int orderId) {
        rejectOrderLiveData.setValue(Resource.loading());
        RetrofitClient.getApiService(getApplication()).rejectOrder(orderId).enqueue(new retrofit2.Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<com.google.gson.JsonObject> call, retrofit2.Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.google.gson.JsonObject body = response.body();
                    if (body.has("success") && body.get("success").getAsBoolean()) {
                        rejectOrderLiveData.setValue(Resource.success("Từ chối đơn thành công"));
                        fetchNewOrders();
                        fetchStats();
                    } else {
                        String msg = body.has("message") ? body.get("message").getAsString() : "Lỗi không xác định";
                        rejectOrderLiveData.setValue(Resource.error(msg, null));
                    }
                } else {
                    rejectOrderLiveData.setValue(Resource.error("Lỗi từ chối đơn hàng", null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.google.gson.JsonObject> call, Throwable t) {
                rejectOrderLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    public void updateWorkStatus(String status) {
        updateStatusLiveData.setValue(Resource.loading());
        com.google.gson.JsonObject body = new com.google.gson.JsonObject();
        body.addProperty("work_status", status);
        RetrofitClient.getApiService(getApplication()).updateWorkStatus(body).enqueue(new retrofit2.Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<com.google.gson.JsonObject> call, retrofit2.Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.google.gson.JsonObject res = response.body();
                    if (res.has("success") && res.get("success").getAsBoolean()) {
                        updateStatusLiveData.setValue(Resource.success(res.has("message") ? res.get("message").getAsString() : "Thành công"));
                    } else {
                        String msg = res.has("message") ? res.get("message").getAsString() : "Lỗi không xác định";
                        updateStatusLiveData.setValue(Resource.error(msg, null));
                    }
                } else {
                    updateStatusLiveData.setValue(Resource.error("Lỗi cập nhật trạng thái", null));
                }
                fetchStats(); // Cập nhật lại stats sau khi đổi trạng thái
            }

            @Override
            public void onFailure(retrofit2.Call<com.google.gson.JsonObject> call, Throwable t) {
                updateStatusLiveData.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling(); // Tránh memory leak
    }
}
