package com.pixibeestudio.greenly.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.ErrorResponse;
import com.pixibeestudio.greenly.data.model.RegisterRequest;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.utils.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {
        apiService = RetrofitClient.getApiService(context);
    }

    public MutableLiveData<Resource<JsonObject>> register(RegisterRequest request) {
        MutableLiveData<Resource<JsonObject>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.registerUser(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    // Xử lý lỗi từ API, đặc biệt là lỗi 422 Validation
                    if (response.code() == 422 && response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            ErrorResponse errorResponse = new Gson().fromJson(errorString, ErrorResponse.class);
                            result.setValue(Resource.error("Lỗi xác thực dữ liệu", errorResponse));
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.setValue(Resource.error("Có lỗi xảy ra khi xử lý lỗi từ server.", null));
                        }
                    } else {
                        result.setValue(Resource.error("Lỗi đăng ký. Mã lỗi: " + response.code(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                result.setValue(Resource.error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra mạng.", null));
            }
        });

        return result;
    }

    public MutableLiveData<Resource<JsonObject>> login(com.pixibeestudio.greenly.data.model.LoginRequest request) {
        MutableLiveData<Resource<JsonObject>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.loginUser(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    // Xử lý lỗi từ API, đặc biệt là lỗi 422 Validation
                    if (response.code() == 422 && response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            ErrorResponse errorResponse = new Gson().fromJson(errorString, ErrorResponse.class);
                            result.setValue(Resource.error("Lỗi xác thực dữ liệu", errorResponse));
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.setValue(Resource.error("Có lỗi xảy ra khi xử lý lỗi từ server.", null));
                        }
                    } else {
                        result.setValue(Resource.error("Lỗi đăng nhập. Vui lòng kiểm tra lại thông tin.", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                result.setValue(Resource.error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra mạng.", null));
            }
        });

        return result;
    }
}
