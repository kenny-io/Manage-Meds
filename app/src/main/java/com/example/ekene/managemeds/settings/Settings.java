package com.example.ekene.managemeds.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import timber.log.Timber;

public class Settings {

    //name our shared preferences
    public static final String SHARED_PREF_NAME = "andela";

    //Check if the User is logged in
    public static final String LOGGED_IN_SHARED_PREF = "loggedin";

    //Check if its a first timer
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static SharedPreferences settings;
    private static SharedPreferences defaultPrefs;

    public static void init(@NonNull Context context) {
        settings = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    // Set Logged in

    public static boolean isLoggedIn() {
        return settings.getBoolean(LOGGED_IN_SHARED_PREF, false);
    }

    public static void setLoggedInSharedPref(boolean loggedIn) {
        settings.edit()
                .putBoolean(LOGGED_IN_SHARED_PREF, loggedIn)
                .apply();
    }

    public static int themeIndex() {
        int themeIndex = 0; // 0 -> Dark 1 -> Light
        try {
            themeIndex = Integer.parseInt(defaultPrefs.getString("THEME", "0"));
        } catch (Exception e) {
            Timber.e(e);
        }
        return themeIndex;
    }

    //Set Theme
    public static void setThemeIndex(int themeIndex) {
        defaultPrefs.edit()
                .putString("THEME", String.valueOf(themeIndex))
                .apply();
    }
}
