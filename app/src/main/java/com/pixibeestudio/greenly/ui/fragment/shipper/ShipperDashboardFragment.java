package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.ui.adapter.ShipperOrderAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.ShipperDashboardViewModel;

public class ShipperDashboardFragment extends Fragment implements ShipperOrderAdapter.OnOrderActionListener {

    private ShipperDashboardViewModel viewModel;
    private RecyclerView rvNewOrdersShipper;
    private ShipperOrderAdapter adapter;
    private TextView tvOrderCount;
    private MaterialSwitch switchStatus;
    private TextView tvStatusText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ShipperDashboardViewModel.class);

        // Ánh xạ View
        rvNewOrdersShipper = view.findViewById(R.id.rvNewOrdersShipper);
        switchStatus = view.findViewById(R.id.switch_status);
        tvStatusText = view.findViewById(R.id.tv_status_text);

        // Thiết lập RecyclerView
        rvNewOrdersShipper.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShipperOrderAdapter(getContext(), this);
        rvNewOrdersShipper.setAdapter(adapter);

        // Observe dữ liệu
        observeViewModel();

        // Bắt đầu polling dữ liệu real-time
        viewModel.startPolling();

        // Xử lý toggle trạng thái online/offline gọi API
        switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Chỉ gọi API nếu user thực sự ấn vào switch
                String status = isChecked ? "available" : "offline";
                viewModel.updateWorkStatus(status);
            }
        });
    }

    private void observeViewModel() {
        // Observe thống kê và trạng thái
        viewModel.getStatsLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null && resource.data.getData() != null) {
                        com.pixibeestudio.greenly.data.model.ShipperStats stats = resource.data.getData();
                        
                        // Cập nhật UI Header (tên)
                        TextView tvName = getView().findViewById(R.id.tvShipperName);
                        if (tvName != null) {
                            tvName.setText(stats.getFullname());
                        }

                        // Cập nhật thống kê
                        TextView tvTodayOrders = getView().findViewById(R.id.tvTodayOrders);
                        TextView tvTodayIncome = getView().findViewById(R.id.tvTodayIncome);
                        
                        if (tvTodayOrders != null) {
                            tvTodayOrders.setText(String.valueOf(stats.getTodayOrders()));
                        }
                        if (tvTodayIncome != null) {
                            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                            tvTodayIncome.setText(format.format(stats.getTodayIncome()));
                        }

                        // Cập nhật trạng thái Switch
                        boolean isAvailable = "available".equals(stats.getWorkStatus()) || "on_delivery".equals(stats.getWorkStatus());
                        switchStatus.setChecked(isAvailable);
                        
                        if (isAvailable) {
                            tvStatusText.setText("Đang rảnh");
                            tvStatusText.setTextColor(0xFFA5D6A7); // Xanh nhạt
                        } else {
                            tvStatusText.setText("Đang bận");
                            tvStatusText.setTextColor(0xFFEF9A9A); // Đỏ nhạt
                        }
                    }
                    break;
                case ERROR:
                    // Có thể bỏ qua error log để tránh spam Toast khi polling
                    break;
            }
        });

        // Observe kết quả cập nhật work status
        viewModel.getUpdateStatusLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), resource.data, Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    // Revert switch nếu lỗi
                    switchStatus.setChecked(!switchStatus.isChecked());
                    break;
            }
        });

        // Observe danh sách đơn hàng mới
        viewModel.getNewOrdersLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // Hiển thị loading (tùy chọn)
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        adapter.setOrders(resource.data);
                        // Cập nhật số lượng đơn hàng lên UI nếu cần
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Observe kết quả nhận đơn
        viewModel.getAcceptOrderLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // Hiển thị loading (tùy chọn)
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Đã nhận đơn! Vui lòng tới cửa hàng lấy hàng", Toast.LENGTH_LONG).show();
                    // Load lại danh sách đơn hàng
                    viewModel.fetchNewOrders();
                    
                    // Chuyển hướng sang Tab "Đơn hàng" (id_tab_orders)
                    if (getActivity() != null) {
                        BottomNavigationView bottomNav = getActivity().findViewById(R.id.nav_view_shipper);
                        if (bottomNav != null) {
                            bottomNav.setSelectedItemId(R.id.shipperOrdersFragment); // Thay thế bằng ID thực tế của menu item
                        }
                    }
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Observe kết quả từ chối đơn
        viewModel.getRejectOrderLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // Hiển thị loading (tùy chọn)
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), resource.data, Toast.LENGTH_SHORT).show();
                    // Đã gọi fetchNewOrders() trong ViewModel khi success
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onAcceptClick(Order order) {
        viewModel.acceptOrder(order.getId());
    }

    @Override
    public void onRejectClick(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn từ chối giao đơn hàng này?")
                .setPositiveButton("CÓ", (dialog, which) -> {
                    viewModel.rejectOrder(order.getId());
                })
                .setNegativeButton("KHÔNG", null)
                .show();
    }
}
