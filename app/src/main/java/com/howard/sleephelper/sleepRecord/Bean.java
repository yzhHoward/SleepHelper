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
    private String totalTime;
    private String deepTime;
    private String swallowTime;
    private String awakeTime;
    private String sleepDetail;//格式："dayOfYear*24*60+hour*60+minute 传感器参数,"
    @Generated(hash = 529597846)
    public Bean(Long id, String date, String startTime, String endTime,
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
