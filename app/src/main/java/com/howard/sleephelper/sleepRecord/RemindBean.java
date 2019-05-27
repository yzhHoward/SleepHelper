package com.howard.sleephelper.sleepRecord;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class RemindBean {
    @Id(autoincrement = true)
    private Long id;
    private String time;

    @Generated(hash = 1395260710)
    public RemindBean(Long id, String time) {
        this.id = id;
        this.time = time;
    }

    @Generated(hash = 1914622572)
    public RemindBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
