package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;

public class AddAddressFragment extends Fragment {

    private ImageButton btnBackAddAddress;
    private TextInputEditText edtNameAddress, edtPhoneAddress, edtProvinceAddress, edtDistrictAddress, edtWardAddress, edtStreetAddress, edtHouseNumberAddress;
    private MaterialButton btnSaveAddress;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());
        initViews(view);
        setupListeners();
        
        // Tự động điền tên người dùng từ Session
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            edtNameAddress.setText(userName);
        }
        
        // Tự động điền SĐT đã lưu (nếu có)
        String savedPhone = sessionManager.getShippingPhone();
        if (savedPhone != null && !savedPhone.isEmpty()) {
            edtPhoneAddress.setText(savedPhone);
        }
        
        // Tự động điền địa chỉ cũ (nếu có)
        String savedAddress = sessionManager.getShippingAddress();
        if (savedAddress != null && !savedAddress.isEmpty() && savedAddress.contains(",")) {
            String[] parts = savedAddress.split(",");
            // Cấu trúc lưu: "Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành phố"
            if (parts.length >= 5) {
                edtHouseNumberAddress.setText(parts[0].trim());
                edtStreetAddress.setText(parts[1].trim());
                edtWardAddress.setText(parts[2].trim());
                edtDistrictAddress.setText(parts[3].trim());
                edtProvinceAddress.setText(parts[4].trim());
            }
        }
    }

    private void initViews(View view) {
        btnBackAddAddress = view.findViewById(R.id.btnBackAddAddress);
        edtNameAddress = view.findViewById(R.id.edtNameAddress);
        edtPhoneAddress = view.findViewById(R.id.edtPhoneAddress);
        edtProvinceAddress = view.findViewById(R.id.edtProvinceAddress);
        edtDistrictAddress = view.findViewById(R.id.edtDistrictAddress);
        edtWardAddress = view.findViewById(R.id.edtWardAddress);
        edtStreetAddress = view.findViewById(R.id.edtStreetAddress);
        edtHouseNumberAddress = view.findViewById(R.id.edtHouseNumberAddress);
        btnSaveAddress = view.findViewById(R.id.btnSaveAddress);
    }

    private void setupListeners() {
        btnBackAddAddress.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        btnSaveAddress.setOnClickListener(v -> {
            saveAddress(v);
        });
    }

    private void saveAddress(View view) {
        String name = edtNameAddress.getText().toString().trim();
        String phone = edtPhoneAddress.getText().toString().trim();
        String province = edtProvinceAddress.getText().toString().trim();
        String district = edtDistrictAddress.getText().toString().trim();
        String ward = edtWardAddress.getText().toString().trim();
        String street = edtStreetAddress.getText().toString().trim();
        String houseNumber = edtHouseNumberAddress.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(province) || 
            TextUtils.isEmpty(district) || TextUtils.isEmpty(ward) || TextUtils.isEmpty(street) || 
            TextUtils.isEmpty(houseNumber)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo chuỗi địa chỉ đầy đủ
        String fullAddress = houseNumber + ", " + street + ", " + ward + ", " + district + ", " + province;

        // Lưu vào SessionManager
        sessionManager.saveShippingInfo(phone, fullAddress);

        Toast.makeText(getContext(), "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).popBackStack();
    }
}
