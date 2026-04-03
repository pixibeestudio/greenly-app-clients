package com.pixibeestudio.greenly.ui.fragment;

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

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.Order;
import com.pixibeestudio.greenly.ui.adapter.CustomerOrderAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.MyOrdersViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersPageFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;

    private RecyclerView rvOrders;
    private LinearLayout layoutEmpty;
    private CustomerOrderAdapter adapter;
    private MyOrdersViewModel sharedViewModel;

    public static MyOrdersPageFragment newInstance(String status) {
        MyOrdersPageFragment fragment = new MyOrdersPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvOrders = view.findViewById(R.id.rvOrders);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        // Khởi tạo Adapter
        adapter = new CustomerOrderAdapter(getContext(), new CustomerOrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Toast.makeText(getContext(), "Chi tiết đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Order order) {
                if ("pending".equals(order.getStatus())) {
                    Toast.makeText(getContext(), "Yêu cầu hủy đơn: " + order.getId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Xem chi tiết đơn: " + order.getId(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);

        // Khởi tạo SharedViewModel từ Fragment cha
        sharedViewModel = new ViewModelProvider(requireParentFragment()).get(MyOrdersViewModel.class);

        // Observe dữ liệu từ SharedViewModel
        sharedViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    // Có thể hiển thị ProgressBar nếu cần
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        filterAndDisplayOrders(resource.data);
                    }
                    break;
                case ERROR:
                    String errorMsg = resource.message != null ? resource.message : "Đã xảy ra lỗi";
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void filterAndDisplayOrders(List<Order> allOrders) {
        List<Order> filteredList = new ArrayList<>();
        
        if ("ALL".equalsIgnoreCase(status)) {
            filteredList.addAll(allOrders);
        } else {
            for (Order order : allOrders) {
                if (order.getStatus() != null && order.getStatus().equalsIgnoreCase(status)) {
                    filteredList.add(order);
                }
            }
        }

        adapter.setOrders(filteredList);

        if (filteredList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
    }
}
