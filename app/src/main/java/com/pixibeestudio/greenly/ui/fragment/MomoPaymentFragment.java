package com.pixibeestudio.greenly.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.MomoCreatePaymentResponse;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment xu ly thanh toan MoMo voi 2 luong:
 *  - 'app': mo deeplink vao app MoMo UAT
 *  - 'qr' : hien thi ma QR de may khac quet
 *
 * Sau khi user chon 1 trong 2, fragment goi POST /api/momo/create-payment
 * de lay payUrl/deeplink/qrCodeUrl, roi:
 *  - 'app': mo Intent ACTION_VIEW voi deeplink
 *  - 'qr' : load anh QR vao ImageView
 *
 * Polling moi 3 giay GET /api/momo/status/{orderId} de phat hien thanh toan thanh cong.
 */
public class MomoPaymentFragment extends Fragment {

    private static final String TAG = "MomoPaymentFragment";
    private static final long POLLING_INTERVAL_MS = 3000L; // 3 giay

    // Header
    private ImageButton btnBack;

    // Thong tin don hang
    private TextView tvOrderCode;
    private TextView tvPaymentAmount;

    // 2 nut chon phuong thuc
    private MaterialButton btnOpenMomoApp;
    private MaterialButton btnShowQr;

    // Khu vuc QR
    private MaterialCardView cardQrContainer;
    private ImageView imgQrCode;

    // Trang thai
    private LinearLayout layoutWaiting;
    private LinearLayout layoutSuccess;

    // Arguments
    private int orderId;
    private int totalAmount;
    private String orderCode;

    // State
    private boolean isPaymentConfirmed = false;
    private boolean isPollingStarted = false;
    private String currentMomoOrderId; // momo_order_id cua giao dich hien tai

