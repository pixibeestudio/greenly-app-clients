package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.model.Address;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.ui.adapter.AddressAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình Sổ địa chỉ - hiển thị danh sách địa chỉ đã lưu
 * Argument "source": "checkout" hoặc "profile" để xác định luồng
 */
public class AddressBookFragment extends Fragment implements AddressAdapter.OnAddressActionListener {

    private static final String TAG = "AddressBookFragment";

    private ImageButton btnBack;
    private MaterialButton btnAddNew;
    private MaterialButton btnTemp;
    private RecyclerView rvAddresses;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;

    private AddressAdapter adapter;
    private String source = "checkout"; // "checkout" hoặc "profile"

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = getArguments().getString("source", "checkout");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back_address_book);
        btnAddNew = view.findViewById(R.id.btn_add_new_address);
        btnTemp = view.findViewById(R.id.btn_temp_address);
        rvAddresses = view.findViewById(R.id.rv_addresses);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        progressBar = view.findViewById(R.id.progress_bar);

        // Ẩn nút "Khác" nếu đến từ Profile
        if ("profile".equals(source)) {
            btnTemp.setVisibility(View.GONE);
        }

        adapter = new AddressAdapter(this);
        rvAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAddresses.setAdapter(adapter);

        // Nút Back
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Nút Thêm địa chỉ mới → navigate tới AddAddressFragment với mode=add
        btnAddNew.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("mode", "add");
            Navigation.findNavController(v).navigate(R.id.action_addressBookFragment_to_addAddressFragment, args);
        });

        // Nút Khác → navigate tới AddAddressFragment với mode=temp
        btnTemp.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("mode", "temp");
            Navigation.findNavController(v).navigate(R.id.action_addressBookFragment_to_addAddressFragment, args);
        });

        // Load danh sách
        loadAddresses();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload mỗi khi quay lại (sau khi thêm/sửa)
        loadAddresses();
    }

    private void loadAddresses() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvAddresses.setVisibility(View.GONE);

        RetrofitClient.getApiService(requireContext()).getAddresses().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("data") && body.get("data").isJsonArray()) {
                        JsonArray arr = body.getAsJsonArray("data");
                        List<Address> list = parseAddresses(arr);

                        if (list.isEmpty()) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            rvAddresses.setVisibility(View.GONE);
                        } else {
                            layoutEmpty.setVisibility(View.GONE);
                            rvAddresses.setVisibility(View.VISIBLE);
                            adapter.setAddressList(list);
                        }
                    }
                } else {
                    Log.e(TAG, "Lỗi tải danh sách địa chỉ: " + response.code());
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Log.e(TAG, "Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private List<Address> parseAddresses(JsonArray arr) {
        List<Address> list = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            Address addr = new Address();
            addr.setId(obj.get("id").getAsInt());
            addr.setReceiverName(obj.has("receiver_name") ? obj.get("receiver_name").getAsString() : "");
            addr.setPhone(obj.has("phone") ? obj.get("phone").getAsString() : "");
            addr.setProvince(obj.has("province") ? obj.get("province").getAsString() : "");
            addr.setDistrict(obj.has("district") ? obj.get("district").getAsString() : "");
            addr.setWard(obj.has("ward") ? obj.get("ward").getAsString() : "");
            addr.setStreet(obj.has("street") ? obj.get("street").getAsString() : "");
            addr.setHouseNumber(obj.has("house_number") ? obj.get("house_number").getAsString() : "");
            addr.setFullAddress(obj.has("full_address") ? obj.get("full_address").getAsString() : "");
            addr.setLabel(obj.has("label") ? obj.get("label").getAsString() : "home");
            addr.setDefault(obj.has("is_default") && obj.get("is_default").getAsBoolean());
            list.add(addr);
        }
        return list;
    }

    // --- AddressAdapter callbacks ---

    @Override
    public void onAddressClick(Address address) {
        // Chỉ cho chọn khi đến từ Checkout
        if ("checkout".equals(source)) {
            // Lưu vào SessionManager và quay về Checkout
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.saveShippingInfo(address.getPhone(), address.getFullAddress());
            // Lưu thêm tên người nhận
            sessionManager.saveShippingReceiverName(address.getReceiverName());

            Navigation.findNavController(requireView()).popBackStack();
        }
    }

    @Override
    public void onEditClick(Address address) {
        Bundle args = new Bundle();
        args.putInt("addressId", address.getId());
        args.putString("receiverName", address.getReceiverName());
        args.putString("phone", address.getPhone());
        args.putString("province", address.getProvince());
        args.putString("district", address.getDistrict());
        args.putString("ward", address.getWard());
        args.putString("street", address.getStreet());
        args.putString("houseNumber", address.getHouseNumber());
        args.putString("label", address.getLabel());
        Navigation.findNavController(requireView()).navigate(R.id.action_addressBookFragment_to_editAddressFragment, args);
    }

    @Override
    public void onDeleteClick(Address address) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAddress(address.getId()))
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteAddress(int addressId) {
        RetrofitClient.getApiService(requireContext()).deleteAddress(addressId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                } else {
                    Toast.makeText(getContext(), "Lỗi xóa địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
