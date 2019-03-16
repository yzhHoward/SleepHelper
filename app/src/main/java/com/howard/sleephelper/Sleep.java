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

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 记录睡眠的界面
 */
public class Sleep extends Activity {
    TextView clock;
    RelativeLayout background;

    private long startTime;

    private Timer mRunTimer;
    private Sensors sensor;
    private Bean mRecord;
    private GetRecord mGetRecord;
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
        clock = findViewById(R.id.mRunTime);
        clock.setTypeface(typeface);

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
        boolean restart;
        restart = this.getIntent().getBooleanExtra("restart", false);
        mGetRecord = new GetRecord(this);
        startRunTimer();
        Calendar calendar = Calendar.getInstance();
        //注意：若启用mJobManager则需要重新调整reStart内容
        if (restart) {
            mRecord = mGetRecord.getLatestRecord();
            sensor = new Sensors(this, mRecord);
            //startGrayService();
        } else {
            mRecord = mGetRecord.insertData(String.valueOf(calendar.get(Calendar.MONTH)) + "-"
                            + String.valueOf(calendar.get(Calendar.DATE)),
                    String.format(Locale.getDefault(), "%02d:%02d",
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            sensor = new Sensors(this, mRecord);
        }
        //startPlayMusicService();
//        startDaemonService();
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
            mGetRecord.finalUpdate(mRecord, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    calendar.getTimeInMillis() - startTime, deepTime, swallowTime, awakeTime);
        }
        stopRunTimer();
//        stopDaemonService();
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
    private void startRunTimer() {
        Calendar calendar = Calendar.getInstance();
        clock.setText(String.format(Locale.getDefault(), "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        TimerTask mTask = new TimerTask() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            @Override
            public void run() {
                ++minute;
                if (minute == 60) {
                    minute = 0;
                    hour++;
                }
                if (hour == 24) {
                    minute = 0;
                    hour = 0;
                }
                // 更新UI，有bug，需要重写
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clock.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                    }
                });
            }
        };
        mRunTimer = new Timer();
        mRunTimer.schedule(mTask, 60000 - calendar.get(Calendar.MINUTE) * 1000
                - calendar.get(Calendar.MILLISECOND), 60000);
    }

    //停止计时器
    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
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
//        stopDaemonService();
        //mJobManager.stopJobScheduler();
    }
}
