package com.pixibeestudio.greenly.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "GreenlySession";
    
    // Các khoá (Keys)
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_GUEST = "isGuest";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_AVATAR = "userAvatar";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLoginState(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setGuestMode(boolean isGuest) {
        editor.putBoolean(KEY_IS_GUEST, isGuest);
        editor.apply();
    }

    public boolean isGuestMode() {
        return pref.getBoolean(KEY_IS_GUEST, false);
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveUser(String name, String avatarUrl) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_AVATAR, avatarUrl);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getUserAvatar() {
        return pref.getString(KEY_USER_AVATAR, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
