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
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình Chỉnh sửa địa chỉ - tương tự Thêm nhưng điền sẵn dữ liệu
 */
public class EditAddressFragment extends Fragment {

    private static final String TAG = "EditAddressFragment";

    private ImageButton btnBack;
    private TextInputEditText edtName, edtPhone, edtProvince, edtDistrict, edtWard, edtStreet, edtHouseNumber;
    private ChipGroup chipGroupLabel;
    private Chip chipHome, chipOffice, chipOther;
    private MaterialButton btnUpdate;

    private int addressId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressId = getArguments().getInt("addressId", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back_edit_address);
        edtName = view.findViewById(R.id.edt_edit_name);
        edtPhone = view.findViewById(R.id.edt_edit_phone);
        edtProvince = view.findViewById(R.id.edt_edit_province);
        edtDistrict = view.findViewById(R.id.edt_edit_district);
        edtWard = view.findViewById(R.id.edt_edit_ward);
        edtStreet = view.findViewById(R.id.edt_edit_street);
        edtHouseNumber = view.findViewById(R.id.edt_edit_house_number);
        chipGroupLabel = view.findViewById(R.id.chip_group_label_edit);
        chipHome = view.findViewById(R.id.chip_home_edit);
        chipOffice = view.findViewById(R.id.chip_office_edit);
        chipOther = view.findViewById(R.id.chip_other_edit);
        btnUpdate = view.findViewById(R.id.btn_update_address);

        // Điền sẵn dữ liệu từ arguments
        if (getArguments() != null) {
            edtName.setText(getArguments().getString("receiverName", ""));
            edtPhone.setText(getArguments().getString("phone", ""));
            edtProvince.setText(getArguments().getString("province", ""));
            edtDistrict.setText(getArguments().getString("district", ""));
            edtWard.setText(getArguments().getString("ward", ""));
            edtStreet.setText(getArguments().getString("street", ""));
            edtHouseNumber.setText(getArguments().getString("houseNumber", ""));

            String label = getArguments().getString("label", "home");
            switch (label) {
                case "office": chipOffice.setChecked(true); break;
                case "other": chipOther.setChecked(true); break;
                default: chipHome.setChecked(true); break;
            }
        }

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        btnUpdate.setOnClickListener(v -> updateAddress());
    }

    private void updateAddress() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String street = edtStreet.getText().toString().trim();
        String houseNumber = edtHouseNumber.getText().toString().trim();

        // Kiểm tra SĐT
        String phoneRegex = "^0[35789][0-9]{8}$";
        if (phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        } else if (!phone.matches(phoneRegex)) {
            Toast.makeText(getContext(), "Số điện thoại không hợp lệ (VD: 0912345678)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(province) ||
                TextUtils.isEmpty(district) || TextUtils.isEmpty(ward) ||
                TextUtils.isEmpty(street) || TextUtils.isEmpty(houseNumber)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác định label
        String label = "home";
        int checkedId = chipGroupLabel.getCheckedChipId();
        if (checkedId == R.id.chip_office_edit) label = "office";
        else if (checkedId == R.id.chip_other_edit) label = "other";

        JsonObject body = new JsonObject();
        body.addProperty("receiver_name", name);
        body.addProperty("phone", phone);
        body.addProperty("province", province);
        body.addProperty("district", district);
        body.addProperty("ward", ward);
        body.addProperty("street", street);
        body.addProperty("house_number", houseNumber);
        body.addProperty("label", label);

        btnUpdate.setEnabled(false);
        btnUpdate.setText("Đang cập nhật...");

        RetrofitClient.getApiService(requireContext()).updateAddress(addressId, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                btnUpdate.setEnabled(true);
                btnUpdate.setText("Cập nhật địa chỉ");

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                } else {
                    Log.e(TAG, "Lỗi cập nhật: " + response.code());
                    Toast.makeText(getContext(), "Lỗi cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnUpdate.setEnabled(true);
                btnUpdate.setText("Cập nhật địa chỉ");
                Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
