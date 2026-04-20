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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Review;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.ui.adapter.CompletedReviewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tab 2: Danh sách review đã đánh giá của user.
 */
public class CompletedReviewsFragment extends Fragment {

    private static final String TAG = "CompletedReviewsFrag";

    private RecyclerView rvCompleted;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    private CompletedReviewAdapter adapter;
    private final List<Review> reviews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCompleted = view.findViewById(R.id.rv_completed_reviews);
        progressBar = view.findViewById(R.id.progress_completed);
        layoutEmpty = view.findViewById(R.id.layout_empty_completed);

        rvCompleted.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CompletedReviewAdapter(reviews);
        rvCompleted.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMyReviews();
    }

    private void loadMyReviews() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService(requireContext()).getMyReviews().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonArray arr = response.body().getAsJsonArray("data");
                        reviews.clear();
                        Gson gson = new Gson();
                        for (int i = 0; i < arr.size(); i++) {
                            Review r = gson.fromJson(arr.get(i), Review.class);
                            reviews.add(r);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse reviews: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Lỗi tải reviews: " + t.getMessage());
            }
        });
    }

    private void updateEmptyState() {
        if (reviews.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCompleted.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCompleted.setVisibility(View.VISIBLE);
        }
    }
}
