package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howard.sleephelper.sensors.Sensors;
import com.howard.sleephelper.service.DaemonService;
import com.howard.sleephelper.service.GrayService;
import com.howard.sleephelper.service.PlayerMusicService;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;
import com.howard.sleephelper.utils.JobSchedulerManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 记录睡眠的界面
 */
public class Sleep extends Activity {
    TextView Clock;
    RelativeLayout background;

    private int time = 0;
    private int timeHour;
    private int timeMin;
    private int timeSec;
    private long startTime;

    private Timer mRunTimer;
    private Sensors sensor;
    private Bean mRecord;
    private GetRecord newRecord;
    //private JobSchedulerManager mJobManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        //mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        //mJobManager.startJobScheduler();
        initView();
        startTime = initData();
    }

    //界面初始化
    public void initView() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "clock_font.ttf");
        Clock = findViewById(R.id.mRunTime);
        Clock.setTypeface(typeface);

        //设置随机背景
        background = findViewById(R.id.sleep_background);
        int array[] = {R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_6, R.drawable.bg_7};
        Random rnd = new Random();
        int index = rnd.nextInt(4);
        Resources resources = getBaseContext().getResources();
        Drawable cur = resources.getDrawable(array[index]);
        background.setBackground(cur);
    }

    //开始记录数据
    public long initData() {
        boolean reStart;
        newRecord = new GetRecord(this);
        Calendar calendar = Calendar.getInstance();
        mRecord = newRecord.insertData(String.valueOf(calendar.get(Calendar.MONTH)) + "-"
                        + String.valueOf(calendar.get(Calendar.DATE)),
                String.format(Locale.getDefault(), "%02d:%02d",
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        //TODO: 写一下reStart的情况（程序意外退出）
//        reStart = this.getIntent().getBooleanExtra("restart", false);
//        //注意：若启用mJobManager则需要重新调整reStart内容
//        if (!reStart) {
//            writeLog(" t date " + String.valueOf(timeMonth * 31 + timeDay));
//            writeLog(" t start " + String.valueOf(timeStart));
//            //startGrayService();
//        }
        sensor = new Sensors(this, mRecord);
        startRunTimer();
        //startPlayMusicService();
        startDaemonService();
        return calendar.getTimeInMillis();
    }

    //按钮
    public void onRunningClick2(View v) {
        Toast.makeText(this, "停止记录！", Toast.LENGTH_SHORT).show();
        int[] details = sensor.stopSensor();
        int deepTime = details[0];
        int swallowTime = details[1];
        int awakeTime = details[2];
        Calendar calendar = Calendar.getInstance();
        if (mRecord != null) {
            newRecord.finalUpdate(mRecord, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                    calendar.getTimeInMillis() - startTime, deepTime, swallowTime, awakeTime);
        }
        stopRunTimer();
        stopDaemonService();
        //stopPlayMusicService();
        //stopGrayService();
        Intent i = new Intent();
        i.setClass(Sleep.this, AfterSleep.class);
        i.putExtra("recordId", mRecord.getId());
        Sleep.this.startActivity(i);
        Sleep.this.finish();
    }

    //返回键效果
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(Sleep.this, "正在睡觉", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //计时器
    //TODO: 有问题，需要重写
    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                ++time;
                ++timeSec;
                if (timeSec == 60) {
                    timeSec = 0;
                    timeMin++;
                }
                if (timeMin == 60) {
                    timeMin = 0;
                    timeHour++;
                }
                if (timeHour == 24) {
                    timeSec = 0;
                    timeMin = 0;
                    timeHour = 0;
                }
                // 更新UI，有bug，需要重写
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (timeHour < 10) {
                            if (timeMin < 10)
                                Clock.setText(String.format("0" + timeHour + ":0" + timeMin));
                            else
                                Clock.setText(String.format("0" + timeHour + ":" + timeMin));
                        } else {
                            if (timeMin < 10)
                                Clock.setText(String.format(timeHour + ":0" + timeMin));
                            else
                                Clock.setText(String.format(timeHour + ":" + timeMin));
                        }
                    }
                });
            }
        };
        mRunTimer = new Timer();
        mRunTimer.schedule(mTask, 0, 1000);
    }

    //停止计时器
    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
        time = 0;
    }

    //下面是乱七八糟的保活策略，只能在sdk=26时有效，目前28没有效果可能还有bug
    private void startPlayMusicService() {
        Intent intent = new Intent(Sleep.this, PlayerMusicService.class);
        startService(intent);
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(Sleep.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(Sleep.this, DaemonService.class);
        startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(Sleep.this, DaemonService.class);
        stopService(intent);
    }

    private void startGrayService() {
        Intent intent = new Intent(Sleep.this, GrayService.class);
        startService(intent);
    }

    private void stopGrayService() {
        Intent intent = new Intent(Sleep.this, GrayService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensor.stopSensor();
        stopRunTimer();
//        stopPlayMusicService();
        stopDaemonService();
        //mJobManager.stopJobScheduler();
    }
}
