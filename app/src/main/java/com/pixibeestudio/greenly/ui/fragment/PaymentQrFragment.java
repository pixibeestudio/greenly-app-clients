package com.pixibeestudio.greenly.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentQrFragment extends Fragment {

    private static final String TAG = "PaymentQrFragment";
    private static final long POLLING_INTERVAL = 3000; // 3 giây

    private ImageButton btnBack;
    private ImageView imgQrCode;
    private TextView tvPaymentAmount;
    private TextView tvOrderCode;
    private LinearLayout layoutWaiting;
    private LinearLayout layoutSuccess;

    private int totalAmount;
    private int orderId;
    private String orderCode;
    private String paymentUrl;

    // Handler cho polling kiểm tra trạng thái thanh toán
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            checkPaymentStatus();
        }
    };
    private boolean isPaymentConfirmed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalAmount = getArguments().getInt("totalAmount", 0);
            orderId = getArguments().getInt("orderId", 0);
            orderCode = getArguments().getString("orderCode", "");
            paymentUrl = getArguments().getString("paymentUrl", "");
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

        // Ánh xạ views
        btnBack = view.findViewById(R.id.btnBack);
        imgQrCode = view.findViewById(R.id.imgQrCode);
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        tvOrderCode = view.findViewById(R.id.tvOrderCode);
        layoutWaiting = view.findViewById(R.id.layoutWaiting);
        layoutSuccess = view.findViewById(R.id.layoutSuccess);

        // Hiển thị thông tin đơn hàng
        tvPaymentAmount.setText(formatCurrency(totalAmount));
        tvOrderCode.setText(orderCode);

        // Tải QR code từ paymentUrl
        loadQrCode();

        // Chặn nút Back hệ thống - hiện dialog
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        // Nút Back trên Header
        btnBack.setOnClickListener(v -> showExitDialog());

        // Bắt đầu polling kiểm tra trạng thái thanh toán
        startPolling();
    }

    /**
     * Tạo QR code chứa paymentUrl (link web xác nhận thanh toán)
     * Dùng API qrserver.com để tạo ảnh QR từ URL
     */
    private void loadQrCode() {
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Log.e(TAG, "paymentUrl trống, không thể tạo QR");
            return;
        }

        // Dùng API qrserver.com để tạo ảnh QR code từ paymentUrl
        String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data="
                + Uri.encode(paymentUrl);

        Log.d(TAG, "QR URL: " + qrImageUrl);
        Log.d(TAG, "Payment URL: " + paymentUrl);

        Glide.with(this)
                .load(qrImageUrl)
                .placeholder(R.drawable.ic_default_product)
                .error(R.drawable.ic_default_product)
                .into(imgQrCode);
    }

    /**
     * Bắt đầu polling kiểm tra trạng thái thanh toán mỗi 3 giây
     */
    private void startPolling() {
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    /**
     * Gọi API kiểm tra trạng thái thanh toán
     */
    private void checkPaymentStatus() {
        if (!isAdded() || isPaymentConfirmed) return;

        RetrofitClient.getApiService(requireContext()).checkPaymentStatus(orderId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!isAdded() || isPaymentConfirmed) return;

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    String paymentStatus = body.has("payment_status") ? body.get("payment_status").getAsString() : "";

                    Log.d(TAG, "Payment status: " + paymentStatus);

                    if ("completed".equals(paymentStatus)) {
                        // Thanh toán thành công!
                        isPaymentConfirmed = true;
                        onPaymentSuccess();
                        return;
                    }
                }

                // Chưa thanh toán → tiếp tục polling
                pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (!isAdded() || isPaymentConfirmed) return;
                Log.e(TAG, "Lỗi kiểm tra trạng thái: " + t.getMessage());
                // Lỗi mạng → vẫn tiếp tục polling
                pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL);
            }
        });
    }

    /**
     * Xử lý khi thanh toán thành công: hiện trạng thái thành công,
     * sau 2 giây tự động chuyển sang màn OrderSuccess
     */
    private void onPaymentSuccess() {
        // Cập nhật UI: ẩn trạng thái chờ, hiện trạng thái thành công
        layoutWaiting.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.VISIBLE);

        Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

        // Sau 2 giây tự động chuyển sang màn thành công
        pollingHandler.postDelayed(() -> {
            if (!isAdded()) return;

            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            args.putString("paymentMethod", "bank_transfer");

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_paymentQrFragment_to_orderSuccessFragment, args);
        }, 2000);
    }

    /**
     * Hiển thị Dialog khi người dùng muốn rời màn hình thanh toán
     */
    private void showExitDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hủy thanh toán?")
                .setMessage("Bạn chưa hoàn thành giao dịch. Đơn hàng sẽ được lưu vào danh sách chờ thanh toán. Bạn có chắc chắn muốn rời đi và quay về Trang chủ không?")
                .setPositiveButton("Về Trang chủ", (dialog, which) -> {
                    pollingHandler.removeCallbacks(pollingRunnable);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dọn dẹp handler để tránh memory leak
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    /**
     * Format số tiền theo định dạng VND
     */
    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        return format.format(amount);
    }
}
