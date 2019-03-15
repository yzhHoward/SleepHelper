package com.howard.sleephelper;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.howard.sleephelper.drawChart.DrawPieChart;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.util.Locale;
import java.util.Random;

/**
 * 结束记录的界面
 */
public class AfterSleep extends Activity {

    RelativeLayout background;
    TextView mTime;
    TextView mStartTime;
    TextView mStopTime;
    PieChart mPieChart;
    TextView mDeep;
    TextView mSwallow;
    TextView mDream;
    DrawPieChart mDrawPieChart;

    private Bean mRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.aftersleep);
        background = findViewById(R.id.aftersleep_background);
        mPieChart = findViewById(R.id.mPiechart);
        mTime = findViewById(R.id.sleepTime);
        mStartTime = findViewById(R.id.startTime);
        mStopTime = findViewById(R.id.stopTime);
        mDeep = findViewById(R.id.deep);
        mSwallow = findViewById(R.id.swallow);
        mDream = findViewById(R.id.dream);

        //随机背景
        int array[] = {R.drawable.bg_1, R.drawable.bg_4, R.drawable.bg_5};
        Random rnd = new Random();
        int index = rnd.nextInt(3);
        Resources resources = getBaseContext().getResources();
        Drawable cur = resources.getDrawable(array[index]);
        background.setBackground(cur);
        long recordId = this.getIntent().getLongExtra("recordId", 0);

        GetRecord mGetRecord = new GetRecord(this);
        mRecord = mGetRecord.getRecordById(recordId);

        initView();
    }

    //设置文本
    protected void initView() {
        if (mRecord != null) {
            mStartTime.setText("睡觉 " + mRecord.getStartTime());
            mStopTime.setText("起床 " + mRecord.getEndTime());
            mTime.setText(String.format(Locale.getDefault(), "时长 %02d:%02d",
                    mRecord.getTotalTime() / 60, mRecord.getTotalTime() % 60));
            mDeep.setText(String.format(Locale.getDefault(), "深度睡眠 %02d:%02d",
                    mRecord.getDeepTime() / 60, mRecord.getDeepTime() % 60));
            mSwallow.setText(String.format(Locale.getDefault(), "浅层睡眠 %02d:%02d",
                    mRecord.getSwallowTime() / 60, mRecord.getSwallowTime() % 60));
            mDream.setText(String.format(Locale.getDefault(), "醒/梦 %02d:%02d",
                    mRecord.getAwakeTime() / 60, mRecord.getAwakeTime() % 60));
            mDrawPieChart = new DrawPieChart(mPieChart, mRecord, getResources());
        } else {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
        }
    }

    public void AfterSleepClick(View v) {
        AfterSleep.this.finish();
    }

    //返回键效果
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AfterSleep.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
