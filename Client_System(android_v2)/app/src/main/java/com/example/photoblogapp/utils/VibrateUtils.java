package com.example.photoblogapp.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrateUtils {

    public static void vibrateDevice(Context context, long duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // 旧版本兼容
            vibrator.vibrate(duration);
        }
    }

    public void vibratePattern(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 500, 200, 500}; // 第一个元素是等待时间，接下来的交替表示震动和暂停的时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1)); // -1 表示不重复
        } else {
            vibrator.vibrate(pattern, -1); // 旧版本兼容
        }
    }


}
