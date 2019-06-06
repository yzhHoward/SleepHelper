package com.howard.sleephelper;

import android.app.Application;
import android.util.Log;

import com.howard.sleephelper.service.SensorService;
import com.howard.sleephelper.utils.ApkHelper;
import com.shihoo.daemon.DaemonEnv;

public class App extends Application {
    public void onCreate() {
        super.onCreate();
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        // 每一次创建进程的时候都需要对Daemon环境进行初始化，所以这里没有判断进程
        String processName = ApkHelper.getProcessName(this.getApplicationContext());

        if ("com.sleephelper.howard.sleephelper".equals(processName)) {
            // 主进程 进行一些其他的操作
            Log.e("app", "启动主进程");
        } else if ("com.sleephelper.howard.sleephelper:work".equals(processName)) {
            Log.e("app", "启动了工作进程");
        } else if ("com.sleephelper.howard.sleephelper:watch".equals(processName)) {
            DaemonEnv.mWorkServiceClass = SensorService.class;
            Log.e("app", "启动了守护进程");
        }
    }
}

