package com.pixibeestudio.greenly.ui.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.ui.viewmodel.CheckoutViewModel;

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
    private CheckoutViewModel viewModel;

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

        // Khoi tao ViewModel
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        // Anh xa views
        btnBack = view.findViewById(R.id.btnBack);
        imgQrCode = view.findViewById(R.id.imgQrCode);
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        btnPaymentDone = view.findViewById(R.id.btnPaymentDone);

        // Hien thi so tien
        tvPaymentAmount.setText(formatCurrency(totalAmount));

        // Tai QR code VietQR
        loadQrCode();

        // Chan nut Back he thong (System Back) - hien dialog thay vi quay lai Checkout
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        // Xu ly su kien click nut Back tren Header - hien dialog thay vi popBackStack
        btnBack.setOnClickListener(v -> showExitDialog());

        btnPaymentDone.setOnClickListener(v -> {
            // Goi API xac nhan thanh toan truoc khi dieu huong
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Đang xác nhận thanh toán...");
            progressDialog.setCancelable(false);

            viewModel.confirmPayment(orderId).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        progressDialog.show();
                        break;
                    case SUCCESS:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Xác nhận thanh toán thành công!", Toast.LENGTH_SHORT).show();

                        // Dieu huong sang OrderSuccessFragment
                        Bundle args = new Bundle();
                        args.putInt("orderId", orderId);
                        args.putString("paymentMethod", "bank_transfer");

                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_paymentQrFragment_to_orderSuccessFragment, args);
                        break;
                    case ERROR:
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }

    /**
     * Tao URL VietQR va load vao ImageView
     */
    private void loadQrCode() {
        String bankId = "TPBank";
        String accountNo = "00001983982";
        String accountName = "Greenly Store";
        String addInfo = "Greenly Store" + orderId;

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
     * Hien thi Dialog xac nhan khi nguoi dung muon roi khoi man hinh thanh toan QR
     * Vi gio hang da bi xoa sau khi dat hang, khong the quay lai Checkout
     */
    private void showExitDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy thanh toán?")
                .setMessage("Bạn chưa hoàn thành giao dịch. Đơn hàng sẽ được lưu vào danh sách chờ thanh toán. Bạn có chắc chắn muốn rời đi và quay về Trang chủ không?")
                .setPositiveButton("Về Trang chủ", (dialog, which) -> {
                    NavController navController = Navigation.findNavController(requireView());
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build();
                    navController.navigate(R.id.homeFragment, null, navOptions);
                })
                .setNegativeButton("Tiếp tục thanh toán", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
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
