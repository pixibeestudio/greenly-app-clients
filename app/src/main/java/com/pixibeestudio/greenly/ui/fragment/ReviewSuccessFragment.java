package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;

/**
 * Màn Cảm ơn sau khi gửi đánh giá thành công.
 * Cả Back và nút "Quay về" đều popBackStack tới MyReviews.
 */
public class ReviewSuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnBack = view.findViewById(R.id.btn_back_success);
        MaterialButton btnBackToReviews = view.findViewById(R.id.btn_back_to_reviews);

        View.OnClickListener popToMyReviews = v -> {
            // Pop tới myReviewsFragment (bỏ qua WriteReview và Success)
            Navigation.findNavController(v).popBackStack(R.id.myReviewsFragment, false);
        };
        btnBack.setOnClickListener(popToMyReviews);
        btnBackToReviews.setOnClickListener(popToMyReviews);

        // Bắt nút Back vật lý
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigation.findNavController(view).popBackStack(R.id.myReviewsFragment, false);
                    }
                }
        );
    }
}