    // Polling handler
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            checkPaymentStatus();
        }
    };

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId     = getArguments().getInt("orderId", 0);
            totalAmount = getArguments().getInt("totalAmount", 0);
            orderCode   = getArguments().getString("orderCode", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_momo_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupListeners();

        // Hien thi thong tin don hang
        tvOrderCode.setText(orderCode);
        tvPaymentAmount.setText(formatCurrency(totalAmount));

        // Chuan bi progress dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Đang khởi tạo giao dịch MoMo...");
        progressDialog.setCancelable(false);
    }

    private void bindViews(View view) {
        btnBack          = view.findViewById(R.id.btnBack);
        tvOrderCode      = view.findViewById(R.id.tvOrderCode);
        tvPaymentAmount  = view.findViewById(R.id.tvPaymentAmount);
        btnOpenMomoApp   = view.findViewById(R.id.btnOpenMomoApp);
        btnShowQr        = view.findViewById(R.id.btnShowQr);
        cardQrContainer  = view.findViewById(R.id.cardQrContainer);
        imgQrCode        = view.findViewById(R.id.imgQrCode);
        layoutWaiting    = view.findViewById(R.id.layoutWaiting);
        layoutSuccess    = view.findViewById(R.id.layoutSuccess);
    }

    private void setupListeners() {
        // Nut Back tren header
        btnBack.setOnClickListener(v -> showExitDialog());

        // Chan nut Back he thong - hien dialog xac nhan
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    showExitDialog();
                }
            });

        // Nut "Mo app MoMo" -> goi BE tao giao dich type=app
        btnOpenMomoApp.setOnClickListener(v -> initiatePayment("app"));

        // Nut "Quet QR" -> goi BE tao giao dich type=qr
        btnShowQr.setOnClickListener(v -> initiatePayment("qr"));
    }

    /**
     * Goi BE de tao giao dich MoMo, sau do mo deeplink hoac hien thi QR.
     */
    private void initiatePayment(String type) {
        if (!isAdded()) return;

        progressDialog.show();

        // Build request body
        JsonObject body = new JsonObject();
        body.addProperty("order_id", orderId);
        body.addProperty("type", type);

        RetrofitClient.getApiService(requireContext())
            .createMomoPayment(body)
            .enqueue(new Callback<MomoCreatePaymentResponse>() {
                @Override
                public void onResponse(@NonNull Call<MomoCreatePaymentResponse> call,
                                       @NonNull Response<MomoCreatePaymentResponse> response) {
                    if (!isAdded()) return;
                    progressDialog.dismiss();

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().isSuccess()
                            && response.body().getData() != null) {

                        MomoCreatePaymentResponse.Data data = response.body().getData();
                        currentMomoOrderId = data.getMomoOrderId();

                        Log.d(TAG, "Tao giao dich MoMo OK: " + currentMomoOrderId
                                + ", type=" + data.getPaymentType());

                        // Phan luong xu ly theo loai thanh toan
                        if ("app".equalsIgnoreCase(data.getPaymentType())) {
                            handleAppMomoFlow(data);
                        } else if ("qr".equalsIgnoreCase(data.getPaymentType())) {
                            handleQrFlow(data);
                        }

                        // Bat dau polling sau khi giao dich da duoc tao
                        if (!isPollingStarted) {
                            isPollingStarted = true;
                            layoutWaiting.setVisibility(View.VISIBLE);
                            startPolling();
                        }
                    } else {
                        // Loi: hien thi thong bao
                        String errorMsg = "Không tạo được giao dịch MoMo";
                        try {
                            if (response.body() != null && response.body().getMessage() != null) {
                                errorMsg = response.body().getMessage();
                            } else if (response.errorBody() != null) {
                                String errBody = response.errorBody().string();
                                Log.e(TAG, "Lỗi tạo MoMo: " + errBody);
                                if (errBody.contains("MOMO_IPN_URL")) {
                                    errorMsg = "Server chưa cấu hình URL ngrok cho MoMo. Vui lòng liên hệ admin.";
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Loi parse error body", e);
                        }
                        showError(errorMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MomoCreatePaymentResponse> call,
                                      @NonNull Throwable t) {
                    if (!isAdded()) return;
                    progressDialog.dismiss();
                    Log.e(TAG, "Loi mang tao MoMo: " + t.getMessage());
                    showError("Không kết nối được server: " + t.getMessage());
                }
            });
    }

    /**
     * Luong 'app': mo deeplink MoMo. Neu khong cai MoMo thi fallback sang QR.
     */
    private void handleAppMomoFlow(MomoCreatePaymentResponse.Data data) {
        // Uu tien deeplink momo://, neu khong co thi dung pay_url
        String url = data.getDeeplink();
        if (url == null || url.isEmpty()) {
            url = data.getPayUrl();
        }

        if (url == null || url.isEmpty()) {
            showError("Không nhận được link thanh toán từ MoMo");
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            // An card QR neu dang hien
            cardQrContainer.setVisibility(View.GONE);
        } catch (ActivityNotFoundException e) {
            // Khong cai MoMo UAT -> fallback sang QR
            Log.w(TAG, "Chua cai MoMo UAT, fallback QR: " + e.getMessage());
            new AlertDialog.Builder(requireContext())
                .setTitle("Chưa cài MoMo UAT")
                .setMessage("Thiết bị chưa cài MoMo UAT. Bạn muốn dùng phương thức Quét QR thay thế?")
                .setPositiveButton("Dùng QR", (d, w) -> initiatePayment("qr"))
                .setNegativeButton("Đóng", null)
                .show();
        }
    }

    /**
     * Luong 'qr': hien thi anh QR de may khac quet.
     */
    private void handleQrFlow(MomoCreatePaymentResponse.Data data) {
        String qrUrl = data.getQrCodeUrl();
        if (qrUrl == null || qrUrl.isEmpty()) {
            // Fallback: dung qrserver.com encode pay_url
            String payUrl = data.getPayUrl();
            if (payUrl == null || payUrl.isEmpty()) {
                showError("Không nhận được URL QR từ MoMo");
                return;
            }
            qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data="
                    + Uri.encode(payUrl);
        }

        Log.d(TAG, "Loading QR: " + qrUrl);

        Glide.with(this)
            .load(qrUrl)
            .placeholder(R.drawable.ic_default_product)
            .error(R.drawable.ic_default_product)
            .into(imgQrCode);

        cardQrContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Bat dau polling kiem tra trang thai thanh toan.
     */
    private void startPolling() {
        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
    }

    /**
     * Goi GET /api/momo/status/{orderId} de check.
     * Neu payment_status='completed' -> chuyen sang OrderSuccess.
     */
    private void checkPaymentStatus() {
        if (!isAdded() || isPaymentConfirmed) return;

        RetrofitClient.getApiService(requireContext())
            .getMomoStatus(orderId)
            .enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call,
                                       @NonNull Response<JsonObject> response) {
                    if (!isAdded() || isPaymentConfirmed) return;

                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject body = response.body();
                        String paymentStatus = body.has("payment_status")
                                ? body.get("payment_status").getAsString()
                                : "";
                        String momoStatus = body.has("momo_status") && !body.get("momo_status").isJsonNull()
                                ? body.get("momo_status").getAsString()
                                : "";

                        Log.d(TAG, "Polling status: payment=" + paymentStatus + ", momo=" + momoStatus);

                        if ("completed".equalsIgnoreCase(paymentStatus)
                                || "success".equalsIgnoreCase(momoStatus)) {
                            isPaymentConfirmed = true;
                            onPaymentSuccess();
                            return;
                        }

                        // Neu MoMo trang thai 'failed' -> dung polling, hien thi loi
                        if ("failed".equalsIgnoreCase(momoStatus)) {
                            pollingHandler.removeCallbacks(pollingRunnable);
                            showError("Giao dịch MoMo thất bại. Vui lòng thử lại.");
                            return;
                        }
                    }

                    // Tiep tuc polling
                    pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    if (!isAdded() || isPaymentConfirmed) return;
                    Log.e(TAG, "Loi polling: " + t.getMessage());
                    // Loi mang van tiep tuc polling (co the do mat ket noi tam)
                    pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
                }
            });
    }

    /**
     * Khi thanh toan thanh cong: an UI cho, hien UI thanh cong, sau 2 giay
     * chuyen sang OrderSuccessFragment.
     */
    private void onPaymentSuccess() {
        layoutWaiting.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.VISIBLE);

        // Vo hieu hoa cac nut tao giao dich moi
        btnOpenMomoApp.setEnabled(false);
        btnShowQr.setEnabled(false);

        Toast.makeText(getContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

        pollingHandler.postDelayed(() -> {
            if (!isAdded()) return;

            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            args.putString("paymentMethod", "momo");

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_momoPaymentFragment_to_orderSuccessFragment, args);
        }, 2000L);
    }

    /**
     * Hien thi dialog xac nhan khi user muon thoat man hinh thanh toan.
     */
    private void showExitDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Hủy thanh toán?")
            .setMessage("Bạn chưa hoàn thành giao dịch. Đơn hàng sẽ được lưu vào danh sách chờ thanh toán. "
                    + "Bạn có chắc muốn rời đi và quay về Trang chủ không?")
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

    /**
     * Hien thi error dialog don gian.
     */
    private void showError(String message) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("Đóng", null)
            .show();
    }

    /**
     * Format so tien sang dinh dang VND (vd: 75.000 đ).
     */
    private String formatCurrency(int amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        return format.format(amount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Don dep handler de tranh memory leak
        pollingHandler.removeCallbacks(pollingRunnable);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
