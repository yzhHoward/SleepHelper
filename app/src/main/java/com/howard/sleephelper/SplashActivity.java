package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * 整个程序的入口，splash界面
 */
public class SplashActivity extends Activity {
    private static final int DELAY_TIME = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景
        setContentView(R.layout.splash);
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
}
