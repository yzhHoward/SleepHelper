package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.howard.sleephelper.R;

public class DaemonService extends Service {
    public static final int NOTICE_ID = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.sleep_1)
                .setContentTitle("睡眠助手")
                .setContentText("正在记录您的睡眠...");
        startForeground(NOTICE_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mManager.cancel(NOTICE_ID);
        /*Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);*/
    }
}
