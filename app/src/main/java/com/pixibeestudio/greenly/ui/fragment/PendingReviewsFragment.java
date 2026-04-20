package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.PendingReviewItem;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.ui.adapter.PendingReviewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tab 1: Danh sách sản phẩm chưa đánh giá.
 * Click "Đánh giá" → navigate sang WriteReviewFragment.
 * Nếu trống → hiển thị empty state với nút "Tiếp tục mua sắm".
 */
public class PendingReviewsFragment extends Fragment {

    private static final String TAG = "PendingReviewsFragment";

    private RecyclerView rvPending;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private MaterialButton btnBackToHome;

    private PendingReviewAdapter adapter;
    private final List<PendingReviewItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPending = view.findViewById(R.id.rv_pending_reviews);
        progressBar = view.findViewById(R.id.progress_pending);
        layoutEmpty = view.findViewById(R.id.layout_empty_pending);
        btnBackToHome = view.findViewById(R.id.btn_back_to_home);

        rvPending.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PendingReviewAdapter(items, this::openWriteReview);
        rvPending.setAdapter(adapter);

        btnBackToHome.setOnClickListener(v -> {
            // Quay về màn Home
            Navigation.findNavController(v).popBackStack(R.id.homeFragment, false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPendingReviews();
    }

    private void loadPendingReviews() {
        showLoading(true);
        RetrofitClient.getApiService(requireContext()).getPendingReviews().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray arr = response.body().getAsJsonArray("data");
                        items.clear();
                        for (int i = 0; i < arr.size(); i++) {
                            PendingReviewItem item = new com.google.gson.Gson()
                                    .fromJson(arr.get(i), PendingReviewItem.class);
                            items.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                Log.e(TAG, "Lỗi tải danh sách: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void updateEmptyState() {
        if (items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvPending.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvPending.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Mở màn Viết đánh giá với thông tin sản phẩm cần đánh giá.
     */
    private void openWriteReview(PendingReviewItem item) {
        Bundle args = new Bundle();
        args.putInt("orderDetailId", item.getOrderDetailId());
        args.putInt("productId", item.getProductId());
        args.putString("productName", item.getProductName());
        args.putString("productImage", item.getProductImage());
        args.putInt("quantity", item.getQuantity());
        args.putFloat("price", (float) item.getPrice());
        Navigation.findNavController(requireParentFragment().requireView())
                .navigate(R.id.action_myReviewsFragment_to_writeReviewFragment, args);
    }
}
