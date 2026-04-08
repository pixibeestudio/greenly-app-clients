package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.data.model.WishlistItem;
import com.pixibeestudio.greenly.data.repository.FavoriteRepository;

import java.util.List;

/**
 * ViewModel cho màn hình Yêu thích.
 * Quản lý LiveData danh sách wishlist và các thao tác toggle/clear.
 */
public class FavoriteViewModel extends AndroidViewModel {
    private final FavoriteRepository favoriteRepository;
    private MutableLiveData<List<WishlistItem>> favoriteListLiveData;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application.getApplicationContext());
    }

    /**
     * Lấy danh sách yêu thích từ API.
     * Mỗi lần gọi sẽ tạo request mới (giống pattern CartViewModel).
     */
    public LiveData<List<WishlistItem>> getFavorites() {
        favoriteListLiveData = favoriteRepository.getWishlists();
        return favoriteListLiveData;
    }

    /**
     * Toggle yêu thích sản phẩm (thêm/xóa).
     * Trả về JsonObject chứa: { success, message, is_favorite }
     */
    public LiveData<JsonObject> toggleFavorite(int productId) {
        return favoriteRepository.toggleFavorite(productId);
    }

    /**
     * Xóa toàn bộ danh sách yêu thích.
     * Trả về Boolean: true nếu thành công.
     */
    public LiveData<Boolean> clearAllFavorites() {
        return favoriteRepository.clearWishlists();
    }
}
