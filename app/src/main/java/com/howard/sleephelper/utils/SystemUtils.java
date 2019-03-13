package com.howard.sleephelper.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class SystemUtils {

    public static boolean isAPPALive(Context mContext, String packageName) {
        boolean isAPPRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (packageName.equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }
}
