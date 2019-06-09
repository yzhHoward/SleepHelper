package com.howard.sleephelper.drawChart;

import android.content.res.Resources;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.howard.sleephelper.R;
import com.howard.sleephelper.database.RecordBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DrawLineChart {
    private LineChart mLineChart;
    private RecordBean mRecord;
    private Resources mResources;
    private ArrayList<Integer> times;
    private ArrayList<Float> sleepDetails;
    private List<Entry> entries = new ArrayList<>();

    /**
     * 绘制折线图
     *
     * @param lineChart  折线图元素
     * @param mRecord    睡眠记录
     * @param mResources 绘图资源
     */
    public DrawLineChart(LineChart lineChart, RecordBean mRecord, Resources mResources) {
        mLineChart = lineChart;
        mLineChart.setDrawBorders(false);
        mLineChart.setNoDataText("睡眠时间太短啦！没有足够数据！");
        mLineChart.setNoDataTextColor(Color.WHITE);
        mLineChart.setDrawGridBackground(true);
        mLineChart.setGridBackgroundColor(mResources.getColor(R.color.transparent_gray));
        mLineChart.setDragEnabled(true);
        mLineChart.animateX(1000);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.getDescription().setEnabled(false);
        //画折线图
        if (mRecord.getDrawChart()) {
            this.mResources = mResources;
            this.mRecord = mRecord;
            readRecordDetails();
            drawChart();
        } else {
            mLineChart.clear();
        }
    }

    public void setRecord(RecordBean mRecord) {
        this.mRecord = mRecord;
        if (mRecord.getDrawChart()) {
            readRecordDetails();
            drawChart();
        } else {
            mLineChart.clear();
        }
    }

    private void readRecordDetails() {
        String[] arr = mRecord.getSleepDetail().split(" ");
        String[] buf;
        for (String e : arr) {
            buf = e.split(",");
            entries.add(new Entry(Integer.parseInt(buf[0]), Float.parseFloat(buf[1])));
        }
    }

    private void drawChart() {
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setColor(mResources.getColor(R.color.Pie_Yellow));
        lineDataSet.setLineWidth(1.6f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        LineData data = new LineData(lineDataSet);
        data.setDrawValues(false);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8, false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int IValue = (int) value;
                return String.format(Locale.getDefault(), "%02d:%02d", IValue % 1440 / 60, IValue % 60);
            }
        });
        YAxis leftYAxis = mLineChart.getAxisLeft();
        YAxis rightYAxis = mLineChart.getAxisRight();
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
        limitLine.setLineColor(mResources.getColor(R.color.Pie_Green));
        limitLine.setTextColor(mResources.getColor(R.color.Pie_Green));
        leftYAxis.addLimitLine(limitLine);

        LimitLine limitLine1 = new LimitLine(0.8f, "浅层睡眠");
        limitLine1.setLineColor(mResources.getColor(R.color.Pie_Blue));
        limitLine1.setTextColor(mResources.getColor(R.color.Pie_Blue));
        leftYAxis.addLimitLine(limitLine1);

        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);

        mLineChart.setData(data);
        mLineChart.invalidate();
    }
}
