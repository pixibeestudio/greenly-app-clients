package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình Thêm địa chỉ mới
 * Hỗ trợ 2 mode qua argument "mode":
 * - "add": Lưu địa chỉ vào hệ thống qua API → hiển thị ở Sổ địa chỉ
 * - "temp": Không lưu vào hệ thống, chỉ lưu tạm vào Session → quay về Checkout
 */
public class AddAddressFragment extends Fragment {

    private static final String TAG = "AddAddressFragment";

    private ImageButton btnBackAddAddress;
    private TextInputEditText edtNameAddress, edtPhoneAddress, edtProvinceAddress, edtDistrictAddress, edtWardAddress, edtStreetAddress, edtHouseNumberAddress;
    private ChipGroup chipGroupLabel;
    private Chip chipHome, chipOffice, chipOther;
    private MaterialButton btnSaveAddress;
    private SessionManager sessionManager;

    // "add" = lưu API, "temp" = lưu tạm Session
    private String mode = "add";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString("mode", "add");
        }
    }

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

        // Tự động điền SĐT nếu có
        String userPhone = sessionManager.getUserPhone();
        if (userPhone != null && !userPhone.isEmpty()) {
            edtPhoneAddress.setText(userPhone);
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
        chipGroupLabel = view.findViewById(R.id.chip_group_label);
        chipHome = view.findViewById(R.id.chip_home);
        chipOffice = view.findViewById(R.id.chip_office);
        chipOther = view.findViewById(R.id.chip_other);
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

        // Kiểm tra định dạng số điện thoại Việt Nam
        String phoneRegex = "^0[35789][0-9]{8}$";

        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        } else if (!phone.matches(phoneRegex)) {
            Toast.makeText(getContext(), "Số điện thoại không hợp lệ (VD: 0912345678)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(province) ||
                TextUtils.isEmpty(district) || TextUtils.isEmpty(ward) || TextUtils.isEmpty(street) ||
                TextUtils.isEmpty(houseNumber)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = houseNumber + ", " + street + ", " + ward + ", " + district + ", " + province;

        // Xác định label
        String label = "home";
        if (chipGroupLabel != null) {
            int checkedId = chipGroupLabel.getCheckedChipId();
            if (checkedId == R.id.chip_office) label = "office";
            else if (checkedId == R.id.chip_other) label = "other";
        }

        if ("temp".equals(mode)) {
            // MODE TEMP: Không lưu API, chỉ lưu tạm vào Session → quay về Checkout
            sessionManager.saveShippingInfo(phone, fullAddress);
            sessionManager.saveShippingReceiverName(name);
            Toast.makeText(getContext(), "Đã sử dụng địa chỉ tạm thời", Toast.LENGTH_SHORT).show();
            // Pop 2 lần: AddAddress → AddressBook → Checkout
            Navigation.findNavController(view).popBackStack(R.id.checkoutFragment, false);
        } else {
            // MODE ADD: Lưu vào hệ thống qua API
            saveAddressToApi(name, phone, province, district, ward, street, houseNumber, label, view);
        }
    }

    private void saveAddressToApi(String name, String phone, String province, String district,
                                   String ward, String street, String houseNumber, String label, View view) {
        JsonObject body = new JsonObject();
        body.addProperty("receiver_name", name);
        body.addProperty("phone", phone);
        body.addProperty("province", province);
        body.addProperty("district", district);
        body.addProperty("ward", ward);
        body.addProperty("street", street);
        body.addProperty("house_number", houseNumber);
        body.addProperty("label", label);

        btnSaveAddress.setEnabled(false);
        btnSaveAddress.setText("Đang lưu...");

        RetrofitClient.getApiService(requireContext()).createAddress(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                btnSaveAddress.setEnabled(true);
                btnSaveAddress.setText("Lưu địa chỉ");

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã lưu địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                } else {
                    Log.e(TAG, "Lỗi lưu địa chỉ: " + response.code());
                    Toast.makeText(getContext(), "Lỗi lưu địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnSaveAddress.setEnabled(true);
                btnSaveAddress.setText("Lưu địa chỉ");
                Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
