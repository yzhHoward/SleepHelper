package com.howard.sleephelper;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.howard.sleephelper.database.DaoMaster;
import com.howard.sleephelper.database.DaoSession;
import com.howard.sleephelper.service.SensorService;
import com.howard.sleephelper.utils.ApkHelper;
import com.shihoo.daemon.DaemonEnv;

import java.io.File;

public class App extends Application {
    public static DaoSession mDaoSession;

    public void onCreate() {
        /*
          此处为应用开始时分别为不同的进程赋值
          每一次创建进程的时候都需要对Daemon环境进行初始化，所以这里没有判断进程
         */
        super.onCreate();
        initGreenDao();

        String processName = ApkHelper.getProcessName(this.getApplicationContext());

        if ("com.sleephelper.howard.sleephelper".equals(processName)) {

            Log.e("app", "启动主进程");
        } else if ("com.sleephelper.howard.sleephelper:work".equals(processName)) {
            Log.e("app", "启动了工作进程");
        } else if ("com.sleephelper.howard.sleephelper:watch".equals(processName)) {
            DaemonEnv.mWorkServiceClass = SensorService.class;
            Log.e("app", "启动了守护进程");
        }
    }

    /**
     * 数据库初始化
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper
                (this, this.getExternalFilesDir(null) + File.separator + "sleepRecord.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }
}

