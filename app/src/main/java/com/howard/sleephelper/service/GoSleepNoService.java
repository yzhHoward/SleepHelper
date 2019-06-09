package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class GoSleepNoService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(201,new Notification());
        stopForeground(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O)manager.getNotificationChannel("3");
        manager.cancel(200);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
