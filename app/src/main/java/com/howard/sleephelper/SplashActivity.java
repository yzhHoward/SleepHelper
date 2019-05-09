package com.howard.sleephelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * 整个程序的入口，splash界面
 */
public class SplashActivity extends Activity {
    private static final int DELAY_TIME = 1500;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景
        setContentView(R.layout.splash);

        myPermission();
        // 利用消息处理器实现延迟跳转到登录窗口
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                // 启动登录窗口
                startActivity(intent);
                // 关闭启动画面
                finish();
            }
        }, DELAY_TIME);

    }

    //检测是否有存储权限，没有申请提权
    public void myPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
            //没有权限则申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
