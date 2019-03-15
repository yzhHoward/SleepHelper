package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.howard.sleephelper.drawChart.DrawLineChart;
import com.howard.sleephelper.drawChart.DrawPieChart;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 详细的记录页面
 */
public class RecordDetails extends Activity {

    Button btn_left;
    Button btn_right;
    TextView mDate;
    TextView mStartTime;
    TextView mStopTime;
    TextView mSleepTime;
    TextView mDeep;
    TextView mSwallow;
    TextView mDream;
    LineChart mLineChart;
    PieChart mPieChart;
    DrawPieChart mDrawPieChart;
    DrawLineChart mDrawLineChart;

    private int idx;
    private int max;
    private int month;
    private int day;

    private boolean left_invisible;
    private boolean right_invisible;

    private List<Bean> records;
    private Bean mRecord;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_details);
        mLineChart = findViewById(R.id.lineChart);
        mPieChart = findViewById(R.id.mPiechart);
        btn_left = findViewById(R.id.left);
        btn_right = findViewById(R.id.right);
        mDate = findViewById(R.id.date);
        mStartTime = findViewById(R.id.startTime);
        mStopTime = findViewById(R.id.stopTime);
        mSleepTime = findViewById(R.id.sleepTime);
        mDeep = findViewById(R.id.deep);
        mSwallow = findViewById(R.id.swallow);
        mDream = findViewById(R.id.dream);

        idx = this.getIntent().getIntExtra("position", 0);
        readLog();
        setText();
        initView();
    }

    private void initView() {
        mDrawPieChart = new DrawPieChart(mPieChart, mRecord, getResources());
        mDrawLineChart = new DrawLineChart(mLineChart, mRecord, getResources());
        if (idx >= max) {
            right_invisible = true;
            btn_right.setVisibility(View.INVISIBLE);
        }
        if (idx == 0) {
            left_invisible = true;
            btn_left.setVisibility(View.INVISIBLE);
        }
    }

    private void setText() {
        mDate.setText(String.format(Locale.getDefault(), "%d月%d日", month, day));
        mStartTime.setText("睡觉 " + mRecord.getStartTime());
        mStopTime.setText("起床 " + mRecord.getEndTime());
        mSleepTime.setText(String.format(Locale.getDefault(), "时长 %02d:%02d",
                mRecord.getTotalTime() / 60, mRecord.getTotalTime() % 60));
        mDeep.setText(String.format(Locale.getDefault(), "深度睡眠 %02d:%02d",
                mRecord.getDeepTime() / 60, mRecord.getDeepTime() % 60));
        mSwallow.setText(String.format(Locale.getDefault(), "浅层睡眠 %02d:%02d",
                mRecord.getSwallowTime() / 60, mRecord.getSwallowTime() % 60));
        mDream.setText(String.format(Locale.getDefault(), "醒/梦 %02d:%02d",
                mRecord.getAwakeTime() / 60, mRecord.getAwakeTime() % 60));
    }

    //左上的返回
    public void ClickBackDetails(View v) {
        Intent i = new Intent();
        i.setClass(RecordDetails.this, Record.class);
        RecordDetails.this.startActivity(i);
        RecordDetails.this.finish();
    }

    //向左切换睡眠记录
    public void ClickLeft(View v) {
        mRecord = records.get(--idx);
        setText();
        mDrawLineChart.setRecord(mRecord);
        mDrawPieChart.setRecord(mRecord);
        if (idx == 0) {
            left_invisible = true;
            btn_left.setVisibility(View.INVISIBLE);
        }
        if (right_invisible) {
            right_invisible = false;
            btn_right.setVisibility(View.VISIBLE);
        }
    }

    //向右切换睡眠记录
    public void ClickRight(View v) {
        mRecord = records.get(++idx);
        setText();
        mDrawLineChart.setRecord(mRecord);
        mDrawPieChart.setRecord(mRecord);
        if (idx >= max) {
            right_invisible = true;
            btn_right.setVisibility(View.INVISIBLE);
        }
        if (left_invisible) {
            left_invisible = false;
            btn_left.setVisibility(View.VISIBLE);
        }
    }

    private void readLog() {
        GetRecord mGetRecord = new GetRecord(this);
        records = mGetRecord.queryAllList();
        Collections.reverse(records);
        mRecord = records.get(idx);
        max = records.size();
        String arr[] = mRecord.getDate().split("-");
        month = Integer.parseInt(arr[0]);
        day = Integer.parseInt(arr[1]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
