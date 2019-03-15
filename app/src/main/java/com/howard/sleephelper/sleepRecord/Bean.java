package com.howard.sleephelper.sleepRecord;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Bean {
    @Id(autoincrement = true)
    private Long id;
    private String date;//格式：yyyy:mm:dd
    private String startTime;//格式：hh:mm
    private String endTime;
    private int totalTime;
    private boolean drawChart;
    private int deepTime;
    private int swallowTime;
    private int awakeTime;
    private String sleepDetail;//格式："dayOfYear*24*60+hour*60+minute 传感器参数,"
    private boolean valid;
    @Generated(hash = 60515970)
    public Bean(Long id, String date, String startTime, String endTime,
            int totalTime, boolean drawChart, int deepTime, int swallowTime,
            int awakeTime, String sleepDetail, boolean valid) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalTime = totalTime;
        this.drawChart = drawChart;
        this.deepTime = deepTime;
        this.swallowTime = swallowTime;
        this.awakeTime = awakeTime;
        this.sleepDetail = sleepDetail;
        this.valid = valid;
    }
    @Generated(hash = 80546095)
    public Bean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public int getTotalTime() {
        return this.totalTime;
    }
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
    public boolean getDrawChart() {
        return this.drawChart;
    }
    public void setDrawChart(boolean drawChart) {
        this.drawChart = drawChart;
    }
    public int getDeepTime() {
        return this.deepTime;
    }
    public void setDeepTime(int deepTime) {
        this.deepTime = deepTime;
    }
    public int getSwallowTime() {
        return this.swallowTime;
    }
    public void setSwallowTime(int swallowTime) {
        this.swallowTime = swallowTime;
    }
    public int getAwakeTime() {
        return this.awakeTime;
    }
    public void setAwakeTime(int awakeTime) {
        this.awakeTime = awakeTime;
    }
    public String getSleepDetail() {
        return this.sleepDetail;
    }
    public void setSleepDetail(String sleepDetail) {
        this.sleepDetail = sleepDetail;
    }
    public boolean getValid() {
        return this.valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
