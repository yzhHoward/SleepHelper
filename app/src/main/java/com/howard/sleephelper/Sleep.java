package com.howard.sleephelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howard.sleephelper.service.DaemonService;
import com.howard.sleephelper.service.GrayService;
import com.howard.sleephelper.service.PlayerMusicService;
import com.howard.sleephelper.utils.JobSchedulerManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
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
    private int timeStop;
    private int[] record = new int[6];
    private int size = 0;
    float k = 0;
    private int timeMonth;
    private int timeDay;
    private int timeHour;
    private int timeMin;
    private int timeSec;

    private SensorManager mSensorManager;
    private Sensor Accelerometer;
    private Sensor Gyroscope;
    private Timer mRunTimer;

    //private JobSchedulerManager mJobManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);

        //mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        //mJobManager.startJobScheduler();

        initView();
        initData();
    }

    //界面初始化
    public void initView() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "clock_font.ttf");
        Clock = findViewById(R.id.mRunTime);
        Clock.setTypeface(typeface);

        background = findViewById(R.id.sleep_background);
        int array[] = {R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_6, R.drawable.bg_7};
        Random rnd = new Random();
        int index = rnd.nextInt(4);
        Resources resources = getBaseContext().getResources();
        Drawable cur = resources.getDrawable(array[index]);
        background.setBackground(cur);
    }

    //开始记录数据
    public void initData() {
        int timeStart;
        boolean reStart;
        Calendar calendar = Calendar.getInstance();
        timeMonth = calendar.get(Calendar.MONTH) + 1;
        timeDay = calendar.get(Calendar.DATE);
        timeHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeMin = calendar.get(Calendar.MINUTE);
        timeSec = calendar.get(Calendar.SECOND);
        timeStart = timeHour * 60 + timeMin;
        reStart = this.getIntent().getBooleanExtra("restart", false);
        //注意：若启用mJobManager则需要重新调整reStart内容
        if (!reStart) {
            writeLog(" t date " + String.valueOf(timeMonth * 31 + timeDay));
            writeLog(" t start " + String.valueOf(timeStart));
            //startGrayService();
        }
        getSensorManager();
        startSensor();
        startRunTimer();
        //startPlayMusicService();
        startDaemonService();
        record[1] = timeStart;
    }

    //按钮
    public void onRunningClick2(View v) {
        Toast.makeText(this, "停止记录！", Toast.LENGTH_SHORT).show();
        stopSensor();
        Calendar calendar = Calendar.getInstance();
        timeStop = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        record[2] = timeStop;
        writeLog(" t time " + String.valueOf(time / 60));
        writeLog(" t stop " + String.valueOf(timeStop));
        writeLog(" Stop");
        record[0] = time / 60;
        stopRunTimer();
        //stopPlayMusicService();
        stopDaemonService();
        //stopGrayService();
        Intent i = new Intent();
        i.setClass(Sleep.this, AfterSleep.class);
        i.putExtra("record", record);
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

    //传感器，下面的都是垃圾算法
    private SensorEventListener listener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x, y, z;
            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                ++size;
                k += (float) Math.sqrt(x * x + y * y + z * z);
                if (size == 600) {
                    size = 0;
                    k /= 3f;
                    if (k > 1.0f)
                        k = k / 100 + 1.2f;
                    if (k >= 0.8f)
                        ++record[5];
                    else if (k >= 0.37f)
                        ++record[4];
                    else
                        ++record[3];
                    if (k >= 10)
                        k /= 10;
                    writeLog(" " + k);
                    k = 0;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                ++size;
                k += (float) Math.sqrt(x * x + y * y + z * z);
                if (size == 600) {
                    size = 0;
                    k /= 3f;
                    if (k > 1.0f)
                        k = k / 100 + 1.2f;
                    if (k >= 0.8f)
                        ++record[5];
                    else if (k >= 0.37f)
                        ++record[4];
                    else
                        ++record[3];
                    if (k >= 10)
                        k /= 10;
                    writeLog(" " + k);
                    k = 0;
                }
            }
        }
    };

    //计时器
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

    //写日志，需要用数据库实现，目前是写了个txt
    private void writeLog(String msg) {
        int TimeDay;
        int TimeHour;
        int TimeMin;
        Calendar calendar = Calendar.getInstance();
        TimeDay = calendar.get(Calendar.DAY_OF_MONTH);
        TimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        TimeMin = calendar.get(Calendar.MINUTE);
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter
                    (Environment.getExternalStorageDirectory().getPath() + "/sleep_record.log", true));
            bfw.write(String.valueOf(TimeDay * 1440 + TimeHour * 60 + TimeMin) + msg);
            bfw.newLine();
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下面都是传感器，可以放到其他文件里，一class到底真的难看
    private void getSensorManager() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void startSensor() {
        if (mSensorManager != null) {
            mSensorManager.registerListener(listener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(listener, Gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void stopSensor() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(listener);
        }
        size = 0;
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
        /*if (mSensorManager != null) {
            writeLog(" t time " + String.valueOf(time / 60));
            Calendar calendar = Calendar.getInstance();
            timeStop = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            writeLog(" t stop " + String.valueOf(timeStop));
            writeLog(" Stop");
        }*/
        stopSensor();
        stopRunTimer();
        stopPlayMusicService();
        stopDaemonService();
        //mJobManager.stopJobScheduler();
        super.onDestroy();
    }
}
