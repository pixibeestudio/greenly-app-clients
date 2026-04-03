package com.pixibeestudio.greenly.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentQrFragment extends Fragment {

    private static final String ARG_TOTAL_AMOUNT = "totalAmount";
    private static final String ARG_ORDER_ID = "orderId";

    private ImageButton btnBack;
    private ImageView imgQrCode;
    private TextView tvPaymentAmount;
    private MaterialButton btnPaymentDone;

    private int totalAmount;
    private int orderId;

    /**
     * Tạo instance của PaymentQrFragment với arguments
     * @param totalAmount Số tiền cần thanh toán
     * @param orderId ID của đơn hàng
     */
    public static PaymentQrFragment newInstance(int totalAmount, int orderId) {
        PaymentQrFragment fragment = new PaymentQrFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOTAL_AMOUNT, totalAmount);
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalAmount = getArguments().getInt(ARG_TOTAL_AMOUNT, 0);
            orderId = getArguments().getInt(ARG_ORDER_ID, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Anh xa views
        btnBack = view.findViewById(R.id.btnBack);
        imgQrCode = view.findViewById(R.id.imgQrCode);
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        btnPaymentDone = view.findViewById(R.id.btnPaymentDone);

        // Hien thi so tien
        tvPaymentAmount.setText(formatCurrency(totalAmount));

        // Tai QR code VietQR
        loadQrCode();

        // Xu ly su kien click
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        btnPaymentDone.setOnClickListener(v -> {
            // Dieu huong sang OrderSuccessFragment va xoa backstack ve Home
            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            args.putString("paymentMethod", "bank_transfer");

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_paymentQrFragment_to_orderSuccessFragment, args);
        });
    }

    /**
     * Tao URL VietQR va load vao ImageView
     */
    private void loadQrCode() {
        String bankId = "MB";
        String accountNo = "0343717527";
        String accountName = "HO SI TUNG";
        String addInfo = "ThanhToanDonHang" + orderId;

        // Mẫu URL của VietQR.io (Dùng template compact2 cho đẹp)
        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo
                + "-compact2.png?amount=" + totalAmount
                + "&addInfo=" + Uri.encode(addInfo)
                + "&accountName=" + Uri.encode(accountName);

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_default_product)
                .error(R.drawable.ic_default_product)
                .into(imgQrCode);
    }

    /**
     * Format so tien theo dinh dang VND
     * @param amount So tien
     * @return Chuoi da format (vd: 500.000 đ)
     */
    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        return format.format(amount);
    }
}
