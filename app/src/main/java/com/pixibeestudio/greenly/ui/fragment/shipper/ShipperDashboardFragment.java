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

        // Xử lý toggle trạng thái online/offline
        switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvStatusText.setText("Đang rảnh");
                tvStatusText.setTextColor(0xFFA5D6A7); // Xanh nhạt
            } else {
                tvStatusText.setText("Đang bận");
                tvStatusText.setTextColor(0xFFEF9A9A); // Đỏ nhạt
            }
        });

        // Thiết lập RecyclerView
        rvNewOrdersShipper.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShipperOrderAdapter(getContext(), this);
        rvNewOrdersShipper.setAdapter(adapter);

        // Observe dữ liệu
        observeViewModel();

        // Gọi API lấy đơn hàng mới
        viewModel.fetchNewOrders();
    }

    private void observeViewModel() {
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
