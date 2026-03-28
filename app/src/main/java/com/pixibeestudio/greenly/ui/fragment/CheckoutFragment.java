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
            Toast.makeText(getContext(), "Chức năng đặt hàng đang được phát triển", Toast.LENGTH_SHORT).show();
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
