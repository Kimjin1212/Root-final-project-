package com.example.photoblogapp;

import android.app.Application;
import android.content.Context;

import com.example.photoblogapp.data.User;


public class MyApp extends Application {
    public static User loginuser;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
