package com.howard.sleephelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.howard.sleephelper.service.GoSleepService;
import com.shihoo.daemon.DaemonEnv;

public class gosleepBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Toast.makeText(context, "提醒服务开始", Toast.LENGTH_SHORT).show();

            Intent ifsleepIntent = new Intent(context, GoSleepService.class);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            context.startForegroundService(ifsleepIntent);
            else
                context.startService(ifsleepIntent);
        }
    }
}
