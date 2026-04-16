package com.pixibeestudio.greenly.ui.activity;

import android.os.Bundle;
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
                        id == R.id.editAddressFragment || id == R.id.searchFragment ||
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
    }
}
