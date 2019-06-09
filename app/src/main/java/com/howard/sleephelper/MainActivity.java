package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.howard.sleephelper.database.GetRecord;
import com.howard.sleephelper.database.RecordBean;

import java.util.Random;

import static com.howard.sleephelper.database.GetRecord.getRecord;

/**
 * 一开始的主界面
 */
public class MainActivity extends Activity {

    private long exitTime = 0;


    //初始化
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readLog();
        initView();

    }

    //设置随机背景
    public void initView() {
        ImageView mImageText;
        mImageText = findViewById(R.id.imageText);
        int[] array = {R.drawable.main_bg_1, R.drawable.main_bg_2, R.drawable.main_bg_3,
                R.drawable.main_bg_4, R.drawable.main_bg_5, R.drawable.main_bg_6};
        Random rnd = new Random();
        int index = rnd.nextInt(6);
        Resources resources = getBaseContext().getResources();
        Drawable cur = resources.getDrawable(array[index]);
        mImageText.setBackground(cur);
    }

    //按钮
    public void onRunningClick1(View v) {
        Toast.makeText(this, "开始记录！", Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.setClass(MainActivity.this, Sleep.class);
        MainActivity.this.startActivity(i);
        MainActivity.this.finish();
    }

    //按钮
    public void ClickRecord(View v) {
        Intent i = new Intent();
        i.setClass(MainActivity.this, CalendarPage.class);
        MainActivity.this.startActivity(i);
        MainActivity.this.finish();
    }

    //按下返回键的效果
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //读睡眠记录，判断是否异常退出
    private void readLog() {
        RecordBean mRecord;
        GetRecord mGetRecord = getRecord();
        mRecord = mGetRecord.getLatestRecord();
        if (mRecord != null) {
            if (!mRecord.getValid()) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, Sleep.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
