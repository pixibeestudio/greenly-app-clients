package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.Product;
import com.pixibeestudio.greenly.data.model.ProductResponse;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel quản lý việc gọi API lọc sản phẩm.
 */
public class FilterViewModel extends AndroidViewModel {

    private static final String TAG = "FilterViewModel";
    private final MutableLiveData<List<Product>> filteredProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final ApiService apiService;

    public FilterViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getClient(application).create(ApiService.class);
    }

    public LiveData<List<Product>> getFilteredProductsLiveData() {
        return filteredProductsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Gọi API lọc sản phẩm với các tham số.
     * @param sortBy      Loại sắp xếp: newest, top_sales, price_asc, price_desc, default
     * @param categoryId  ID danh mục (0 = tất cả)
     * @param isDiscount  true nếu chỉ lấy SP đang giảm giá
     */
    public void fetchFilteredProducts(String sortBy, int categoryId, boolean isDiscount) {
        isLoading.setValue(true);

        String discountStr = isDiscount ? "true" : "false";

        apiService.filterProducts(sortBy, categoryId, discountStr).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    filteredProductsLiveData.setValue(response.body().getData());
                    Log.d(TAG, "Lọc thành công: " + response.body().getData().size() + " sản phẩm");
                } else {
                    filteredProductsLiveData.setValue(null);
                    Log.e(TAG, "Lỗi API filter: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                filteredProductsLiveData.setValue(null);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
