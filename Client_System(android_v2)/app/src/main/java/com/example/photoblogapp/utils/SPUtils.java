package com.example.photoblogapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.photoblogapp.MyApp;


public class SPUtils {
    private static final String PREF_NAME = "app_prefs";
    private static SharedPreferences sharedPreferences;

    // 初始化 SharedPreferences
    public static void init() {
        if (sharedPreferences == null) {
            sharedPreferences = MyApp.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    // 保存字符串
    public static void putString(String key, String value) {
        init();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    // 获取字符串
    public static String getString(String key, String defaultValue) {
        init();
        return sharedPreferences.getString(key, defaultValue);
    }

    // 保存整型
    public static void putInt(String key, int value) {
        init();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    // 获取整型
    public static int getInt(String key, int defaultValue) {
        init();

        return sharedPreferences.getInt(key, defaultValue);
    }

    // 保存布尔值
    public static void putBoolean(String key, boolean value) {
        init();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // 获取布尔值
    public static boolean getBoolean(String key, boolean defaultValue) {
        init();

        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // 清除数据
    public static void clear() {
        init();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
