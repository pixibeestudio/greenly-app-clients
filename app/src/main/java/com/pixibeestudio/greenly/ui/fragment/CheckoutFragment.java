package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProvider;
import com.pixibeestudio.greenly.data.model.CheckoutRequest;
import com.pixibeestudio.greenly.data.model.CheckoutResult;
import com.pixibeestudio.greenly.ui.viewmodel.CheckoutViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;

import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private ImageButton btnBackCheckout;
    private TextView btnEditAddress;
    private TextView tvCheckoutNamePhone;
    private TextView tvCheckoutAddress;
    private RadioGroup rgShippingMethod;
    private RadioGroup rgPaymentMethod;
    private TextInputEditText etCheckoutNote;
    
    private TextView tvCheckoutSubtotal;
    private TextView tvCheckoutShipping;
    private TextView tvCheckoutGrandTotal;
    private MaterialButton btnPlaceOrder;
    
    private SessionManager sessionManager;
    private CheckoutViewModel checkoutViewModel;
    private ProgressDialog progressDialog;

    private double subtotal = 0;
    private double shippingFee = 20000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sessionManager = new SessionManager(requireContext());
        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
        
        // Lấy subtotal từ arguments
        if (getArguments() != null) {
            subtotal = getArguments().getDouble("subtotal", 0);
        }
        
        initViews(view);
        setupListeners();
        updateTotals();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Nếu chưa có shipping info → thử load địa chỉ mặc định từ API
        String existingAddress = sessionManager.getShippingAddress();
        if (existingAddress == null || existingAddress.isEmpty()) {
            loadDefaultAddress();
        } else {
            loadShippingInfo();
        }
        // Kiem tra lai gio hang, neu trong thi chuyen ve gio hang
        if (subtotal <= 0) {
            Toast.makeText(getContext(), "Giỏ hàng trống, vui lòng thêm sản phẩm", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack(R.id.cartFragment, false);
        }
    }

    private void initViews(View view) {
        btnBackCheckout = view.findViewById(R.id.btnBackCheckout);
        btnEditAddress = view.findViewById(R.id.btnEditAddress);
        tvCheckoutNamePhone = view.findViewById(R.id.tvCheckoutNamePhone);
        tvCheckoutAddress = view.findViewById(R.id.tvCheckoutAddress);
        rgShippingMethod = view.findViewById(R.id.rgShippingMethod);
        rgPaymentMethod = view.findViewById(R.id.rgPaymentMethod);
        etCheckoutNote = view.findViewById(R.id.etCheckoutNote);
        
        tvCheckoutSubtotal = view.findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutShipping = view.findViewById(R.id.tvCheckoutShipping);
        tvCheckoutGrandTotal = view.findViewById(R.id.tvCheckoutGrandTotal);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
    }

    private void loadShippingInfo() {
        String phone = sessionManager.getShippingPhone();
        String address = sessionManager.getShippingAddress();
        String receiverName = sessionManager.getShippingReceiverName();
        String userName = sessionManager.getUserName();
        
        if (address != null && !address.isEmpty() && phone != null && !phone.isEmpty()) {
            // Ưu tiên tên người nhận riêng (từ Sổ địa chỉ), rồi đến tên user
            String nameToDisplay = (receiverName != null && !receiverName.isEmpty()) ? receiverName :
                    (userName != null && !userName.isEmpty()) ? userName : "Người nhận";
            tvCheckoutNamePhone.setText(nameToDisplay + " - " + phone);
            tvCheckoutAddress.setText(address);
        } else {
            tvCheckoutNamePhone.setText("Người nhận: Chưa có");
            tvCheckoutAddress.setText("Vui lòng cập nhật địa chỉ giao hàng");
        }
    }

    private void setupListeners() {
        btnBackCheckout.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        btnEditAddress.setOnClickListener(v -> {
            Bundle addressArgs = new Bundle();
            addressArgs.putString("source", "checkout");
            Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_addressBookFragment, addressArgs);
        });

        rgShippingMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbShippingExpress) {
                shippingFee = 40000;
            } else {
                shippingFee = 20000;
            }
            updateTotals();
        });

        btnPlaceOrder.setOnClickListener(v -> {
            placeOrder();
        });
    }

    private void placeOrder() {
        // Kiem tra gio hang co san pham khong
        if (subtotal <= 0) {
            Toast.makeText(getContext(), "Giỏ hàng trống, không thể đặt hàng", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack(R.id.cartFragment, false);
            return;
        }
        String phone = sessionManager.getShippingPhone();
        String address = sessionManager.getShippingAddress();
        String receiverName = sessionManager.getShippingReceiverName();
        String userName = sessionManager.getUserName();
        // Ưu tiên tên người nhận riêng (từ Sổ địa chỉ), rồi đến tên user
        String name = (receiverName != null && !receiverName.isEmpty()) ? receiverName :
                (userName != null && !userName.isEmpty()) ? userName : "Khách hàng";
        
        if (address == null || address.isEmpty() || phone == null || phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm địa chỉ nhận hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = etCheckoutNote.getText() != null ? etCheckoutNote.getText().toString().trim() : "";
        
        String shippingMethod = rgShippingMethod.getCheckedRadioButtonId() == R.id.rbShippingExpress ? "Hoa_toc" : "Nhanh";
        // Lay ID cua RadioButton duoc chon de xac dinh phuong thuc thanh toan
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        String paymentMethod = (selectedPaymentId == R.id.rbPaymentVietQR) ? "banking" : "COD";

        CheckoutRequest request = new CheckoutRequest(
            name,
            phone,
            address,
            shippingMethod,
            paymentMethod,
            (int) shippingFee,
            notes
        );

        checkoutViewModel.placeOrder(request).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressDialog.show();
                    break;
                case SUCCESS:
                    progressDialog.dismiss();
                    sessionManager.clearShippingInfo();
                    Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Lay orderId va grandTotal tu backend response
                    CheckoutResult checkoutResult = resource.data;
                    int orderId = (checkoutResult != null) ? checkoutResult.getOrderId() : 0;
                    int grandTotal = (checkoutResult != null) ? checkoutResult.getGrandTotal() : (int)(subtotal + shippingFee);
                    
                    // Chia luong dieu huong theo phuong thuc thanh toan
                    if (selectedPaymentId == R.id.rbPaymentVietQR) {
                        // Lấy thêm orderCode và paymentUrl từ backend response
                        String orderCodeStr = (checkoutResult != null) ? checkoutResult.getOrderCode() : "";
                        String paymentUrlStr = (checkoutResult != null) ? checkoutResult.getPaymentUrl() : "";

                        // Chuyen sang man hinh QR voi đầy đủ thông tin
                        Bundle args = new Bundle();
                        args.putInt("totalAmount", grandTotal);
                        args.putInt("orderId", orderId);
                        args.putString("orderCode", orderCodeStr);
                        args.putString("paymentUrl", paymentUrlStr);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_checkoutFragment_to_paymentQrFragment, args);
                    } else {
                        // COD - Chuyen sang man hinh thanh toan thanh cong
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_checkoutFragment_to_orderSuccessFragment);
                    }
                    break;
                case ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * Tự động load địa chỉ mặc định từ API khi vào Checkout lần đầu
     */
    private void loadDefaultAddress() {
        RetrofitClient.getApiService(requireContext()).getAddresses().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("data") && body.get("data").isJsonArray()) {
                        JsonArray arr = body.getAsJsonArray("data");
                        // Tìm địa chỉ mặc định (item đầu tiên vì backend đã sort is_default DESC)
                        if (arr.size() > 0) {
                            JsonObject first = arr.get(0).getAsJsonObject();
                            boolean isDefault = first.has("is_default") && first.get("is_default").getAsBoolean();
                            if (isDefault) {
                                String name = first.has("receiver_name") ? first.get("receiver_name").getAsString() : "";
                                String phone = first.has("phone") ? first.get("phone").getAsString() : "";
                                String fullAddr = first.has("full_address") ? first.get("full_address").getAsString() : "";
                                sessionManager.saveShippingInfo(phone, fullAddr);
                                sessionManager.saveShippingReceiverName(name);
                            }
                        }
                    }
                }
                loadShippingInfo();
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e("CheckoutFragment", "Lỗi load địa chỉ mặc định: " + t.getMessage());
                loadShippingInfo();
            }
        });
    }

    private void updateTotals() {
        double grandTotal = subtotal + shippingFee;
        
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        
        tvCheckoutSubtotal.setText(format.format(subtotal));
        tvCheckoutShipping.setText(format.format(shippingFee));
        tvCheckoutGrandTotal.setText(format.format(grandTotal));
    }
}
