package com.howard.sleephelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howard.sleephelper.service.DaemonService;
import com.howard.sleephelper.service.GoSleepService;
import com.howard.sleephelper.service.GrayService;
import com.howard.sleephelper.service.MediaService;
import com.howard.sleephelper.service.PlayerMusicService;
import com.howard.sleephelper.service.SensorService;
import com.shihoo.daemon.DaemonEnv;
import com.shihoo.daemon.WatchProcessPrefHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * 记录睡眠的界面
 */
public class Sleep extends Activity {
    TextView clock;
    TextView musicTitle;
    RelativeLayout background;

    private Button playButton;

    private int runningTime;

    private Timer mRunTimer;

    private boolean playing;
    //private JobSchedulerManager mJobManager;
    private MediaService.MyBinder mMyBinder;
    // 连接音乐服务
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MediaService.MyBinder) service;
            mMyBinder.getMusicTitle(musicTitle);
            Log.e(TAG, "Service与Activity已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private BroadcastReceiver mScreenOReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null && action.equals("android.intent.action.SCREEN_ON")) {
                stopRunTimer();
                startRunTimer();
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep);
        //mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        //mJobManager.startJobScheduler();
        stopGoSleepService();

        initView();
        initMedia();
        initData();

        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        this.registerReceiver(mScreenOReceiver, mScreenOnFilter);
    }

    //开始记录数据
    public void initData() {
        runningTime = 0;
        startRunTimer();
        //startPlayMusicService();
        //stopGosleepService();
        startDaemonService();

        WatchProcessPrefHelper.setIsStartSDaemon(this, true);
        DaemonEnv.startServiceSafely(this, SensorService.class, false);
    }

    public void initMedia() {
        playing = false;
        Intent MediaServiceIntent = new Intent(this, MediaService.class);
        bindService(MediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    //界面初始化
    public void initView() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "clock_font.ttf");
        clock = findViewById(R.id.mRunTime);
        clock.setTypeface(typeface);
        musicTitle = findViewById(R.id.musicName);
        playButton = findViewById(R.id.play);

        //设置随机背景
        background = findViewById(R.id.sleep_background);
        int[] array = {R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_6, R.drawable.bg_7};
        Random rnd = new Random();
        int index = rnd.nextInt(4);
        Resources resources = getBaseContext().getResources();
        Drawable cur = resources.getDrawable(array[index]);
        background.setBackground(cur);
    }

    // 音乐播放按钮
    public void onClickMedia(View v) {
        runningTime = 0;
        switch (v.getId()) {
            case R.id.play:
                if (!playing) {
                    mMyBinder.playMusic();
                    Log.e(TAG, "Play music.");
                    playButton.setBackground(getResources().getDrawable(R.drawable.ic_play_btn_pause));
                } else {
                    mMyBinder.pauseMusic();
                    Log.e(TAG, "Pause music.");
                    playButton.setBackground(getResources().getDrawable(R.drawable.ic_play_btn_play));
                }
                playing = !playing;
                break;
            case R.id.next:
                mMyBinder.nextMusic();
                break;
            case R.id.previous:
                mMyBinder.previousMusic();
                break;
        }
    }

    // 停止记录按钮
    public void onRunningClick2(View v) {
        Toast.makeText(this, "停止记录！", Toast.LENGTH_SHORT).show();
        WatchProcessPrefHelper.setIsStartSDaemon(this, false);
        DaemonEnv.stopAllServices(this);
        stopRunTimer();
        stopDaemonService();
        //stopPlayMusicService();
        //stopGrayService();
        Intent i = new Intent();
        i.setClass(Sleep.this, AfterSleep.class);
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
    public void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            @Override
            public void run() {
                if (playing) {
                    ++runningTime;
                    if (runningTime > 15) {
                        mMyBinder.closeMedia();
                    }
                }
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
        Calendar calendar = Calendar.getInstance();
        clock.setText(String.format(Locale.getDefault(), "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        mRunTimer = new Timer();
        mRunTimer.schedule(mTask, 60000 - calendar.get(Calendar.SECOND) * 1000
                - calendar.get(Calendar.MILLISECOND), 60000);
    }

    //停止计时器
    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
    }

    //下面各种的保活策略，部分在sdk=25时有效，于是暂时去掉了
    private void startPlayMusicService() {
        Intent intent = new Intent(Sleep.this, PlayerMusicService.class);
        startService(intent);
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(Sleep.this, PlayerMusicService.class);
        stopService(intent);
    }

    private void startGoSleepService() {
        Intent intent = new Intent(Sleep.this, GoSleepService.class);
        startService(intent);
    }

    private void stopGoSleepService() {
        Intent intent = new Intent(Sleep.this, GoSleepService.class);
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
        this.unregisterReceiver(mScreenOReceiver);

        if (mMyBinder != null && playing) {
            mMyBinder.closeMedia();
        }
        unbindService(mServiceConnection);

        WatchProcessPrefHelper.setIsStartSDaemon(this, false);
        DaemonEnv.stopAllServices(this);
        stopRunTimer();
//        stopPlayMusicService();
        stopDaemonService();
        //startGosleepService();
        //mJobManager.stopJobScheduler();
    }
}
