package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.howard.sleephelper.R;
import com.howard.sleephelper.Record;
import com.howard.sleephelper.sleepRecord.GetRecord;
import com.howard.sleephelper.sleepRecord.RecordBean;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoSleepService extends Service {
    public static final int NOTICE_ID = 101;
    int hour;
    int min;
    boolean notification_on = false;
    String time;
    NotificationManager mManager;
    Notification.Builder sleep;
    boolean ifsleep = false;

    @Override
    public void onCreate() {

        super.onCreate();
        /*time = new GetRecord(this).get();
        String[] strs = time.split(":");
        hour = Integer.parseInt(strs[0]);*/
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sleep = createNotification();
        ifsleep = ifSleepToday();
        startForeground(3, createNotificationtmp().build());
        mManager.cancel(3);
        timer.schedule(timerTask, 0, 500);
    }

    Notification.Builder createNotificationtmp() {
        Notification.Builder builder = new Notification.Builder(this);
        builder
                .setContentTitle("睡眠助手")

                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("3", "gosleepmsg", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();
            channel.getAudioAttributes();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mManager.createNotificationChannel(channel);
            builder.setChannelId("3");
        }
        return builder;
    }

    Notification.Builder createNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.sleep_1)
                .setContentTitle("睡眠助手")
                .setContentText("您该睡觉啦")
                .setDefaults(NotificationCompat.DEFAULT_ALL).setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "gosleepmsg", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();
            channel.getAudioAttributes();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mManager.createNotificationChannel(channel);
            builder.setChannelId("2");
        }
        return builder;
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (!ifsleep && !notification_on) {
                Calendar now = Calendar.getInstance();
                if ((now.get(Calendar.HOUR_OF_DAY)) > 14) {
                    mManager.notify(2, sleep.build());
                    notification_on = true;
                }
            }
        }
    };

    public boolean ifSleepToday() {
        //GetRecord myRecord=new GetRecord(this);
        /*List<RecordBean> allRecord=myRecord.queryAllList();
        if (allRecord.isEmpty()||allRecord.size()<=1)
            return  false;
        RecordBean lastRecord=allRecord.get(allRecord.size()-1);
        String arr[] = lastRecord.getDate().split("-");
        int month = Integer.parseInt(arr[0]);
        int day = Integer.parseInt(arr[1]);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH)!=day||calendar.get(Calendar.MONTH)!=month)
        {
            return false;
        }
        else
            return true;
           */
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
