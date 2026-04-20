package com.pixibeestudio.greenly.data.network;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.BannerResponse;
import com.pixibeestudio.greenly.data.model.CartRequest;
import com.pixibeestudio.greenly.data.model.CartResponse;
import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.model.ProductDetailResponse;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.model.RegisterRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    // --- BANNER API ---
    @GET("api/banners")
    Call<BannerResponse> getBanners();

    @GET("api/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/products")
    Call<ProductResponse> getProducts();

    @GET("api/products/discounted")
    Call<ProductResponse> getDiscountedProducts();

    // --- SAN PHAM THEO DANH MUC ---
    @GET("api/products/category/{categoryId}")
    Call<ProductResponse> getProductsByCategory(@Path("categoryId") int categoryId);

    // --- LOC SAN PHAM ---
    @GET("api/products/filter")
    Call<ProductResponse> filterProducts(
            @Query("sort_by") String sortBy,
            @Query("category_id") int categoryId,
            @Query("is_discount") String isDiscount
    );

    // --- TIM KIEM SAN PHAM ---
    @GET("api/products/search")
    Call<ProductResponse> searchProducts(@Query("keyword") String keyword);

    @GET("api/products/{id}")
    Call<ProductDetailResponse> getProductDetail(@Path("id") int id);

    @POST("api/register")
    Call<JsonObject> registerUser(@Body RegisterRequest request);

    @POST("api/login")
    Call<JsonObject> loginUser(@Body com.pixibeestudio.greenly.data.model.LoginRequest request);

    // --- CART API ---
    @GET("api/carts")
    Call<CartResponse> getCarts();

    @POST("api/carts")
    Call<JsonObject> addToCart(@Body CartRequest req);

    @PUT("api/carts/{id}")
    Call<JsonObject> updateCart(@Path("id") int id, @Body CartRequest req);

    @DELETE("api/carts/{id}")
    Call<JsonObject> deleteCartItem(@Path("id") int id);

    @DELETE("api/carts/clear")
    Call<JsonObject> clearCart();

    // --- CHECKOUT API ---
    @POST("api/checkout")
    Call<JsonObject> placeOrder(@Body com.pixibeestudio.greenly.data.model.CheckoutRequest request);

    // --- XAC NHAN THANH TOAN ---
    @POST("api/orders/{id}/confirm-payment")
    Call<JsonObject> confirmPayment(@Path("id") int orderId);

    // --- KIỂM TRA TRẠNG THÁI THANH TOÁN (polling) ---
    @GET("api/payment/status/{id}")
    Call<JsonObject> checkPaymentStatus(@Path("id") int orderId);

    // --- SỔ ĐỊA CHỈ API ---
    @GET("api/addresses")
    Call<JsonObject> getAddresses();

    @POST("api/addresses")
    Call<JsonObject> createAddress(@Body JsonObject body);

    @PUT("api/addresses/{id}")
    Call<JsonObject> updateAddress(@Path("id") int id, @Body JsonObject body);

    @DELETE("api/addresses/{id}")
    Call<JsonObject> deleteAddress(@Path("id") int id);

    @POST("api/addresses/{id}/set-default")
    Call<JsonObject> setDefaultAddress(@Path("id") int id);

    // --- CUSTOMER ORDERS API ---
    @GET("api/my-orders")
    Call<com.pixibeestudio.greenly.data.model.OrderResponse> getMyOrders();

    // --- SHIPPER API ---
    @GET("api/shipper/orders/new")
    Call<com.pixibeestudio.greenly.data.model.OrderResponse> getShipperNewOrders();

    @GET("api/shipper/orders/pickup")
    Call<com.pixibeestudio.greenly.data.model.OrderResponse> getShipperPickupOrders();

    @GET("api/shipper/orders/shipping")
    Call<com.pixibeestudio.greenly.data.model.OrderResponse> getShipperShippingOrders();

    @POST("api/shipper/orders/{id}/accept")
    Call<JsonObject> acceptOrder(@Path("id") int orderId);

    @POST("api/shipper/orders/{id}/reject")
    Call<JsonObject> rejectOrder(@Path("id") int orderId);

    @POST("api/shipper/orders/{id}/pickup")
    Call<JsonObject> pickupOrder(@Path("id") int orderId);

    @POST("api/shipper/orders/{id}/complete")
    Call<JsonObject> completeOrder(@Path("id") int orderId);

    @POST("api/shipper/orders/{id}/fail")
    Call<JsonObject> failOrder(@Path("id") int orderId);

    @GET("api/shipper/stats")
    Call<com.pixibeestudio.greenly.data.model.ShipperStatsResponse> getShipperStats();

    @POST("api/shipper/work-status")
    Call<JsonObject> updateWorkStatus(@Body JsonObject body);

    @GET("api/shipper/wallet-profile")
    Call<com.pixibeestudio.greenly.data.model.WalletProfileResponse> getWalletProfile();

    // --- WISHLIST (YÊU THÍCH) API ---
    @GET("api/wishlist")
    Call<com.pixibeestudio.greenly.data.model.WishlistResponse> getWishlists();

    @POST("api/wishlist/toggle")
    Call<JsonObject> toggleFavorite(@Body JsonObject body);

    @DELETE("api/wishlist/clear")
    Call<JsonObject> clearWishlists();

    // --- REVIEWS (ĐÁNH GIÁ) API ---
    @GET("api/reviews/stats")
    Call<JsonObject> getReviewStats();

    @GET("api/reviews/pending-count")
    Call<JsonObject> getPendingReviewCount();

    @GET("api/reviews/pending")
    Call<JsonObject> getPendingReviews();

    @GET("api/reviews/my")
    Call<JsonObject> getMyReviews();

    /**
     * Tạo review mới với upload ảnh (multipart/form-data)
     * - order_detail_id: RequestBody
     * - rating: RequestBody
     * - comment: RequestBody (nullable)
     * - images[]: List<MultipartBody.Part> (nullable, max 5)
     */
    @Multipart
    @POST("api/reviews")
    Call<JsonObject> createReview(
            @Part("order_detail_id") RequestBody orderDetailId,
            @Part("rating") RequestBody rating,
            @Part("comment") RequestBody comment,
            @Part List<MultipartBody.Part> images
    );
}

