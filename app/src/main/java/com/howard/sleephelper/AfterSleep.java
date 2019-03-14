package com.howard.sleephelper;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.util.ArrayList;
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

    private Bean mRecord;
    private GetRecord mGetRecord;

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
            mPieChart.setNoDataText("睡眠时间太短啦！没有足够数据！");
            mPieChart.setNoDataTextColor(Color.WHITE);
            //画空心饼状图
            if (mRecord.getDrawChart()) {
                drawChart();
            }
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

    //画图的部分
    protected void drawChart() {
        mPieChart.setCenterText("您的睡眠状态");
        mPieChart.setCenterTextColor(Color.WHITE);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(10, 10, 10, 5);
        mPieChart.setDrawCenterText(true);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);
        mPieChart.setHoleRadius(58f);
        mPieChart.setHoleColor(Color.TRANSPARENT);
        mPieChart.setTransparentCircleRadius(61f);
        mPieChart.setRotationAngle(0);
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(false);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(mRecord.getDeepTime(), "深度睡眠"));
        entries.add(new PieEntry(mRecord.getSwallowTime(), "浅层睡眠"));
        entries.add(new PieEntry(mRecord.getAwakeTime(), "醒/梦"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        mPieChart.setEntryLabelColor(Color.WHITE);
        mPieChart.setEntryLabelTextSize(12f);
        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextColor(Color.WHITE);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.Pie_Green));
        colors.add(getResources().getColor(R.color.Pie_Blue));
        colors.add(getResources().getColor(R.color.Pie_Yellow));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mPieChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
