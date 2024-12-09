package com.example.testnavigation;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "My Shared Preference";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;
    static SharedPrefManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance=new SharedPrefManager(context);
        return ourInstance;
    }
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,
                SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean readBoolean(String rememberMe, boolean b) {
        return sharedPreferences.getBoolean(rememberMe, b);
    }

    public void writeBoolean(String rememberMe, boolean b) {
        editor.putBoolean(rememberMe, b);
        editor.commit();
    }

    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

}