package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.howard.sleephelper.R;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class GoSleepService extends Service {
    boolean notification_on = false;
    NotificationManager mManager;
    Notification.Builder sleep;
    int hour;
    int min;
    boolean ifSleep = false;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (!ifSleep && !notification_on) {
                Calendar now = Calendar.getInstance();
                if ((now.get(Calendar.HOUR_OF_DAY)) > hour&&(now.get(Calendar.MINUTE))>min) {
                    mManager.notify(2, sleep.build());
                    notification_on = true;
                }
            }
        }
    };
    Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        GetRecord myGet=new GetRecord(this);
        String remind=myGet.getRemind();
        String[] res = remind.split(":");
        hour=Integer.parseInt(res[0]);
        min=Integer.parseInt(res[1]);
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sleep = createNotification();
        ifSleep = ifSleepToday();
        startForeground(3, createMyNotification().build());
        mManager.cancel(3);
        timer.schedule(timerTask, 0, 60000);
    }
    //此为开机自弃成功与否测试
    Notification.Builder createMyNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder
                .setContentTitle("睡眠助手")
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("3", "睡眠记录", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();
            channel.getAudioAttributes();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mManager.createNotificationChannel(channel);
            builder.setChannelId("3");
        }
        return builder;
    }

    /**创建通知及通知频道
     *
     * @return 通知构造器
     */
    Notification.Builder createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.sleep_1)
                .setContentTitle("睡眠助手")
                .setContentText("小助手提醒您该睡觉啦")
                .setDefaults(NotificationCompat.DEFAULT_ALL).setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "睡眠提醒", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();
            channel.getAudioAttributes();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mManager.createNotificationChannel(channel);
            builder.setChannelId("2");
        }
        return builder;
    }
    /**
     * 今日是否睡觉
     *
     * @return 已睡觉，未睡觉
    **/
    public boolean ifSleepToday() {
        Calendar calendar = Calendar.getInstance();
        return ((new GetRecord(this)).queryByDate(((calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH))).size() > 0);

    }

    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void onDestroy() {
        mManager.cancel(2);
        super.onDestroy();
    }
}
