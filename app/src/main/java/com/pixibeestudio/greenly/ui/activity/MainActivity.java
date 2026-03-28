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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pixibeestudio.greenly.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Xử lý hiển thị tràn viền (Edge-to-Edge)
        View mainView = findViewById(R.id.main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            // Lấy insets của cả system bars (status bar, nav bar) và ime (bàn phím)
            Insets insetsAll = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            
            // Áp dụng padding cho toàn bộ main view để không bị bàn phím hay thanh điều hướng che
            v.setPadding(insetsAll.left, insetsAll.top, insetsAll.right, insetsAll.bottom);
            
            // Reset padding của bottomNavigationView vì mainView đã xử lý rồi
            bottomNavigationView.setPadding(0, 0, 0, 0);
            
            return WindowInsetsCompat.CONSUMED;
        });

        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Liên kết BottomNavigationView với NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

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
                    id == R.id.addAddressFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
