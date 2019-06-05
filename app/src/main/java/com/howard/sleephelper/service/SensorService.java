package com.howard.sleephelper.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

import com.howard.sleephelper.sensors.Sensors;
import com.howard.sleephelper.sleepRecord.GetRecord;
import com.howard.sleephelper.sleepRecord.RecordBean;
import com.shihoo.daemon.AbsWorkService;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class SensorService extends AbsWorkService {

    private boolean IsShouldStopService;
    private Disposable mDisposable;
    private Sensors sensor;
    private RecordBean mRecord;
    private GetRecord mGetRecord;

    private boolean restart;
    private long startTime;

    /**
     * 是否任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return IsShouldStopService;
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return mDisposable != null && !mDisposable.isDisposed();
    }

    @Override
    public IBinder onBindService(Intent intent, Void v) {
        // 此处必须有返回，否则绑定无回调
        return new Messenger(new Handler()).getBinder();
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        if (sensor != null) {
            sensor.stopSensor();
        }
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        IsShouldStopService = true;
        //取消对任务的订阅
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        int[] details = sensor.stopSensor();
        int deepTime = details[0];
        int swallowTime = details[1];
        int awakeTime = details[2];
        Calendar calendar = Calendar.getInstance();
        if (mRecord != null) {
            mGetRecord.finalUpdate(mRecord, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    calendar.getTimeInMillis() - startTime, deepTime, swallowTime, awakeTime);
        }
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
        //注意：若启用mJobManager则需要重新调整reStart内容
        mGetRecord = new GetRecord(this);
        mRecord = mGetRecord.getLatestRecord();
        if (mRecord != null) {
            restart = !mRecord.getValid();
        }
        if (restart) {
            mRecord = mGetRecord.getLatestRecord();
            sensor = new Sensors(this, mRecord);
        } else {
            mRecord = mGetRecord.insertData((calendar.get(Calendar.MONTH) + 1) + "-"
                            + calendar.get(Calendar.DATE),
                    String.format(Locale.getDefault(), "%02d:%02d",
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            sensor = new Sensors(this, mRecord);
        }
        mDisposable = Observable
                .interval(3, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                    }
                });
    }
}
