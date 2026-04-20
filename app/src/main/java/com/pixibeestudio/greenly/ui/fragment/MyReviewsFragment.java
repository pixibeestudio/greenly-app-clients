package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.ui.adapter.MyReviewsPagerAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn Đánh giá của tôi - host 2 tab: Chưa đánh giá / Đã đánh giá
 */
public class MyReviewsFragment extends Fragment {

    private static final String TAG = "MyReviewsFragment";

    private ImageButton btnBack;
    private ShapeableImageView ivAvatar;
    private TextView tvTotalReviews;
    private TextView tvPendingReviews;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back_my_reviews);
        ivAvatar = view.findViewById(R.id.iv_user_avatar_reviews);
        tvTotalReviews = view.findViewById(R.id.tv_total_reviews);
        tvPendingReviews = view.findViewById(R.id.tv_pending_reviews);
        tabLayout = view.findViewById(R.id.tabs_my_reviews);
        viewPager = view.findViewById(R.id.viewpager_my_reviews);

        setupUser();
        setupViewPager();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void setupUser() {
        SessionManager session = new SessionManager(requireContext());
        String avatar = session.getUserAvatar();
        Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.ic_default_avatar_placeholder)
                .error(R.drawable.ic_default_avatar_placeholder)
                .centerCrop()
                .into(ivAvatar);
    }

    private void setupViewPager() {
        MyReviewsPagerAdapter adapter = new MyReviewsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Gắn TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Chưa đánh giá" : "Đã đánh giá");
        }).attach();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    /**
     * Load stats: tổng đánh giá + chưa đánh giá
     */
    private void loadStats() {
        RetrofitClient.getApiService(requireContext()).getReviewStats().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject data = response.body().getAsJsonObject("data");
                        tvTotalReviews.setText(String.valueOf(data.get("total_reviews").getAsInt()));
                        tvPendingReviews.setText(String.valueOf(data.get("pending_count").getAsInt()));
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse stats: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(TAG, "Lỗi load stats: " + t.getMessage());
            }
        });
    }
}
