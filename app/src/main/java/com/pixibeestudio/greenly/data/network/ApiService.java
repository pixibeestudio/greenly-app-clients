package com.pixibeestudio.greenly.data.network;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.model.ProductDetailResponse;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/products")
    Call<ProductResponse> getProducts();

    @GET("api/products/discounted")
    Call<ProductResponse> getDiscountedProducts();

    @GET("api/products/{id}")
    Call<ProductDetailResponse> getProductDetail(@Path("id") int id);

    @POST("api/register")
    Call<JsonObject> registerUser(@Body RegisterRequest request);
}

