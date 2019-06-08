package com.howard.sleephelper.recyclerView;

/**
 * 用来绘制记录的类
 */
public class Trace {

    private String Date;
    private String Time;

    public Trace(String mDate, String mTime) {
        this.Date = mDate;
        this.Time = mTime;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }
}
