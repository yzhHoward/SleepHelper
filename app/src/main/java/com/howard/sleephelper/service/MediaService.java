package com.howard.sleephelper.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "MediaService";
    //初始化MediaPlayer
    public MediaPlayer mMediaPlayer;
    private MyBinder mBinder = new MyBinder();
    //标记当前歌曲的序号
    private int currentPosition = 0;
    //这里要是路径有问题，就加上getAbsolutePath()，像下面这样
    //Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sounds/a1.mp3",
    //歌曲路径
    private String[] musicPath;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPath = getMusic();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public String[] getMusic() {
        // player.setOnCompletionListener(new InnerOnCompletionListener());
        AssetManager assetManager = getAssets();
        String[] tmp_files = null;
        try {
            tmp_files = assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> files_array = new ArrayList<>();
        if (tmp_files != null) {
            for (String str : tmp_files) {
                if (str.endsWith(".mp3")) {
                    files_array.add(str);
                }
            }
        }
        return files_array.toArray(new String[0]);
    }

    private void changeMusic() {
        if (currentPosition < 0) {
            currentPosition = musicPath.length - 1;
        } else if (currentPosition > musicPath.length - 1) {
            currentPosition = 0;
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            AssetFileDescriptor fd = getAssets().openFd(musicPath[currentPosition]);
            // 切歌之前先重置，释放掉之前的资源
            mMediaPlayer.reset();
            // 设置播放源
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.setOnPreparedListener(this);
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        ++currentPosition;
        changeMusic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            // 开始播放
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyBinder extends Binder {

        // 播放音乐
        public void playMusic() {
            /*if (!mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.start();
            }*/
            changeMusic();
        }

        // 暂停
        public void pauseMusic() {
            if (mMediaPlayer.isPlaying()) {
                // 如果还没开始播放，就开始
                mMediaPlayer.pause();
            }
        }

        // 下一首
        public void nextMusic() {
            if (mMediaPlayer != null && musicPath.length != 0) {
                currentPosition += 1;
                changeMusic();
            } else {
                Toast.makeText(getApplicationContext(), "未找到音乐", Toast.LENGTH_SHORT).show();
            }
        }

        // 上一首
        public void previousMusic() {
            if (mMediaPlayer != null && musicPath.length != 0) {
                currentPosition -= 1;
                changeMusic();
            } else {
                Toast.makeText(getApplicationContext(), "未找到音乐", Toast.LENGTH_SHORT).show();
            }
        }

        // 关闭播放器
        public void closeMedia() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }
}
