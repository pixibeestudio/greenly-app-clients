package com.pixibeestudio.greenly.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;
import com.pixibeestudio.greenly.data.network.ApiService;
import com.pixibeestudio.greenly.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navViewCustomer;
    private BottomNavigationView navViewShipper;
    private boolean isShipper;
    private NavController.OnDestinationChangedListener destChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý hiển thị tràn viền (Edge-to-Edge)
        View mainView = findViewById(R.id.main);
        navViewCustomer = findViewById(R.id.nav_view);
        navViewShipper = findViewById(R.id.nav_view_shipper);

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            // Lấy insets của cả system bars (status bar, nav bar) và ime (bàn phím)
            Insets insetsAll = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

            // Áp dụng padding cho toàn bộ main view để không bị bàn phím hay thanh điều hướng che
            v.setPadding(insetsAll.left, insetsAll.top, insetsAll.right, insetsAll.bottom);

            // Reset padding của bottomNavigationView vì mainView đã xử lý rồi
            navViewCustomer.setPadding(0, 0, 0, 0);
            navViewShipper.setPadding(0, 0, 0, 0);

            return WindowInsetsCompat.CONSUMED;
        });

        // Thiết lập Navigation graph lần đầu (hiện splash screen)
        setupNavigation(false);

        // Xu ly deeplink khi app duoc mo lan dau qua scheme greenly://
        handleMomoDeeplink(getIntent());
    }

    /**
     * Khi app dang chay (singleTask) va nhan deeplink, Android goi onNewIntent thay vi onCreate.
     * Day la noi xu ly callback tu MoMo UAT sau khi user thanh toan xong.
     */
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        // Cap nhat intent moi cho activity
        setIntent(intent);
        handleMomoDeeplink(intent);
    }

    /**
     * Thiết lập Navigation graph, BottomNav và destination listener.
     * @param skipSplash true khi reset (login/logout) - bỏ qua splash, đi thẳng home/welcome
     */
    private void setupNavigation(boolean skipSplash) {
        SessionManager sessionManager = new SessionManager(this);
        isShipper = sessionManager.isLoggedIn() && sessionManager.isShipper();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph;

        if (isShipper) {
            // LUỒNG SHIPPER
            graph = inflater.inflate(R.navigation.nav_shipper);
            navViewCustomer.setVisibility(View.GONE);
            navViewShipper.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(navViewShipper, navController);
        } else {
            // LUỒNG KHÁCH HÀNG (Mặc định)
            graph = inflater.inflate(R.navigation.nav_main);
            navViewShipper.setVisibility(View.GONE);
            navViewCustomer.setVisibility(View.VISIBLE);
            NavigationUI.setupWithNavController(navViewCustomer, navController);
        }

        // Khi reset (login/logout), bỏ qua splash screen
        if (skipSplash && !isShipper) {
            if (sessionManager.isLoggedIn() || sessionManager.isGuestMode()) {
                graph.setStartDestination(R.id.homeFragment);
            } else {
                graph.setStartDestination(R.id.welcomeFragment);
            }
        }

        navController.setGraph(graph);

        // Xóa listener cũ nếu có (tránh duplicate khi gọi resetNavigation)
        if (destChangedListener != null) {
            navController.removeOnDestinationChangedListener(destChangedListener);
        }

        // Logic ẩn/hiện BottomNavigationView theo màn hình hiện tại
        destChangedListener = (controller, destination, arguments) -> {
            int id = destination.getId();
            // Ẩn bottom nav khi đang ở các màn hình không cần thiết
            if (id == R.id.splashFragment || id == R.id.productDetailFragment ||
                    id == R.id.welcomeFragment || id == R.id.loginFragment ||
                    id == R.id.registerFragment || id == R.id.checkoutFragment ||
                    id == R.id.addAddressFragment || id == R.id.addressBookFragment ||
                    id == R.id.editAddressFragment || id == R.id.myReviewsFragment ||
                    id == R.id.writeReviewFragment || id == R.id.reviewSuccessFragment ||
                    id == R.id.searchFragment ||
                    id == R.id.searchResultFragment || id == R.id.favoriteFragment ||
                    id == R.id.categoryProductFragment ||
                    id == R.id.filteredProductsFragment ||
                    id == R.id.myOrdersFragment ||
                    id == R.id.momoPaymentFragment ||
                    id == R.id.forgotPasswordFragment) {
                navViewCustomer.setVisibility(View.GONE);
                navViewShipper.setVisibility(View.GONE);
            } else {
                // Hiển thị dựa theo quyền
                if (isShipper) {
                    navViewCustomer.setVisibility(View.GONE);
                    navViewShipper.setVisibility(View.VISIBLE);
                } else {
                    navViewShipper.setVisibility(View.GONE);
                    navViewCustomer.setVisibility(View.VISIBLE);
                }
            }
        };
        navController.addOnDestinationChangedListener(destChangedListener);

        // Tải badge count thông báo chưa đọc (chỉ cho khách hàng đã đăng nhập)
        if (!isShipper && sessionManager.isLoggedIn()) {
            loadNotificationBadge();
        } else {
            navViewCustomer.removeBadge(R.id.notificationFragment);
        }
    }

    /**
     * Reset Navigation graph mà không restart Activity.
     * Gọi từ LoginFragment/ProfileFragment khi đăng nhập/đăng xuất.
     */
    public void resetNavigation() {
        setupNavigation(true);
    }

    /**
     * Gọi API lấy số thông báo chưa đọc và hiển thị badge trên icon chuông.
     */
    public void loadNotificationBadge() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getUnreadNotificationCount().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().has("unread_count")) {
                    int count = response.body().get("unread_count").getAsInt();
                    updateNotificationBadge(count);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Không cần xử lý lỗi badge
            }
        });
    }

    /**
     * Cập nhật badge count trên icon Thông báo trong BottomNavigationView.
     */
    public void updateNotificationBadge(int count) {
        if (navViewCustomer == null) return;
        if (count > 0) {
            BadgeDrawable badge = navViewCustomer.getOrCreateBadge(R.id.notificationFragment);
            badge.setNumber(count);
            badge.setVisible(true);
            badge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            navViewCustomer.removeBadge(R.id.notificationFragment);
        }
    }

    private void handleMomoDeeplink(Intent intent) {
        if (intent == null || intent.getData() == null) return;

        Uri uri = intent.getData();
        if ("greenly".equalsIgnoreCase(uri.getScheme())
                && "momo-callback".equalsIgnoreCase(uri.getHost())) {
            Log.d("MainActivity", "Nhan deeplink MoMo callback: " + uri);
            // Polling tu MomoPaymentFragment se kiem tra status va cap nhat UI.
            // Khong can navigate o day de tranh xung dot voi back stack hien tai.
        }
    }
}
