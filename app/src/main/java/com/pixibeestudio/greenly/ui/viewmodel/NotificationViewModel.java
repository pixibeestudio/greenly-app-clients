package com.pixibeestudio.greenly.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pixibeestudio.greenly.data.model.NotificationItem;
import com.pixibeestudio.greenly.data.network.Resource;
import com.pixibeestudio.greenly.data.repository.NotificationRepository;

import java.util.List;

/**
 * ViewModel cho NotificationFragment, quản lý LiveData thông báo.
 */
public class NotificationViewModel extends AndroidViewModel {

    private final NotificationRepository repository;
    private MutableLiveData<Resource<List<NotificationItem>>> notificationsLiveData;
    private MutableLiveData<Resource<Integer>> unreadCountLiveData;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        repository = new NotificationRepository(application.getApplicationContext());
    }

    public MutableLiveData<Resource<List<NotificationItem>>> getNotificationsLiveData() {
        if (notificationsLiveData == null) {
            notificationsLiveData = new MutableLiveData<>();
        }
        return notificationsLiveData;
    }

    public MutableLiveData<Resource<Integer>> getUnreadCountLiveData() {
        if (unreadCountLiveData == null) {
            unreadCountLiveData = new MutableLiveData<>();
        }
        return unreadCountLiveData;
    }

    /**
     * Tải danh sách thông báo từ API.
     */
    public void fetchNotifications() {
        repository.getNotifications().observeForever(resource -> {
            if (notificationsLiveData != null) {
                notificationsLiveData.setValue(resource);
            }
        });
    }

    /**
     * Tải số thông báo chưa đọc (badge count).
     */
    public void fetchUnreadCount() {
        repository.getUnreadCount().observeForever(resource -> {
            if (unreadCountLiveData != null) {
                unreadCountLiveData.setValue(resource);
            }
        });
    }

    /**
     * Đánh dấu 1 thông báo đã đọc.
     */
    public void markAsRead(int notificationId) {
        repository.markAsRead(notificationId);
    }

    /**
     * Đánh dấu tất cả đã đọc.
     */
    public MutableLiveData<Resource<Boolean>> markAllAsRead() {
        return repository.markAllAsRead();
    }
}
