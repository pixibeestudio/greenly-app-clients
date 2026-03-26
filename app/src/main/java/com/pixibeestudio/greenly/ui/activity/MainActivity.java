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
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Chỉ padding top cho main view (để tránh status bar)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            
            // Padding bottom cho BottomNavigationView (để tránh system navigation bar)
            bottomNavigationView.setPadding(0, 0, 0, systemBars.bottom);
            
            return insets;
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
                // Ẩn bottom nav khi đang ở màn hình Splash hoặc Chi tiết sản phẩm hoặc Welcome, Login, Register
                if (destination.getId() == R.id.splashFragment || destination.getId() == R.id.productDetailFragment || destination.getId() == R.id.welcomeFragment || destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
