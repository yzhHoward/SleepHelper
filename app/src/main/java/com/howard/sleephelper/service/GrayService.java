package com.howard.sleephelper.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GrayService extends Service {

    private final static int GRAY_SERVICE_ID = -1001;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 利用曾经的漏洞保活
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(GRAY_SERVICE_ID, new Notification());
        //stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
