package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Cart;
import com.pixibeestudio.greenly.data.repository.CartRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private MutableLiveData<Double> subtotalLiveData = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> grandTotalLiveData = new MutableLiveData<>(0.0);
    private final double SHIPPING_FEE = 20000.0;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application.getApplicationContext());
    }

    public LiveData<List<Cart>> getCarts() {
        return cartRepository.getCarts();
    }

    public LiveData<Boolean> addToCart(int productId, int quantity) {
        return cartRepository.addToCart(productId, quantity);
    }

    public LiveData<Boolean> updateCart(int cartId, int quantity) {
        return cartRepository.updateCart(cartId, quantity);
    }

    public LiveData<Boolean> deleteCartItem(int cartId) {
        return cartRepository.deleteCartItem(cartId);
    }

    public LiveData<Boolean> clearCart() {
        return cartRepository.clearCart();
    }

    public void calculateTotals(List<Cart> cartList) {
        double subtotal = 0.0;
        if (cartList != null) {
            for (Cart cart : cartList) {
                if (cart.getProduct() != null) {
                    double price = cart.getProduct().getDiscountPrice() > 0 
                            ? cart.getProduct().getDiscountPrice() 
                            : cart.getProduct().getPrice();
                    subtotal += price * cart.getQuantity();
                }
            }
        }
        subtotalLiveData.setValue(subtotal);
        
        if (subtotal > 0) {
            grandTotalLiveData.setValue(subtotal + SHIPPING_FEE);
        } else {
            grandTotalLiveData.setValue(0.0);
        }
    }

    public LiveData<Double> getSubtotalLiveData() {
        return subtotalLiveData;
    }

    public LiveData<Double> getGrandTotalLiveData() {
        return grandTotalLiveData;
    }
}
