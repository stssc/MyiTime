package com.example.myitime.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class Day implements Serializable {
    private byte[] picture;
    private String title,remark;
    private Calendar time;
    private int period;
    private HashMap<String,Boolean> labels;

    public Day(byte[] picture, String title, String remark, Calendar time,int period,HashMap<String,Boolean> labels) {
        this.picture = picture;
        this.title = title;
        this.remark = remark;
        this.time = time;
        this.period=period;
        this.labels=labels;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public HashMap<String, Boolean> getLabels() {
        return labels;
    }

    public void setLabels(HashMap<String,Boolean> labels) {
        this.labels = labels;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
