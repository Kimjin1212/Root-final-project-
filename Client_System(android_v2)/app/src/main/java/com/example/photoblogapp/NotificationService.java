package com.example.photoblogapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSONArray;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.ui.MainActivity;
import com.example.photoblogapp.utils.VibrateUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationService extends Service {

    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "django_notifications";
    private static final String CHANNEL_NAME = "Django Notifications";
    private static final String DJANGO_URL = "http://your-django-server/send_notification/";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        new Thread(this::pollNotifications).start(); // 开启轮询线程
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void pollNotifications() {
        try {
            while (true) {
                // 模拟轮询，每 10 秒请求一次
                Api.Get(Api.queryNotify, new Api.ApiCallBack() {
                    @Override
                    public void success(com.alibaba.fastjson.JSONObject result) {
                        if (result.containsKey("id")) {
                            String title = "Alert!";
                            String message = "Fall Down Detect! ";

                            showNotification(title, message);
                            VibrateUtils.vibrateDevice(getApplicationContext(),200);
                        }
                    }

                    @Override
                    public void fail(com.alibaba.fastjson.JSONObject result) {

                    }

                    @Override
                    public void successArray(JSONArray result) {

                    }
                });

                Thread.sleep(10000); // 每 10 秒轮询一次
            }
        } catch (Exception e) {
            Log.e(TAG, "轮询出错: " + e.getMessage());
        }
    }

    private void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
