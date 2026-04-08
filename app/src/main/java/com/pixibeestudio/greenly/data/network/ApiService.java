package com.pixibeestudio.greenly.data.network;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.CartRequest;
import com.pixibeestudio.greenly.data.model.CartResponse;
import com.pixibeestudio.greenly.data.model.CategoryResponse;
import com.pixibeestudio.greenly.data.model.ProductDetailResponse;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/products")
    Call<ProductResponse> getProducts();

    @GET("api/products/discounted")
    Call<ProductResponse> getDiscountedProducts();

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
}

