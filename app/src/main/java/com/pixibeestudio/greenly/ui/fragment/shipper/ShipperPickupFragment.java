package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.ui.viewmodel.ShipperOrdersViewModel;

public class ShipperPickupFragment extends Fragment implements ShipperPickupAdapter.OnPickupClickListener {

    private ShipperOrdersViewModel viewModel;
    private RecyclerView rvPickupOrders;
    private ShipperPickupAdapter adapter;
    private SwipeRefreshLayout swipeRefreshPickup;
    private LinearLayout layoutEmptyPickup;
    private boolean isManualRefresh = false;

    public ShipperPickupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_pickup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvPickupOrders = view.findViewById(R.id.rvPickupOrders);
        swipeRefreshPickup = view.findViewById(R.id.swipeRefreshPickup);
        layoutEmptyPickup = view.findViewById(R.id.layoutEmptyPickup);

        // Khởi tạo ViewModel (Dùng requireActivity để share chung ViewModel giữa 2 tab nếu cần)
        viewModel = new ViewModelProvider(requireActivity()).get(ShipperOrdersViewModel.class);

        // Thiết lập RecyclerView
        rvPickupOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShipperPickupAdapter(getContext(), this);
        rvPickupOrders.setAdapter(adapter);

        // Thiết lập SwipeRefreshLayout
        swipeRefreshPickup.setOnRefreshListener(() -> {
            isManualRefresh = true;
            viewModel.fetchPickupOrders();
        });

        // Observe dữ liệu danh sách chờ lấy hàng
        viewModel.getPickupOrdersLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    swipeRefreshPickup.setRefreshing(false);
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.setOrders(resource.data);
                        rvPickupOrders.setVisibility(View.VISIBLE);
                        layoutEmptyPickup.setVisibility(View.GONE);
                    } else {
                        adapter.setOrders(new java.util.ArrayList<>());
                        rvPickupOrders.setVisibility(View.GONE);
                        layoutEmptyPickup.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    swipeRefreshPickup.setRefreshing(false);
                    if (isManualRefresh) {
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    }
                    isManualRefresh = false;
                    // Vẫn hiển thị danh sách cũ nếu có
                    if (adapter.getItemCount() == 0) {
                        rvPickupOrders.setVisibility(View.GONE);
                        layoutEmptyPickup.setVisibility(View.VISIBLE);
                    }
                    break;
                case LOADING:
                    if (!swipeRefreshPickup.isRefreshing()) {
                        swipeRefreshPickup.setRefreshing(true);
                    }
                    break;
            }
        });

        // Observe trạng thái Action (Lấy hàng, Giao, Hủy...)
        viewModel.getActionStatusLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    Toast.makeText(getContext(), resource.data, Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Fetch lần đầu
        viewModel.fetchPickupOrders();
    }

    @Override
    public void onPickupClick(Order order) {
        viewModel.pickupOrder(order.getId());
        Toast.makeText(getContext(), "Đã lấy hàng, bắt đầu giao!", Toast.LENGTH_SHORT).show();
    }
}
