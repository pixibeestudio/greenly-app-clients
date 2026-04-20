package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pixibeestudio.greenly.R;

/**
 * BottomSheet cho phép khách chọn "Chụp ảnh mới" hoặc "Chọn từ thư viện".
 * Dùng trong WriteReviewFragment.
 */
public class ImagePickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnPickListener {
        void onPickCamera();
        void onPickGallery();
    }

    private OnPickListener listener;

    public void setOnPickListener(OnPickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_image_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout optionCamera = view.findViewById(R.id.option_camera);
        LinearLayout optionGallery = view.findViewById(R.id.option_gallery);
        TextView optionCancel = view.findViewById(R.id.option_cancel);

        optionCamera.setOnClickListener(v -> {
            if (listener != null) listener.onPickCamera();
            dismiss();
        });

        optionGallery.setOnClickListener(v -> {
            if (listener != null) listener.onPickGallery();
            dismiss();
        });

        optionCancel.setOnClickListener(v -> dismiss());
    }
}
