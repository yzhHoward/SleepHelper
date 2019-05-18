package com.howard.sleephelper.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MediaService extends Service {
    private static final String TAG = "MediaService";
    //初始化MediaPlayer
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private MyBinder mBinder = new MyBinder();
    //标记当前歌曲的序号
    private int i = 0;
    //这里要是路径有问题，就加上getAbsolutePath()，像下面这样
    //Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sounds/a1.mp3",
    //歌曲路径
    private String[] musicPath = getMusic();

    public String[] getMusic() {
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
        return (String[]) files_array.toArray();
    }

    private void initMusic(int idx) {
        //获取文件路径
        try {
            //此处的两个方法需要捕获IO异常
            //设置音频文件到MediaPlayer对象中
            mMediaPlayer.setDataSource(musicPath[idx]);
            //让MediaPlayer对象准备
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.d(TAG, "设置资源，准备阶段出错");
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        /**
         * 播放音乐
         */
        public void playMusic() {
            if (!mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.start();
            }
        }

        /**
         * 暂停播放
         */
        public void pauseMusic() {
            if (mMediaPlayer.isPlaying()) {
                //如果还没开始播放，就开始
                mMediaPlayer.pause();
            }
        }

        /**
         * 下一首
         */
        public void nextMusic() {
            if (mMediaPlayer != null && musicPath.length != 0) {
                //切换歌曲reset()很重要很重要很重要，没有会报IllegalStateException
                mMediaPlayer.reset();
                i += 1;
                if (i == musicPath.length) {
                    i = 0;
                }
                initMusic(i);
                playMusic();
            } else {
                Toast.makeText(getApplicationContext(), "未找到音乐", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 上一首
         */
        public void previousMusic() {
            if (mMediaPlayer != null && musicPath.length != 0) {
                mMediaPlayer.reset();
                i -= 1;
                if (i == 0) {
                    i = musicPath.length;
                }
                initMusic(i);
                playMusic();
            } else {
                Toast.makeText(getApplicationContext(), "未找到音乐", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 关闭播放器
         */
        public void closeMedia() {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
    }
}
