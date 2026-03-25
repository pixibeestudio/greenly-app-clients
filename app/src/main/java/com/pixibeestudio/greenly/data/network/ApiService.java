package com.pixibeestudio.greenly.data.network;

import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/products")
    Call<ProductResponse> getProducts();

    @GET("api/products/discounted")
    Call<ProductResponse> getDiscountedProducts();
}
