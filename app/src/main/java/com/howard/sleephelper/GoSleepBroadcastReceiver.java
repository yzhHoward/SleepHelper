package com.howard.sleephelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.howard.sleephelper.service.GoSleepService;

public class GoSleepBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            Intent ifSleepIntent = new Intent(context, GoSleepService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(ifSleepIntent);
            } else {
                context.startService(ifSleepIntent);
            }
        }
    }
}
