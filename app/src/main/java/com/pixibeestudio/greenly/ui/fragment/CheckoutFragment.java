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
import com.pixibeestudio.greenly.ui.viewmodel.CheckoutViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.pixibeestudio.greenly.R;

import com.pixibeestudio.greenly.data.local.SessionManager;

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
        loadShippingInfo();
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
        String userName = sessionManager.getUserName();
        
        if (address != null && !address.isEmpty() && phone != null && !phone.isEmpty()) {
            String nameToDisplay = (userName != null && !userName.isEmpty()) ? userName : "Người nhận";
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
            Navigation.findNavController(v).navigate(R.id.action_checkoutFragment_to_addAddressFragment);
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
        String userName = sessionManager.getUserName();
        String name = (userName != null && !userName.isEmpty()) ? userName : "Khách hàng";
        
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
                    
                    // Tinh tong tien
                    int totalAmount = (int) (subtotal + shippingFee);
                    // orderId tam thoi dung 0 (API placeOrder chi tra ve Boolean)
                    int orderId = 0;
                    
                    // Chia luong dieu huong theo phuong thuc thanh toan
                    // Dung selectedPaymentId vi paymentMethod da doi thanh "banking"
                    if (selectedPaymentId == R.id.rbPaymentVietQR) {
                        // Chuyen sang man hinh QR
                        Bundle args = new Bundle();
                        args.putInt("totalAmount", totalAmount);
                        args.putInt("orderId", orderId);
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

    private void updateTotals() {
        double grandTotal = subtotal + shippingFee;
        
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        
        tvCheckoutSubtotal.setText(format.format(subtotal));
        tvCheckoutShipping.setText(format.format(shippingFee));
        tvCheckoutGrandTotal.setText(format.format(grandTotal));
    }
}
