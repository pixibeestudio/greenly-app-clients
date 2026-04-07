package com.pixibeestudio.greenly.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly lich su tim kiem cuc bo bang SharedPreferences va Gson.
 * Luu toi da 10 tu khoa tim kiem gan nhat.
 */
public class SearchHistoryManager {

    private static final String PREFS_NAME = "search_prefs";
    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 10;

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Lay danh sach lich su tim kiem tu SharedPreferences.
     * @return Danh sach cac tu khoa da tim kiem, hoac list rong neu chua co.
     */
    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }

    /**
     * Them tu khoa tim kiem vao lich su.
     * - Xoa trung lap neu da ton tai.
     * - Them vao dau danh sach.
     * - Giu toi da 10 phan tu.
     * @param query Tu khoa tim kiem can luu.
     */
    public void addSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        String trimmedQuery = query.trim();
        List<String> history = getSearchHistory();

        // Xoa neu da ton tai de tranh trung lap
        history.remove(trimmedQuery);

        // Them vao vi tri dau tien
        history.add(0, trimmedQuery);

        // Chi giu toi da 10 lich su gan nhat
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(history.size() - 1);
        }

        // Luu lai vao SharedPreferences
        String json = gson.toJson(history);
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply();
    }

    /**
     * Xoa toan bo lich su tim kiem.
     */
    public void clearHistory() {
        sharedPreferences.edit().clear().apply();
    }
}
