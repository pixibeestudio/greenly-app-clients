package com.pixibeestudio.greenly.data.network;

import android.content.Context;

import com.pixibeestudio.greenly.data.local.SessionManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.2.200:8000/";
    private static Retrofit retrofit = null;
    // Lưu context riêng để interceptor luôn dùng được context mới nhất
    private static Context appContext = null;

    public static Retrofit getClient(Context context) {
        // Cập nhật context nếu có truyền vào (dùng ApplicationContext để tránh memory leak)
        if (context != null) {
            appContext = context.getApplicationContext();
        }

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Thêm Interceptor để tự động gắn Token
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    // Dùng appContext (static) thay vì closure context
                    if (appContext != null) {
                        SessionManager sessionManager = new SessionManager(appContext);
                        String token = sessionManager.getAuthToken();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                    }

                    // Thêm header Accept: application/json cho tất cả API
                    requestBuilder.header("Accept", "application/json");

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
    
    // Giữ lại hàm cũ cho các chỗ không cần truyền context hoặc chưa cập nhật
    public static ApiService getApiService() {
        return getClient(null).create(ApiService.class);
    }
}
