package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.howard.sleephelper.R;

public class DaemonService extends Service {
    public static final int NOTICE_ID = 100;
    private NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化获取channel manager与notification构造器
     */
    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder sleep = createNotification();
        manager.notify(NOTICE_ID, sleep.build());
    }

    /**
     * 创建通知及通知频道
     *
     * @return 通知构造器
     */
    Notification.Builder createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.sleep_1)
                .setContentTitle("睡眠助手")
                .setContentText("正在记录您的睡眠...")
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "睡眠记录", NotificationManager.IMPORTANCE_LOW);
            channel.canBypassDnd();
            channel.getAudioAttributes();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            builder.setChannelId("1");
        }
        return builder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mManager != null) {
            mManager.cancel(NOTICE_ID);
        }
    }
}
