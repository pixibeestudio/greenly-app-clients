package com.pixibeestudio.greenly.ui.fragment.shipper;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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

public class ShipperShippingFragment extends Fragment implements ShipperShippingAdapter.OnShippingActionListener {

    private ShipperOrdersViewModel viewModel;
    private RecyclerView rvShippingOrders;
    private ShipperShippingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshShipping;
    private LinearLayout layoutEmptyShipping;
    private boolean isManualRefresh = false;

    public ShipperShippingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shipper_shipping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvShippingOrders = view.findViewById(R.id.rvShippingOrders);
        swipeRefreshShipping = view.findViewById(R.id.swipeRefreshShipping);
        layoutEmptyShipping = view.findViewById(R.id.layoutEmptyShipping);

        // Khởi tạo ViewModel (Share với tab Pickup)
        viewModel = new ViewModelProvider(requireActivity()).get(ShipperOrdersViewModel.class);

        // Thiết lập RecyclerView
        rvShippingOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShipperShippingAdapter(getContext(), this);
        rvShippingOrders.setAdapter(adapter);

        // Thiết lập SwipeRefreshLayout
        swipeRefreshShipping.setOnRefreshListener(() -> {
            isManualRefresh = true;
            viewModel.fetchShippingOrders();
        });

        // Observe dữ liệu
        viewModel.getShippingOrdersLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case SUCCESS:
                    swipeRefreshShipping.setRefreshing(false);
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.setOrders(resource.data);
                        rvShippingOrders.setVisibility(View.VISIBLE);
                        layoutEmptyShipping.setVisibility(View.GONE);
                    } else {
                        adapter.setOrders(new java.util.ArrayList<>());
                        rvShippingOrders.setVisibility(View.GONE);
                        layoutEmptyShipping.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    swipeRefreshShipping.setRefreshing(false);
                    if (isManualRefresh) {
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    }
                    isManualRefresh = false;
                    if (adapter.getItemCount() == 0) {
                        rvShippingOrders.setVisibility(View.GONE);
                        layoutEmptyShipping.setVisibility(View.VISIBLE);
                    }
                    break;
                case LOADING:
                    if (!swipeRefreshShipping.isRefreshing()) {
                        swipeRefreshShipping.setRefreshing(true);
                    }
                    break;
            }
        });

        // Fetch lần đầu
        viewModel.fetchShippingOrders();
    }

    @Override
    public void onCallClick(Order order) {
        if (order.getShippingPhone() != null && !order.getShippingPhone().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + order.getShippingPhone()));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Không có số điện thoại khách hàng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(Order order) {
        if (order.getAddress() != null && !order.getAddress().isEmpty()) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(order.getAddress()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Nếu máy không có Google Maps thì mở web
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(order.getAddress()))));
            }
        } else {
            Toast.makeText(getContext(), "Không có địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailClick(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Giao hàng thất bại")
                .setMessage("Bạn chắc chắn muốn báo cáo giao hàng thất bại (hủy đơn) cho đơn hàng này?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    viewModel.failOrder(order.getId());
                    Toast.makeText(getContext(), "Đã hủy đơn!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    @Override
    public void onCompleteClick(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận thu tiền")
                .setMessage("Bạn đã thu đủ số tiền COD từ khách hàng và giao hàng thành công?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    viewModel.completeOrder(order.getId());
                    Toast.makeText(getContext(), "Giao thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Chưa", null)
                .show();
    }
}
