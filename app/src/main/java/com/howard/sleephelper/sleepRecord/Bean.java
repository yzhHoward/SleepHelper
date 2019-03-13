package com.howard.sleephelper.sleepRecord;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Bean {
    int id;
    String date;//格式：yyyy:mm:dd
    String startTime;//格式：hh:mm
    String endTime;
    String totalTime;
    String deepTime;
    String swallowTime;
    String awakeTime;
    String sleepDetail;//格式："dayOfYear*24*60+hour*60+minute 传感器参数,"
    @Generated(hash = 33248262)
    public Bean(int id, String date, String startTime, String endTime,
            String totalTime, String deepTime, String swallowTime, String awakeTime,
            String sleepDetail) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalTime = totalTime;
        this.deepTime = deepTime;
        this.swallowTime = swallowTime;
        this.awakeTime = awakeTime;
        this.sleepDetail = sleepDetail;
    }
    @Generated(hash = 80546095)
    public Bean() {
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
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
    public String getTotalTime() {
        return this.totalTime;
    }
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    public String getDeepTime() {
        return this.deepTime;
    }
    public void setDeepTime(String deepTime) {
        this.deepTime = deepTime;
    }
    public String getSwallowTime() {
        return this.swallowTime;
    }
    public void setSwallowTime(String swallowTime) {
        this.swallowTime = swallowTime;
    }
    public String getAwakeTime() {
        return this.awakeTime;
    }
    public void setAwakeTime(String awakeTime) {
        this.awakeTime = awakeTime;
    }
    public String getSleepDetail() {
        return this.sleepDetail;
    }
    public void setSleepDetail(String sleepDetail) {
        this.sleepDetail = sleepDetail;
    }
}
