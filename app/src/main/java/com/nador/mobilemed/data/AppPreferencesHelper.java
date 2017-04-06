package com.nador.mobilemed.data;

import android.content.SharedPreferences;

/**
 * Created by nador on 12/07/16.
 */
public class AppPreferencesHelper {

    private static final String KEY_REMEMBER_USER = "RememberUser";

    private AppPreferencesHelper() {}

    public synchronized static void putRememberUser(final SharedPreferences preferences, final boolean rememberUser) {
        preferences.edit().putBoolean(KEY_REMEMBER_USER, rememberUser).commit();
    }

    public synchronized static boolean getRememberUser(final SharedPreferences preferences) {
        return preferences.getBoolean(KEY_REMEMBER_USER, false);
    }
}
