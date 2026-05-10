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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.local.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);

        // Xử lý hiển thị tràn viền (Edge-to-Edge)
        View mainView = findViewById(R.id.main);
        BottomNavigationView navViewCustomer = findViewById(R.id.nav_view);
        BottomNavigationView navViewShipper = findViewById(R.id.nav_view_shipper);

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

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph;

        boolean isShipper = sessionManager.isLoggedIn() && sessionManager.isShipper();

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

        navController.setGraph(graph);

        // Logic ẩn/hiện BottomNavigationView theo màn hình hiện tại
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             Bundle arguments) {
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
                        id == R.id.filteredProductsFragment) {
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
            }
        });

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
     * Kiem tra Intent co phai la deeplink callback tu MoMo khong.
     * Khong can lam gi them - polling o MomoPaymentFragment se tu detect
     * trang thai 'completed' tu backend va dieu huong sang OrderSuccessFragment.
     * Ham nay chi de log + co the trigger UI feedback nhanh hon trong tuong lai.
     */
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
