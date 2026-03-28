package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.pixibeestudio.greenly.R;

public class AddAddressFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Placeholder - hiển thị thông báo tạm thời
        Toast.makeText(getContext(), "Chức năng thêm địa chỉ đang được phát triển", Toast.LENGTH_SHORT).show();
        
        // Tự động quay lại sau khi hiển thị
        view.postDelayed(() -> {
            if (isAdded()) {
                Navigation.findNavController(view).popBackStack();
            }
        }, 2000);
    }
}
