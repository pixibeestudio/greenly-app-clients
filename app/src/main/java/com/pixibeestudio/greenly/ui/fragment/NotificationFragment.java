package com.pixibeestudio.greenly.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.NotificationItem;
import com.pixibeestudio.greenly.ui.adapter.NotificationAdapter;
import com.pixibeestudio.greenly.ui.viewmodel.NotificationViewModel;

/**
 * Fragment hiển thị danh sách thông báo đơn hàng.
 * Click vào thông báo → chuyển sang MyOrdersFragment đúng tab trạng thái.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvMarkAllRead;

    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvMarkAllRead = view.findViewById(R.id.tvMarkAllRead);

        // Khởi tạo Adapter
        adapter = new NotificationAdapter(getContext(), this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Observe danh sách thông báo
        viewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), resource -> {
            swipeRefresh.setRefreshing(false);
            switch (resource.status) {
                case LOADING:
                    swipeRefresh.setRefreshing(true);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.setItems(resource.data);
                        rvNotifications.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    } else {
                        rvNotifications.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    String msg = resource.message != null ? resource.message : "Đã xảy ra lỗi";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // Tải dữ liệu lần đầu
        viewModel.fetchNotifications();

        // Kéo xuống để refresh
        swipeRefresh.setOnRefreshListener(() -> viewModel.fetchNotifications());

        // Nút "Đọc tất cả"
        tvMarkAllRead.setOnClickListener(v -> {
            viewModel.markAllAsRead().observe(getViewLifecycleOwner(), result -> {
                if (result.status == com.pixibeestudio.greenly.data.network.Resource.Status.SUCCESS) {
                    // Reload danh sách để cập nhật UI đã đọc
                    viewModel.fetchNotifications();
                    // Cập nhật badge count
                    refreshBadge();
                    Toast.makeText(getContext(), "Đã đánh dấu tất cả đã đọc", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Xử lý click vào 1 thông báo:
     * 1. Đánh dấu đã đọc
     * 2. Navigate sang MyOrdersFragment đúng tab trạng thái
     */
    @Override
    public void onNotificationClick(NotificationItem item) {
        // Đánh dấu đã đọc
        if (!item.isRead()) {
            viewModel.markAsRead(item.getId());
            item.setRead(true);
            adapter.notifyDataSetChanged();
            // Cập nhật badge
            refreshBadge();
        }

        // Tính tabIndex dựa theo type (status)
        int tabIndex = getTabIndexForStatus(item.getType());

        // Navigate sang MyOrdersFragment với argument tabIndex
        Bundle args = new Bundle();
        args.putInt("tabIndex", tabIndex);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_notificationFragment_to_myOrdersFragment, args);
    }

    /**
     * Map trạng thái đơn hàng → tab index trong MyOrdersFragment.
     * Đồng bộ với MyOrdersPagerAdapter.statusList:
     * 0=ALL, 1=PENDING, 2=PROCESSING, 3=READY_FOR_PICKUP, 4=SHIPPING, 5=DELIVERED, 6=CANCELLED
     */
    private int getTabIndexForStatus(String type) {
        if (type == null) return 0;
        switch (type) {
            case "pending":          return 1;
            case "processing":       return 2;
            case "ready_for_pickup": return 3;
            case "shipping":         return 4;
            case "delivered":        return 5;
            case "cancelled":        return 6;
            default:                 return 0;
        }
    }

    /**
     * Cập nhật badge count trên BottomNavigationView thông qua MainActivity.
     */
    private void refreshBadge() {
        if (getActivity() instanceof com.pixibeestudio.greenly.ui.activity.MainActivity) {
            ((com.pixibeestudio.greenly.ui.activity.MainActivity) getActivity()).loadNotificationBadge();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh badge count mỗi khi quay lại màn thông báo
        refreshBadge();
    }
}
