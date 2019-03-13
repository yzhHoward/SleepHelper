package com.howard.sleephelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

/**
 * 整个程序的入口，splash界面
 */
public class SplashActivity extends Activity {
    private static final int GO_HOME = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景
        setContentView(R.layout.splash);
        //下面这些能让程序延时1.5秒
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GO_HOME) {
                    final Activity context = SplashActivity.this;
                    //下面3句是切换activity的基本步骤
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    context.finish();
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(GO_HOME, 1500);
    }
}
