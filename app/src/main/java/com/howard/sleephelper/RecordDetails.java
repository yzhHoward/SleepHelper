package com.howard.sleephelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.howard.sleephelper.sleepRecord.Bean;
import com.howard.sleephelper.sleepRecord.GetRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 详细的记录页面
 */
public class RecordDetails extends Activity {


    Button btn_left;
    Button btn_right;
    LineChart lineChart;
    PieChart mPieChart;
    TextView mDate;
    TextView mStartTime;
    TextView mStopTime;
    TextView mSleepTime;
    TextView mDeep;
    TextView mSwallow;
    TextView mDream;

    private int cur;
    private int idx;
    private int max;
    private int month;
    private int day;
    private float[][] record = new float[20][1200];
    private int[][] level = new int[20][3];
    private int[][] time = new int[20][1200];
    private int[][] specialTime = new int[20][4];
    private int[] size = new int[20];

    private boolean left_invisible;
    private boolean right_invisible;

    private List<Bean> records;
    private Bean mRecord;
    private GetRecord mGetRecord;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_details);
        lineChart = findViewById(R.id.lineChart);
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

        readLog();
        oldReadLog();
        max = cur - 1;
        idx = this.getIntent().getIntExtra("position", 0);
        cur = this.getIntent().getIntExtra("position", 0);
        initView();
        draw(cur);
        if (cur >= max) {
            right_invisible = true;
            btn_right.setVisibility(View.INVISIBLE);
        }
        if (cur == 0) {
            left_invisible = true;
            btn_left.setVisibility(View.INVISIBLE);
        }
    }

    protected void initView() {
        lineChart.setDrawBorders(false);
        lineChart.setNoDataText("睡眠时间太短啦！没有足够数据！");
        lineChart.setNoDataTextColor(Color.WHITE);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(getResources().getColor(R.color.transparent_gray));
        lineChart.setDragEnabled(true);
        lineChart.animateX(1000);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);
    }

    public void draw(int cur) {
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
        if (mRecord.getDrawChart()) {
            drawLineChart();
            drawPieChart();
        } else {
            lineChart.clear();
            mPieChart.clear();
        }
    }

    public String timeProcess(int target) {
        int Hour = target / 60;
        int Min = target % 60;
        if (Hour < 10) {
            if (Min < 10)
                return "0" + Hour + ":0" + Min;
            else
                return "0" + Hour + ":" + Min;
        } else {
            if (Min < 10)
                return Hour + ":0" + Min;
            else
                return Hour + ":" + Min;
        }
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
        draw(--cur);
        if (cur == 0) {
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
        draw(++cur);
        if (cur >= max) {
            right_invisible = true;
            btn_right.setVisibility(View.INVISIBLE);
        }
        if (left_invisible) {
            left_invisible = false;
            btn_left.setVisibility(View.VISIBLE);
        }
    }

    private void readLog() {
        records = mGetRecord.queryAllList();
        mRecord = records.get(idx);
        String arr[] = mRecord.getDate().split("-");
        month = Integer.parseInt(arr[0]);
        day = Integer.parseInt(arr[1]);
    }

    private void oldReadLog() {
        int aTime;
        String arr[];
        String stop = "Stop";
        String t = "t";
        String t0 = "start";
        String t1 = "stop";
        String t2 = "date";
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(
                    Environment.getExternalStorageDirectory().getPath() + "/sleep_record.log"));
            String line = bfr.readLine();
            while (line != null) {
                arr = line.split(" ");
                try {
                    record[cur][size[cur]] = Float.parseFloat(arr[1]);
                    if (record[cur][size[cur]] <= 0.37f)
                        ++level[cur][0];
                    else if (record[cur][size[cur]] <= 1.2f)
                        ++level[cur][1];
                    else
                        ++level[cur][2];
                    aTime = Integer.parseInt(arr[0]);
                    //if (size[cur] > 0)
                    time[cur][size[cur]] = aTime;
                    ++size[cur];
                } catch (Exception e) {
                    if (arr[1].equals(stop) && size[cur] > 0)
                        ++cur;
                    else if (arr[1].equals(t)) {
                        if (arr[2].equals(t2))
                            specialTime[cur][0] = Integer.parseInt(arr[3]);
                        else if (arr[2].equals(t0))
                            specialTime[cur][1] = Integer.parseInt(arr[3]);
                        else if (arr[2].equals(t1))
                            specialTime[cur][2] = Integer.parseInt(arr[3]);
                        else
                            specialTime[cur][3] = Integer.parseInt(arr[3]);
                    }
                }
                line = bfr.readLine();
            }
            bfr.close();
        } catch (IOException e) {
        }
    }

    //画折线图
    private void drawLineChart(final int times) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < size[times]; ++i) {
            entries.add(new Entry(time[times][i], record[times][i]));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setColor(getResources().getColor(R.color.Pie_Yellow));
        lineDataSet.setLineWidth(1.6f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        LineData data = new LineData(lineDataSet);
        data.setDrawValues(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8, false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            String xFormat;

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int IValue = (int) value;
                if (IValue % 60 < 10)
                    xFormat = IValue % 1440 / 60 + ":0" + IValue % 60;
                else
                    xFormat = IValue % 1440 / 60 + ":" + IValue % 60;
                return xFormat;
            }
        });
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        leftYAxis.setEnabled(true);
        rightYAxis.setEnabled(false);
        leftYAxis.setDrawGridLines(false);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });

        LimitLine limitLine = new LimitLine(0.37f, "深度睡眠");
        limitLine.setLineColor(getResources().getColor(R.color.Pie_Green));
        limitLine.setTextColor(getResources().getColor(R.color.Pie_Green));
        leftYAxis.addLimitLine(limitLine);

        LimitLine limitLine1 = new LimitLine(1.2f, "浅层睡眠");
        limitLine1.setLineColor(getResources().getColor(R.color.Pie_Blue));
        limitLine1.setTextColor(getResources().getColor(R.color.Pie_Blue));
        leftYAxis.addLimitLine(limitLine1);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        lineChart.setData(data);
        lineChart.invalidate();
    }

    //画空心饼状图
    protected void drawPieChart(final int times) {
        mPieChart.setCenterText("您的睡眠状态");
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(10, 10, 10, 5);
        mPieChart.setDrawCenterText(true);
        mPieChart.setCenterTextColor(Color.WHITE);
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
        entries.add(new PieEntry(level[times][0], "深度睡眠"));
        entries.add(new PieEntry(level[times][1], "浅层睡眠"));
        entries.add(new PieEntry(level[times][2], "醒/梦"));

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
    public void onDestroy() {
        super.onDestroy();
    }
}
