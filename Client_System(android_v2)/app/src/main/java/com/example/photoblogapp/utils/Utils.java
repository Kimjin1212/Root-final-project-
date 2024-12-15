package com.example.photoblogapp.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class Utils {
    private static String TAG  = " xingxinglight ";

    public static void toastinfo(Activity activity, String info) {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity,info,Toast.LENGTH_SHORT).show();
            Log.d(TAG, "debug: " + info);
        });
    }
    public static void loginfo(String info) {
        Log.d(TAG, "debug: " + info);
    }

}
