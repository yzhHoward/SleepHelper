package com.howard.sleephelper.drawChart;

import android.content.res.Resources;
import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.howard.sleephelper.R;
import com.howard.sleephelper.sleepRecord.RecordBean;

import java.util.ArrayList;

public class DrawPieChart {

    private PieChart mPieChart;
    private RecordBean mRecord;
    private Resources mResources;

    /**
     * 绘制饼状图
     *
     * @param pieChart   饼状图的对象
     * @param mRecord    睡眠记录
     * @param mResources 资源实例
     */
    public DrawPieChart(PieChart pieChart, RecordBean mRecord, Resources mResources) {
        mPieChart = pieChart;
        mPieChart.setNoDataText("睡眠时间太短啦！没有足够数据！");
        mPieChart.setNoDataTextColor(Color.WHITE);
        //画空心饼状图
        if (mRecord.getDrawChart()) {
            this.mResources = mResources;
            this.mRecord = mRecord;
            drawChart();
        } else {
            mPieChart.clear();
        }
    }

    public void setRecord(RecordBean mRecord) {
        this.mRecord = mRecord;
        if (mRecord.getDrawChart()) {
            drawChart();
        } else {
            mPieChart.clear();
        }
    }

    private void drawChart() {
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
        colors.add(mResources.getColor(R.color.Pie_Green));
        colors.add(mResources.getColor(R.color.Pie_Blue));
        colors.add(mResources.getColor(R.color.Pie_Yellow));
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
}
